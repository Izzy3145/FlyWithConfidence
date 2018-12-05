package uk.airbyte.fwc.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import uk.airbyte.fwc.BuildConfig;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.User;

public interface APIService {

    @POST("/auth/register")
    Call<User> registerUser(@Body Login login);

    @POST("/auth")
    Call<User> login(@Body Login login);

    @POST("/auth/forgot")
    Call<Boolean> forgotPassword();
    //pass in @Field("email") String email, get sent:true/false back

    //TEST
    @POST("/api/users")
    Call<User> createUser(@Body User user);
}
