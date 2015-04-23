package com.yjiky.mt.multitenancy;

import com.yjiky.mt.domain.Tenant;
import liquibase.database.Database;
import liquibase.exception.CustomPreconditionErrorException;
import liquibase.exception.CustomPreconditionFailedException;
import liquibase.precondition.CustomPrecondition;

public class MultitenantPrecondition implements CustomPrecondition {
    private enum Tenancy {
        LANDLORD("landlord"),
        TENANT("tenant"),
        BOTH("both");

        private String value;

        Tenancy(String value) {
            this.value = value;
        }

        protected String getValue() {
            return value;
        }
    }

    private String tenancy;

    public String getTenancy() {
        return tenancy;
    }

    public void setTenancy(String tenancy) {
        this.tenancy = tenancy;
    }

    @Override
    public void check(final Database db) throws CustomPreconditionFailedException, CustomPreconditionErrorException {
        try {
            String dbUrl = db.getConnection().getURL();
            Tenant tenant = ConnectionProviderFactory.getInstance().fetchTenantByDbUrl(dbUrl);
            if (tenant == null ) {
                if (Tenancy.TENANT.getValue().equals(tenancy)){
                    throw new CustomPreconditionFailedException("LANDLORD DB");
                }
            } else {
                if (Tenancy.LANDLORD.getValue().equals(tenancy)){
                    throw new CustomPreconditionFailedException("TENANT DB");
                }
            }
        } catch (final CustomPreconditionFailedException e) {
            throw e;
        } catch (final Exception e) {
            throw new CustomPreconditionErrorException("Error", e);
        }
    }
}
