package com.yjiky.mt.multitenancy;

import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import java.util.Properties;


public class MultiTenantConnectionProviderImpl extends org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider implements ServiceRegistryAwareService {

    Properties properties = new Properties();

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        ConnectionProviderFactory.getInstance().bootstrapLandlordConnectionProviders(serviceRegistry);
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        ConnectionProviderHolder dataSourceHolder = ConnectionProviderFactory.getInstance().resolveConnectionProviderForTenant(ConnectionProviderFactory.DEFAULT_LANDLORD);
        properties.setProperty(Environment.USER, dataSourceHolder.user);
        properties.setProperty(Environment.PASS, dataSourceHolder.password);

        return dataSourceHolder.dataSource;
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        ConnectionProviderHolder dataSourceHolder = ConnectionProviderFactory.getInstance().resolveConnectionProviderForTenant(tenantIdentifier);
        //properties.setProperty(Environment.USER,dataSourceHolder.user);
        //properties.setProperty(Environment.PASS,dataSourceHolder.password);

        return dataSourceHolder.dataSource;
    }
}
