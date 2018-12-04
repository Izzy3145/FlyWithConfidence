package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.model.User;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<User> user;

    //we will call this method to get the data
    public LiveData<User> getUser(String name, String leader) {
        //if the list is null
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            loadUser(name, leader);
        }

        //finally we will return the list
        return user;
    }

    private void loadUser(String name, String leader){
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<User> call = apiService.createUser(new User(name, leader));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user.setValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                user.setValue(null);
            }
        });
    }


}
