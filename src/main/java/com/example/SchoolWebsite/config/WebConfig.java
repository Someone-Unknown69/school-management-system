package com.example.SchoolWebsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig — the single place where Spring MVC extensions are registered.
 *
 * Registers RoleInterceptor for ALL paths; the interceptor itself decides
 * which paths are public and which are protected.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    public WebConfig(RoleInterceptor roleInterceptor) {
        this.roleInterceptor = roleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply to every URL; RoleInterceptor.isPublicPath() whitelists the open ones.
        registry.addInterceptor(roleInterceptor);
    }
}

