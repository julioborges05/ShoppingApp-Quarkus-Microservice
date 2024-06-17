package com.julionborges.config;

import com.julionborges.service.ProductService;
import com.julionborges.service.UserService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
public class ReadinessCheck implements HealthCheck {

    @Inject
    @RestClient
    private ProductService productService;

    @Inject
    @RestClient
    private UserService userService;

    @Override
    public HealthCheckResponse call() {
        if(productService.findById(1L) != null && userService.findById(1L) != null)
            return HealthCheckResponse.up("Ready");

        return HealthCheckResponse.up("Not ready");
    }
}
