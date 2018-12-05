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
import uk.airbyte.fwc.model.User;

public class AuthViewModel extends ViewModel {

    private static final String TAG = AuthViewModel.class.getSimpleName();


    private MutableLiveData<User> user;

    //we will call this method to get the data
    public LiveData<User> getUser(String name, String leader) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            loadUser(name, leader);
        }

        //finally we will return the list
        Log.d(TAG, "Response getUser(): " + user.toString());

        return user;
    }

    private void loadUser(String name, String leader){
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<User> call = apiService.createUser(new User(name, leader));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Response loadUser() success: " + response.body());
                user.postValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Response loadUser() failure");
                user.postValue(null);
            }
        });
    }
}
