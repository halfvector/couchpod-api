package com.augmate.test;

import com.couchpod.ApiBootstrap;
import com.couchpod.ApiConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiResourceTestRule extends DropwizardAppRule<ApiConfiguration> {
    public ApiResourceTestRule() {
        super(ApiBootstrap.class, "src/dist/config/testing.yml");
    }

    public ApiInterface getApi() {
        return new Retrofit.Builder()
                .baseUrl("http://localhost:" + getLocalPort())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }
}
