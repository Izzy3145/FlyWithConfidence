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
import uk.airbyte.fwc.model.User;

public class AuthViewModel extends ViewModel {

    private static final String TAG = AuthViewModel.class.getSimpleName();

    private MutableLiveData<User> user;

    private APIService apiService = APIClient.getClient().create(APIService.class);

    //we will call this method to get the data
    public LiveData<User> getUser(String password, String email) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            loginUser(password, email);
        }

        return user;
    }

    //TODO: remove/adapt?
    //we will call this method to get the data
    public LiveData<User> getUserTwo(String password, String email, String lName, String fName) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            registerUser(password, email, lName, fName);
        }

        return user;
    }

    private void loginUser(String password, String email) {
        apiService.login(new Login(password, email))
                .enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Response loginUser() success: " + response.body());
                user.postValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Response loginUser() failure");
                user.postValue(null);
            }
        });
    }

    private void registerUser(String password, String email, String lName, String fName) {
        apiService.registerUser(new Login(password, email, lName, fName))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "Response loginUser() success: " + response.body());
                        user.postValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response loginUser() failure");
                        user.postValue(null);
                    }
                });
    }
}
