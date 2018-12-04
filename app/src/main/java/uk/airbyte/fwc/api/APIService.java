package uk.airbyte.fwc.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import uk.airbyte.fwc.model.User;

public interface APIService {

    @POST("/auth/register")
    Call<User> registerUser();
    //add in @Body User user

    @POST("/auth")
    Call<User> login();
    //pass in @Field("email") String email,  @Field("password") String password

    @POST("/auth/forgot")
    Call<Boolean> forgotPassword();
    //pass in @Field("email") String email, get sent:true/false back

    //TEST
    @POST("/api/users")
    Call<User> createUser(@Body User user);
}
