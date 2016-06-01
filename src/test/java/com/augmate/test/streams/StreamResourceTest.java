package com.augmate.test.streams;

import com.augmate.test.ResourceTestBase;
import com.augmate.test.users.UserTestingDAO;
import com.couchpod.api.streams.CreateStreamRequestDTO;
import com.couchpod.api.streams.StreamDTO;
import com.couchpod.api.users.UserDTO;
import com.couchpod.api.users.UserRegistrationRequestDTO;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class StreamResourceTest extends ResourceTestBase {
    @Inject
    private StreamTestingDAO streamDAO;

    @Before
    public void before() {
        streamDAO.deleteAll();
    }

    @Test
    public void canListStreams() throws Exception {
        Response<List<StreamDTO>> execute = Rule.getApi().getAllStreams().execute();

        assertThat(execute.code(), is(200));
        assertThat(execute.body(), is(not(nullValue())));
    }

    @Test
    public void whenStreamDoesNotExistReturn404() throws Exception {
        Response<StreamDTO> execute = Rule.getApi().getStreamDetails(100000).execute();
        assertThat(execute.code(), is(404));
    }

    @Test
    public void whenFetchingExistingStreamReturn200() throws Exception {
        // given we have a registered user
        CreateStreamRequestDTO request = getSampleRegistrationRequest();
        long streamId = api.createStream(request).execute().body();

        // when fetching user details
        Response<StreamDTO> response = api.getStreamDetails(streamId).execute();

        // then http status code should be ok
        assertThat(response.code(), is(200));

        // and user should have a matching name
        assertThat(response.body().name, is(request.streamName));
    }

    /*
     * Helpers
     */

    private CreateStreamRequestDTO getSampleRegistrationRequest() {
        CreateStreamRequestDTO request = new CreateStreamRequestDTO();
        request.streamName = "A Public Stream";
        request.description = "Default stream";
        request.visibility = 0L;
        return request;
    }
}
