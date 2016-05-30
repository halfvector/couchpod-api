package com.couchpod.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ApiConfiguration extends Configuration {
//    @Valid
//    @NotNull
//    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @JsonProperty("cookieDomain")
    public String cookieDomain;

    @JsonProperty("cookieAccessTokenName")
    public String cookieAccessTokenName;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory datasourceFactory = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("environment")
    private String environment;

    @Valid
    @NotNull
    @JsonProperty("migrateOnStartup")
    private Boolean migrateOnStartup;

    // Max file upload size defaults to 25mb
    public int maxFileUploadSize = 1024 * 1024 * 25;

//    @JsonProperty("sundial")
//    public SundialConfiguration getSundialConfiguration() {
//        return sundialConfiguration;
//    }

    public Boolean getMigrateOnStartup() {
        return migrateOnStartup;
    }

    public String getEnvironment() {
        return environment;
    }

    public DataSourceFactory getDataSourceFactory() {
        return datasourceFactory;
    }
}