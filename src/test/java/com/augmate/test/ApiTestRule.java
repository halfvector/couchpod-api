package com.augmate.test;

import com.augmate.TestModule;
import com.couchpod.ApiBootstrap;
import com.couchpod.ApiConfiguration;
import com.google.inject.Guice;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.ws.rs.core.HttpHeaders;

public class ApiTestRule extends DropwizardAppRule<ApiConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(ApiTestRule.class);
    private TestModule module;

    public ApiTestRule() {
        super(ApiBootstrap.class, "src/dist/config/testing.yml");

        // Wait until server starts and configuration is available
        // then create a reusable TestModule instance
        // to minimize DI overhead and maximize DBI reuse.
        addListener(new ServiceListener<ApiConfiguration>() {
            @Override
            public void onRun(ApiConfiguration configuration, Environment environment, DropwizardAppRule<ApiConfiguration> rule) throws Exception {
                module = new TestModule(getConfiguration());
            }
        });
    }

    public void setupInjection(Object testClass) {
        Guice.createInjector(module).injectMembers(testClass);
    }

    /**
     * Build interface to API without an authorized user token.
     * @return
     */
    public ApiInterface getApi() {
        return new Retrofit.Builder()
                .baseUrl("http://localhost:" + getLocalPort())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    /**
     * Build interface to API with an authorized user token.
     */
    public ApiInterface getAuthenticatedApi(String token) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .authenticator((route, response) ->
                        response.request().newBuilder()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build()
                )
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://localhost:" + getLocalPort())
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }
}
