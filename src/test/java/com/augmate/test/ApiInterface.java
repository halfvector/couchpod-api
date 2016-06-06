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
    /*
     * Stream CRUD
     */

    @POST("streams")
    Call<Long> createStream(@Body CreateStreamRequestDTO request);

    @GET("streams")
    Call<List<StreamDTO>> getAllStreams();

    @GET("streams/{id}")
    Call<StreamDTO> getStreamDetails(@Path("id") long streamId);

    /*
     * User CRUD
     */

    @POST("users")
    Call<Long> registerUser(@Body UserRegistrationRequestDTO request);

    @GET("users")
    Call<List<UserDTO>> getAllUsers();

    @GET("users/{id}")
    Call<UserDTO> getUserDetails(@Path("id") long userId);

    /*
     * Stream Contributor assignment
     */

    @GET("contributors")
    Call<Void> setStreamContributor(@Body ContributorDTO relationship);

    @DELETE("contributors")
    Call<Void> removeStreamContributor(@Body ContributorDTO relationship);

    /*
     * Authentication
     */

    @POST("tokens")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO dto);

    @DELETE("tokens")
    Call<Void> logout();

    @GET("tokens/current")
    Call<UserDTO> getCurrentUser();
}
