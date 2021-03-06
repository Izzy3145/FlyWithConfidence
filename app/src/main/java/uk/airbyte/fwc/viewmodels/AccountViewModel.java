package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIError;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.api.ErrorUtils;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Password;
import uk.airbyte.fwc.model.Success;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.repositories.UserRepository;

public class AccountViewModel extends ViewModel{

    private static final String TAG = AccountViewModel.class.getSimpleName();
    private MutableLiveData<User> user;
    private MutableLiveData<Boolean> userUpdated;
    private MutableLiveData<Success> success;
    public MutableLiveData<User> userName;
    private APIService apiService = APIClient.getClient().create(APIService.class);
    private final UserRepository userRepository;

    public AccountViewModel() {
        userRepository = new UserRepository();
        }

    public LiveData<User> getUserProfile(Context context, String accessToken) {
        if (user == null) {
            user = new MutableLiveData<User>();
            profileCall(context, accessToken);
        }
        return user;
    }

    public User getUserRealm(String accessToken){
        return userRepository.getUserDetailsRealm(accessToken);
    }

    public LiveData<Boolean> userUpdated(){
        if(userUpdated == null){
            userUpdated = new MutableLiveData<>();
        }
        return userUpdated;
    }

    public void closeRealm(){
        userRepository.onDestroy();
    }


    private void profileCall(final Context context, String accessToken) {
        apiService.getUserProfile(accessToken)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            user.postValue(response.body());
                            Log.d(TAG, "Response profileCall() success: " + response.body());
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            String errorCode = String.valueOf(error.status());
                            String errorMessage = error.message();
                            Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.d("profileCall() error message", error.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response profileCall() failure");
                        Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                        user.postValue(null);
                    }
                });
    }

    public void putUserProfile(final Context context, final String accessToken, String fName, String lName, String email) {
        apiService.updateUserProfile(accessToken, new Login(email, lName, fName))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            userRepository.updateUserDetailsRealm(response.body(), accessToken);
                            userUpdated.postValue(true);
                            Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Response updateUserProfile() success: " + response.body());

                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            // … and use it to show error information
                            String errorCode = String.valueOf(error.status());
                            String errorMessage = error.message();
                            Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.d("updateUserProfile() error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response registerCall() failure");
                        userUpdated.postValue(false);
                        Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void putNewPassword(final Context context, String accessToken, String currentPassword, String newPassword) {
        apiService.updateUserPassword(accessToken, new Password(currentPassword, newPassword))
                .enqueue(new Callback<Success>() {
                    @Override
                    public void onResponse(Call<Success> call, Response<Success> response) {
                        if (response.isSuccessful()) {
                            //success.postValue(response.body());
                            Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Response profileCall() success: " + response.body());
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            String errorMessage = error.message();
                            Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.d("profileCall() error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Success> call, Throwable t) {
                        Log.d(TAG, "Response profileCall() failure");
                        Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                        success.postValue(null);
                    }
                });
    }

    public void deleteRealmContents(){
        userRepository.deleteRealmContents();
    }
}
