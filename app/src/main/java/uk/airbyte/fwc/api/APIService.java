package uk.airbyte.fwc.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.Password;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.Success;
import uk.airbyte.fwc.model.Topic;
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

    @PUT("/profile/password")
    Call<Success> updateUserPassword(@Header("User-Token") String accessToken, @Body Password password);

    @GET("/content/topics")
    Call<List<Topic>> getTopics(@Header("User-Token") String accessToken, @Query("category") String category);

    @GET("/content/modules")
    Call<List<Module>> getModulesForTopics(@Header("User-Token") String accessToken, @Query("topic") String topicID);
}
