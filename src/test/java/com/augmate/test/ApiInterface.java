package com.augmate.test;

import com.couchpod.api.streams.CreateStreamRequestDTO;
import com.couchpod.api.streams.StreamDTO;
import com.couchpod.api.users.UserDTO;
import com.couchpod.api.users.UserRegistrationRequestDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiInterface {
    @POST("streams")
    Call<Long> createStream(@Body CreateStreamRequestDTO request);

    @GET("streams")
    Call<List<StreamDTO>> getAllStreams();

    @GET("streams/{id}")
    Call<StreamDTO> getStreamDetails(@Path("id") long streamId);

    @POST("users")
    Call<Long> registerUser(@Body UserRegistrationRequestDTO request);

    @GET("users")
    Call<List<UserDTO>> getAllUsers();

    @GET("users/{id}")
    Call<UserDTO> getUserDetails(@Path("id") long userId);
}
