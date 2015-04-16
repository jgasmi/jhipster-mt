package com.yjiky.mt.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yjiky.mt.domain.Tenant;
import com.yjiky.mt.domain.TenantConfig;
import com.yjiky.mt.domain.util.UtilValidator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConnectionProviderFactory implements ITenantAwareConnectionProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionProviderFactory.class);
    private static ConnectionProviderFactory instance;
    public static final String DEFAULT_LANDLORD = "DEFAULT";

    private static Map<Object, ConnectionProviderHolder> tenantIdToConnectionProviderHolderMap = null;
    private static Map<Object, ConnectionProvider> tenantIdToConnectionProviderMap = null;

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
        TenantConfig tenantConfig = getTenantConfig(tenant);
        dataSource.configure(getSettingsForTenant(cfgSettings, tenantConfig));

        ConnectionProviderHolder dataSourceHolder = new ConnectionProviderHolder(dataSource, tenantConfig.getUsername(), tenantConfig.getPassword());
        return dataSourceHolder;
    }

    private ConnectionProvider createLandlordConnectionProvider() {
        logger.debug("Going to create a brand new connection provider for landlord ");
        C3P0ConnectionProvider dataSource = new C3P0ConnectionProvider();
        dataSource.injectServices(serviceRegistry);
        TenantConfig tenantConfig = getLandlordConfig();
        dataSource.configure(getSettingsForTenant(cfgSettings, tenantConfig));

        return dataSource;
    }

    private Map getSettingsForTenant(Map cfgSettings, TenantConfig tenantConfig) {
        Map tenantSettings = new HashMap();
        tenantSettings.putAll(cfgSettings);

        String url = tenantConfig.getUrl();
        String driver = "org.postgresql.Driver";
        String password = tenantConfig.getPassword();
        String username = tenantConfig.getUsername();

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


    TenantConfig getTenantConfig(Tenant tenant) {
//        JdbcTemplate jdbcTemplate = getJdbcTemplate();
//        Map<String, Object> tenantResult = jdbcTemplate.queryForMap("select * from tenant where tenantid = ?", tenant.getId());
//        Map<String, Object> customisationResult = jdbcTemplate.queryForMap("select * from tenant_customisation_details where customisationId = ?", tenantResult.get("customisationId"));
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setId(2l);
        tenantConfig.setUrl("jdbc:postgresql://localhost:5432/tenantdb");
        tenantConfig.setUsername("postgres");
        tenantConfig.setPassword("postgres");
        return tenantConfig;
    }

    TenantConfig getLandlordConfig() {
//        JdbcTemplate jdbcTemplate = getJdbcTemplate();
//        Map<String, Object> tenantResult = jdbcTemplate.queryForMap("select * from tenant where tenantid = ?", tenant.getId());
//        Map<String, Object> customisationResult = jdbcTemplate.queryForMap("select * from tenant_customisation_details where customisationId = ?", tenantResult.get("customisationId"));
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setId(1l);
        tenantConfig.setUrl("jdbc:postgresql://localhost:5432/jhmtdb");
        tenantConfig.setUsername("postgres");
        tenantConfig.setPassword("postgres");
        return tenantConfig;
    }

    @Override
    public Map<Object, ConnectionProviderHolder> bootstrapLandlordConnectionProviders(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.cfgSettings = serviceRegistry.getService(ConfigurationService.class).getSettings();
        //TODO GET LANDLORD CONNECTION SETTINGS
        Map<Object, ConnectionProviderHolder> tenantIdToConnectionProviderHolderMap = Maps.newHashMap();
        Map<Object, ConnectionProvider> tenantIdToConnectionProviderMap = Maps.newHashMap();

        if (defaultConnectionProvider == null)
            defaultConnectionProvider = createLandlordConnectionProvider();

        ConnectionProviderHolder defaultConnectionProviderHolder = new ConnectionProviderHolder(defaultConnectionProvider, "root", "");
        tenantIdToConnectionProviderHolderMap.put(DEFAULT_LANDLORD, defaultConnectionProviderHolder);
        tenantIdToConnectionProviderMap.put(DEFAULT_LANDLORD, defaultConnectionProvider);
        this.tenantIdToConnectionProviderHolderMap = tenantIdToConnectionProviderHolderMap;
        this.tenantIdToConnectionProviderMap = tenantIdToConnectionProviderMap;
        return tenantIdToConnectionProviderHolderMap;
    }

    @Override
    public Map<Object, ConnectionProviderHolder> bootstrapTenantConnectionProviders(String tenantId) {
        Preconditions.checkNotNull(defaultConnectionProvider);

//        JdbcTemplate jdbcTemplate = getJdbcTemplate();
//        List<Tenant> allConfiguredTenants = jdbcTemplate.query("select * from tenant", new RowMapper<Tenant>() {
//            @Override
//            public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
//                Tenant tenant = new Tenant(rs.getString("tenantId"), rs.getString("tenantName"), rs.getBoolean("isEnabled"));
//                return tenant.with(rs.getLong("customisationId"));
//            }
//        });
        //TODO dummy tenants
        Tenant tenant = new Tenant();
        tenant.setId(3l);
        tenant.setName("terre");
        tenant.setIsEnabled(true);
        ConnectionProviderHolder dataSourceHolder = createConnectionProviderHolder(tenant);
        tenantIdToConnectionProviderHolderMap.put(tenant.getName(), dataSourceHolder);
        tenantIdToConnectionProviderMap.put(tenant.getName(), dataSourceHolder.dataSource);

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
}
