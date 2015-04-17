package com.yjiky.mt.multitenancy;

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
            if (CustomUserDetails.class.isInstance(authentication.getPrincipal())) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                tenantId = userDetails.getUser().getTenant().getId()+"_"+userDetails.getUser().getTenant().getName();
                ConnectionProviderFactory.getInstance().cacheTenant(userDetails.getUser().getTenant());
            }
        }
        System.out.println(" tenantId -->  " + tenantId);

        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
