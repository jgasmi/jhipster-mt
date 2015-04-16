package com.yjiky.mt.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String tenantId = ConnectionProviderFactory.DEFAULT_LANDLORD;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                if (userDetails.getUsername().equals("admin"))
                    tenantId = ConnectionProviderFactory.DEFAULT_LANDLORD;//userDetails.getUser().getMyTenantIdentifier();
                else
                    tenantId = "terre";
            }
        }
        System.out.println("   tenantId-->  " + tenantId);

        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
