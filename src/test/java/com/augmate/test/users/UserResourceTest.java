package com.augmate.test.users;

import com.augmate.test.ApiInterface;
import com.augmate.test.ResourceTestBase;
import com.couchpod.api.users.LoginRequestDTO;
import com.couchpod.api.users.LoginResponseDTO;
import com.couchpod.api.users.UserDTO;
import com.couchpod.api.users.UserRegistrationRequestDTO;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(JUnit4.class)
public class UserResourceTest extends ResourceTestBase {
    private static final Logger log = LoggerFactory.getLogger(UserResourceTest.class);

    @Inject
    private UserTestingDAO userDAO;

    @Before
    public void before() {
        userDAO.deleteAll();
    }

    @Test
    public void canRegisterUser() throws Exception {
        UserRegistrationRequestDTO request = getSampleRegistrationRequest();

        Response<Long> registrationResponse = api.registerUser(request).execute();

        assertThat(registrationResponse.code(), is(200));
    }

    @Test
    public void whenRegisteringDuplicateReturn409() throws Exception {
        UserRegistrationRequestDTO request = getSampleRegistrationRequest();

        Response<Long> firstRegistration = api.registerUser(request).execute();
        Response<Long> secondRegistration = api.registerUser(request).execute();

        assertThat(firstRegistration.code(), is(200));
        assertThat(secondRegistration.code(), is(409));
    }

    @Test
    public void canListAllUsers() throws Exception {
        Response<List<UserDTO>> users = api.getAllUsers().execute();
        assertThat(users.code(), is(200));
    }

    @Test
    public void whenFetchingNonExistingUserReturn404() throws Exception {
        // when fetching user that doesn't exist
        Response<UserDTO> user = api.getUserDetails(1000).execute();

        // then http status code should be 404
        assertThat(user.code(), is(404));
    }

    @Test
    public void whenFetchingExistingUserReturn200() throws Exception {
        // given we have a registered user
        UserRegistrationRequestDTO request = getSampleRegistrationRequest();
        long userId = api.registerUser(request).execute().body();

        // when fetching user details
        Response<UserDTO> response = api.getUserDetails(userId).execute();

        // then http status code should be ok
        assertThat(response.code(), is(200));

        // and user should have a matching name
        assertThat(response.body().fullName, is(request.fullName));
    }

    @Test
    public void newUserCanAccessAuthenticatedEndpoints() throws Exception {
        UserRegistrationRequestDTO newUserRequest = new UserRegistrationRequestDTO();
        newUserRequest.fullName = "Alex";
        newUserRequest.password = "this is a password";
        newUserRequest.email = "aleckz@gmail.com";
        api.registerUser(newUserRequest).execute();

        // when asking for a json web token
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.email = newUserRequest.email;
        loginRequest.password = newUserRequest.password;
        Response<LoginResponseDTO> loginResponse = api.login(loginRequest).execute();

        // then http status code should be ok
        assertThat(loginResponse.code(), is(200));

        ApiInterface authenticatedApi = Rule.getAuthenticatedApi(loginResponse.body().token);

        Response<UserDTO> currentUserResponse = authenticatedApi.getCurrentUser().execute();

        assertThat(currentUserResponse.code(), is(200));
        assertThat(currentUserResponse.body().fullName, is(newUserRequest.fullName));
    }

    @Test
    public void loggedOutUserHasNoAuth() throws Exception {
        Response<UserDTO> currentUserResponse = api.getCurrentUser().execute();

        assertThat(currentUserResponse.code(), is(401));
    }

    /*
     * Helpers
     */

    private UserRegistrationRequestDTO getSampleRegistrationRequest() {
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.fullName = "Bob Barker";
        request.password = "exc3ll3nt p4ssw0Rd!!<>";
        request.email = "bob.barker@couchpod.com";
        return request;
    }
}
