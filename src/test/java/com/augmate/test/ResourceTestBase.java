package com.augmate.test;

import org.junit.ClassRule;

public class ResourceTestBase {
    @ClassRule
    public static final ApiTestRule Rule = new ApiTestRule();

    protected ApiInterface api = Rule.getApi();

    public ResourceTestBase() {
        Rule.setupInjection(this);
    }
}
