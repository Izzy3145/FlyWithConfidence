package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIError;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.api.ErrorUtils;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Password;
import uk.airbyte.fwc.model.Success;
import uk.airbyte.fwc.model.User;

public class AccountViewModel extends ViewModel {

    private static final String TAG = AccountViewModel.class.getSimpleName();

    private MutableLiveData<User> user;
    private MutableLiveData<Success> success;
    private Realm realm;

    private APIService apiService = APIClient.getClient().create(APIService.class);

    //we will call this method to get the data
    public LiveData<User> getUserProfile(Context context, String accessToken) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            profileCall(context, accessToken);
        }
        return user;
    }

    //we will call this method to get the data
    public LiveData<User> updateUserProfile(Context context, String accessToken, String fName, String lName, String email) {
        user = new MutableLiveData<User>();
        //we will load it asynchronously from server in this method
        putUserProfile(context, accessToken, fName, lName, email);
        return user;
    }

    //we will call this method to get the data
    public LiveData<Success> putUserPassword(Context context, String accessToken, String currentPassword, String newPassword) {

        success = new MutableLiveData<Success>();
        //we will load it asynchronously from server in this method
        putNewPassword(context, accessToken, currentPassword, newPassword);
        return success;
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

    private void putUserProfile(final Context context, String accessToken, String fName, String lName, String email) {
        apiService.updateUserProfile(accessToken, new Login(email, lName, fName))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            user.postValue(response.body());
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
                        Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                        user.postValue(null);
                    }
                });
    }

    private void putNewPassword(final Context context, String accessToken, String currentPassword, String newPassword) {
        apiService.updateUserPassword(accessToken, new Password(currentPassword, newPassword))
                .enqueue(new Callback<Success>() {
                    @Override
                    public void onResponse(Call<Success> call, Response<Success> response) {
                        if (response.isSuccessful()) {
                            success.postValue(response.body());
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
}
