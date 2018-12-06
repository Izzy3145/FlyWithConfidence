package uk.airbyte.fwc.api;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.User;

public interface APIService {

    @POST("/auth/register")
    Call<User> registerUser(@Body Login login);

    @POST("/auth")
    Call<User> login(@Body Login login);

    @POST("/auth/forgot")
    Call<Reminder> forgotPassword(@Body String email);

    //TEST
    @POST("/api/users")
    Call<User> createUser(@Body User user);
}
