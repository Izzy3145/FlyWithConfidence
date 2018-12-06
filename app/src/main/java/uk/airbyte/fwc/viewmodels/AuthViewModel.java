package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.User;

public class AuthViewModel extends ViewModel {

    private static final String TAG = AuthViewModel.class.getSimpleName();

    private MutableLiveData<User> user;
    private MutableLiveData<Reminder> reminderSent;


    private APIService apiService = APIClient.getClient().create(APIService.class);

    //we will call this method to get the data
    public LiveData<User> getUserFromLogin(String password, String email) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            loginCall(password, email);
        }

        return user;
    }

    //we will call this method to get the data
    public LiveData<User> getUserFromRegister(String password, String email, String lName, String fName) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            registerCall(password, email, lName, fName);
        }
        return user;
    }

    public LiveData<Reminder> getForgottenPw(String email) {

            reminderSent = new MutableLiveData<Reminder>();
            //we will load it asynchronously from server in this method
            forgotCall(email);

            return reminderSent;
    }

    private void loginCall(String password, String email) {
        apiService.login(new Login(password, email))
                .enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Response loginCall() success: " + response.body());
                user.postValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Response loginCall() failure");
                user.postValue(null);
            }
        });
    }

    private void registerCall(String password, String email, String lName, String fName) {
        apiService.registerUser(new Login(password, email, lName, fName))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "Response registerCall() success: " + response.body());
                        user.postValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response registerCall() failure");
                        user.postValue(null);
                    }
                });
    }

    private void forgotCall(String email) {
        apiService.forgotPassword(email).enqueue(new Callback<Reminder>() {
            @Override
            public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                Log.d(TAG, "Response forgotCall() success: " + response.body());
                reminderSent.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Reminder> call, Throwable t) {
                Log.d(TAG, "Response forgotCall() failure");
                reminderSent.postValue(null);
            }
        });

    }
}
