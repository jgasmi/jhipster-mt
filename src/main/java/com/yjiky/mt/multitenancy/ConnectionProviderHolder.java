package com.yjiky.mt.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public class ConnectionProviderHolder {

    public final ConnectionProvider dataSource;
    public final String user;
    public final String password;

    public ConnectionProviderHolder(ConnectionProvider dataSource, String user, String password) {
        this.dataSource = dataSource;
        this.user = user;
        this.password = password;
    }
}
