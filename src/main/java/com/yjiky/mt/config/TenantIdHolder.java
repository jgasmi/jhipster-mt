package com.yjiky.mt.config;

public class TenantIdHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static void setTenantId(String tenantId) {
        contextHolder.set(tenantId);
    }

    public static String getTenantId() {
        return contextHolder.get();
    }

    public static void clearTenantId() {
        contextHolder.remove();
    }

}
