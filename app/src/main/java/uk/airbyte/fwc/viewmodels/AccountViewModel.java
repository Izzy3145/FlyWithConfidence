package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIError;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.api.ErrorUtils;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.User;

public class AccountViewModel extends ViewModel {

    private static final String TAG = AccountViewModel.class.getSimpleName();

    private MutableLiveData<User> user;
    private APIService apiService = APIClient.getClient().create(APIService.class);

    //we will call this method to get the data
    public LiveData<User> getUserProfile(String accessToken) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            profileCall(accessToken);
        }

        return user;
    }

    //we will call this method to get the data
    public LiveData<User> updateUserProfile(String accessToken, String fName, String lName, String email) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            putUserProfile(accessToken, fName, lName, email);
        }
        return user;
    }

    private void profileCall(String accessToken) {
        apiService.getUserProfile(accessToken)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {

                            Log.d(TAG, "Response profileCall() success: " + response.body());
                            user.postValue(response.body());
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            // … and use it to show error information
                            //TODO: post a toast message to activity with error message shown
                            // … or just log the issue like we’re doing :)
                            Log.d("profileCall() error message", error.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response profileCall() failure");
                        user.postValue(null);
                    }
                });
    }

    private void putUserProfile(String accessToken, String fName, String lName, String email) {
        apiService.updateUserProfile(accessToken, new Login(fName, lName, email))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {

                            Log.d(TAG, "Response updateUserProfile() success: " + response.body());
                            user.postValue(response.body());

                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            // … and use it to show error information
                            //TODO: post a toast message to activity with error message shown
                            // … or just log the issue like we’re doing :)
                            Log.d("updateUserProfile() error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response registerCall() failure");
                        user.postValue(null);
                    }
                });
    }
}
