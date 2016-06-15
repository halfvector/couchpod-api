package com.couchpod;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;

public class ApiConfig extends Configuration {
    @NotEmpty
    private String jwtTokenSecret;

    public byte[] getJwtTokenSecret() throws UnsupportedEncodingException {
        return jwtTokenSecret.getBytes("UTF-8");
    }

//    @Valid
//    @NotNull
//    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @JsonProperty("cookieDomain")
    public String cookieDomain;

    @JsonProperty("cookieAccessTokenName")
    public String cookieAccessTokenName;

    public int maxSessionAgeSecs = 3600;

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