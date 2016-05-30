package com.couchpod.healthchecks;

import com.codahale.metrics.health.HealthCheck;

/**
 * Simple health-check for load-balancers.
 * Ideally should check that this API service is connected to
 * all dependent services and ready for requests.
 */
public class LoadBalancerPing extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}