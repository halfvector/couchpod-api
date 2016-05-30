package com.augmate.test;

import com.couchpod.api.streams.StreamDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ApiInterface {
    @GET("streams")
    Call<List<StreamDTO>> getAllStreams();

    @GET("streams/{id}")
    Call<StreamDTO> getStreamDetails(@Path("id") long streamId);
}
