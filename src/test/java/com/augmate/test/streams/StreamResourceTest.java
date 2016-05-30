package com.augmate.test.streams;

import com.augmate.test.ApiResourceTestRule;
import com.couchpod.api.streams.StreamDTO;
import org.junit.ClassRule;
import org.junit.Test;
import retrofit2.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class StreamResourceTest {
    @ClassRule
    public static final ApiResourceTestRule Rule = new ApiResourceTestRule();

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

}
