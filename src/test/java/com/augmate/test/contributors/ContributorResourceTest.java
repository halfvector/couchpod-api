package com.augmate.test.contributors;

import com.augmate.test.ResourceTestBase;
import com.couchpod.api.contributors.ContributorDTO;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ContributorResourceTest extends ResourceTestBase {
    @Inject
    private ContributorTestingDAO contributorTestingDAO;

    @Before
    public void before() {
        contributorTestingDAO.deleteAll();
    }

    @Test
    public void canCreateAndRemoveRelationship() throws Exception {
        ContributorDTO dto = getSampleRegistrationRequest();

        Response<Void> execute = Rule.getApi().setStreamContributor(dto).execute();
        assertThat(execute.code(), is(200));

        execute = Rule.getApi().removeStreamContributor(dto).execute();
        assertThat(execute.code(), is(200));
    }

    /*
     * Helpers
     */

    private ContributorDTO getSampleRegistrationRequest() {
        ContributorDTO request = new ContributorDTO();
        request.userId = 1;
        request.streamId = 2;
        request.contributionType = 0;
        return request;
    }
}
