package com.yjiky.mt.multitenancy;

import com.yjiky.mt.domain.Tenant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TenantRowMapper implements RowMapper<Tenant> {

    @Override
    public Tenant mapRow(ResultSet resultSet, int i) throws SQLException {
        return null;
    }
}
