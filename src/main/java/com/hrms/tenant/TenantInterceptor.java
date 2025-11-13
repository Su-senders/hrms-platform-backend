package com.hrms.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor to extract tenant identifier from HTTP headers.
 * Sets the tenant context for the current request.
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Value("${hrms.multi-tenant.default-tenant}")
    private String defaultTenant;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = defaultTenant;
            log.debug("No tenant ID in header, using default: {}", defaultTenant);
        }

        log.debug("Setting tenant context to: {}", tenantId);
        TenantContext.setTenantId(tenantId);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, ModelAndView modelAndView) {
        // Optional: Add tenant ID to response header
        response.setHeader(TENANT_HEADER, TenantContext.getTenantId());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) {
        TenantContext.clear();
    }
}
