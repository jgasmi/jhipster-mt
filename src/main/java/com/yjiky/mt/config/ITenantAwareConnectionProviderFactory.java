package com.yjiky.mt.config;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import java.util.Map;

public interface ITenantAwareConnectionProviderFactory {

    Map<Object,ConnectionProviderHolder> bootstrapLandlordConnectionProviders(ServiceRegistryImplementor serviceRegistry);

    Map<Object,ConnectionProviderHolder> bootstrapTenantConnectionProviders(String tenantId);

    Map<Object, ConnectionProviderHolder> fetchConfiguredTenantConnectionProviderHolders();

    Map<Object,ConnectionProvider> fetchConfiguredTenantConnectionProvider();
}
