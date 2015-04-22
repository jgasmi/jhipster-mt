package com.yjiky.mt.multitenancy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yjiky.mt.domain.Tenant;
import com.yjiky.mt.domain.util.UtilValidator;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionProviderFactory implements ITenantAwareConnectionProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionProviderFactory.class);
    private static ConnectionProviderFactory instance;
    public static final String DEFAULT_LANDLORD = "DEFAULT";

    private static Map<Object, ConnectionProviderHolder> tenantIdToConnectionProviderHolderMap = null;
    private static Map<Object, ConnectionProvider> tenantIdToConnectionProviderMap = null;
    private static Map<String, Tenant> tenantMap = new ConcurrentHashMap<>();

    private Map cfgSettings;
    private ConnectionProvider defaultConnectionProvider;
    private ServiceRegistryImplementor serviceRegistry;

    public static ConnectionProviderFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionProviderFactory();
        }
        return instance;
    }

    private ConnectionProviderHolder createConnectionProviderHolder(Tenant tenant) {
        logger.debug("Going to create a brand new connection provider for tenant " + tenant.getId());
        Preconditions.checkNotNull(tenant);
        C3P0ConnectionProvider dataSource = new C3P0ConnectionProvider();
        dataSource.injectServices(serviceRegistry);
        dataSource.configure(getSettingsForTenant(cfgSettings, tenant));

        ConnectionProviderHolder dataSourceHolder = new ConnectionProviderHolder(dataSource, tenant.getDbName(), tenant.getDbUserName(), tenant.getDbPassword());
        return dataSourceHolder;
    }

    private ConnectionProvider createLandlordConnectionProvider() {
        logger.debug("Going to create a brand new connection provider for landlord ");
        Map originalSettings = serviceRegistry.getService(ConfigurationService.class).getSettings();

        C3P0ConnectionProvider connectionProvider = new C3P0ConnectionProvider();
        connectionProvider.injectServices(serviceRegistry);
        connectionProvider.configure(getOriginalLandlordSettings(originalSettings));

        return connectionProvider;
    }

    private Map getSettingsForTenant(Map cfgSettings, Tenant tenant) {
        Map tenantSettings = new HashMap();
        tenantSettings.putAll(cfgSettings);

        String url = getTenantDbUrl(tenant);
        String driver = "org.postgresql.Driver";
        String password = tenant.getDbPassword();
        String username = tenant.getDbUserName();

        if (StringUtils.isBlank(url)) {
            //throw new UnknownTenantException("The tenant '" + tenantIdentifier + "' is not known");
        }

        if (StringUtils.isBlank(driver) || StringUtils.isBlank(password) || StringUtils.isBlank(username)) {
            //throw new InvalidTenantConfigurationException("The tenant '" + tenantIdentifier + "' is not known");
        }

        tenantSettings.remove(Environment.DRIVER);
        tenantSettings.put(Environment.DRIVER, driver);
        // TODO For now only a single dialect is supported for all the tenants
        tenantSettings.remove(Environment.URL);
        tenantSettings.put(Environment.URL, url);
        tenantSettings.remove(Environment.PASS);
        tenantSettings.put(Environment.PASS, password);
        tenantSettings.remove(Environment.USER);
        tenantSettings.put(Environment.USER, username);

        return tenantSettings;
    }

    private Map getOriginalLandlordSettings(Map originalSettings) {
        Map landlordSettings = new HashMap();
        HikariDataSource hikariDataSource = (HikariDataSource) originalSettings.get("hibernate.connection.datasource");
        Properties properties = hikariDataSource.getDataSourceProperties();

        String driver = "org.postgresql.Driver";

        landlordSettings.remove(Environment.DRIVER);
        landlordSettings.put(Environment.DRIVER, driver);
        // TODO For now only a single dialect is supported for all the tenants
        landlordSettings.remove(Environment.URL);
        landlordSettings.put(Environment.URL, "jdbc:postgresql://" + properties.getProperty("serverName") + ":5432/" + properties.getProperty("databaseName"));
        landlordSettings.remove(Environment.PASS);
        landlordSettings.put(Environment.PASS, properties.getProperty("password"));
        landlordSettings.remove(Environment.USER);
        landlordSettings.put(Environment.USER, properties.getProperty("user"));

        return landlordSettings;
    }

    @Override
    public Map<Object, ConnectionProviderHolder> bootstrapLandlordConnectionProviders(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.cfgSettings = serviceRegistry.getService(ConfigurationService.class).getSettings();

        Map<Object, ConnectionProviderHolder> tenantIdToConnectionProviderHolderMap = Maps.newHashMap();
        Map<Object, ConnectionProvider> tenantIdToConnectionProviderMap = Maps.newHashMap();

        if (defaultConnectionProvider == null)
            defaultConnectionProvider = createLandlordConnectionProvider();

        ConnectionProviderHolder defaultConnectionProviderHolder = new ConnectionProviderHolder(defaultConnectionProvider, "", "root", "");
        tenantIdToConnectionProviderHolderMap.put(DEFAULT_LANDLORD, defaultConnectionProviderHolder);
        tenantIdToConnectionProviderMap.put(DEFAULT_LANDLORD, defaultConnectionProvider);
        this.tenantIdToConnectionProviderHolderMap = tenantIdToConnectionProviderHolderMap;
        this.tenantIdToConnectionProviderMap = tenantIdToConnectionProviderMap;
        return tenantIdToConnectionProviderHolderMap;
    }

    @Override
    public Map<Object, ConnectionProviderHolder> bootstrapTenantConnectionProviders(String tenantIdentifier) {
        Preconditions.checkNotNull(defaultConnectionProvider);

        Tenant tenant = resolveTenant(tenantIdentifier);

        ConnectionProviderHolder dataSourceHolder = createConnectionProviderHolder(tenant);
        tenantIdToConnectionProviderHolderMap.put(tenantIdentifier, dataSourceHolder);
        tenantIdToConnectionProviderMap.put(tenantIdentifier, dataSourceHolder.dataSource);

        return tenantIdToConnectionProviderHolderMap;
    }

    @Override
    public Map<Object, ConnectionProviderHolder> fetchConfiguredTenantConnectionProviderHolders() {
        if (UtilValidator.isNotEmpty(this.tenantIdToConnectionProviderHolderMap)) {
            logger.debug("Picking data from cache");
            return tenantIdToConnectionProviderHolderMap;
        } else {
            logger.debug("initialiseConfiguredTenantConnectionProviders");
            return bootstrapLandlordConnectionProviders(serviceRegistry);
        }
    }

    @Override
    public Map<Object, ConnectionProvider> fetchConfiguredTenantConnectionProvider() {
        return tenantIdToConnectionProviderMap;
    }

    public ConnectionProviderHolder resolveConnectionProviderForTenant(String tenantId) {
        if (tenantIdToConnectionProviderHolderMap.get(tenantId) == null) {
            bootstrapTenantConnectionProviders(tenantId);
        }
        return tenantIdToConnectionProviderHolderMap.get(tenantId);
    }

    public void cacheTenant(Tenant tenant) {
        tenantMap.put(tenant.getId() + "_" + tenant.getTenantName(), tenant);
    }

    public Tenant resolveTenant(String tenantIdentifier) {
        return tenantMap.get(tenantIdentifier);
    }

    protected static String getTenantDbUrl(Tenant tenant) {
        String url = "jdbc:postgresql://" + tenant.getDbHost() + ":" + tenant.getDbPort();
        return tenant.isEnabled() ? url + "/" + tenant.getDbName() : url;
    }
}
