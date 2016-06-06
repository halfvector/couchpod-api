package com.augmate.test;

import com.couchpod.api.contributors.ContributorDTO;
import com.couchpod.api.streams.CreateStreamRequestDTO;
import com.couchpod.api.streams.StreamDTO;
import com.couchpod.api.users.LoginRequestDTO;
import com.couchpod.api.users.LoginResponseDTO;
import com.couchpod.api.users.UserDTO;
import com.couchpod.api.users.UserRegistrationRequestDTO;
import retrofit2.Call;
import retrofit2.http.*;

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

    @GET("contributors")
    Call<Void> setStreamContributor(@Body ContributorDTO relationship);

    @DELETE("contributors")
    Call<Void> removeStreamContributor(@Body ContributorDTO relationship);

    @POST("users/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO dto);

    @POST("users/logout")
    Call<Void> logout();

    @GET("users/get_current")
    Call<UserDTO> getCurrentUser();
}
