package uk.airbyte.fwc.api;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.User;

public interface APIService {

    @POST("/auth/register")
    Call<User> registerUser(@Body Login login);

    @POST("/auth")
    Call<User> login(@Body Login login);

    @POST("/auth/forgot")
    Call<Reminder> forgotPassword(@Body Login login);

    @GET("/profile")
    Call<User> getUserProfile(@Header("User-Token") String accessToken);

    @PUT("/profile")
    Call<User> updateUserProfile(@Header("User-Token") String accessToken, @Body Login login);
}
