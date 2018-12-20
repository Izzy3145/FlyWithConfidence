package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIError;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.api.ErrorUtils;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.Success;
import uk.airbyte.fwc.model.User;

public class TopicsViewModel extends ViewModel {

    private final static String TAG = TopicsViewModel.class.getSimpleName();

    // TODO: To get list of topics also

    private MutableLiveData<List<Module>> modules;

    private APIService apiService = APIClient.getClient().create(APIService.class);

    //we will call this method to get the data
    public LiveData<List<Module>> getModulesForTopic(Context context, String accessToken, String topicID) {
        if (modules == null) {
            modules = new MutableLiveData<List<Module>>();
            //we will load it asynchronously from server in this method
            moduleCall(context, accessToken, topicID);
        }
        return modules;
    }

    private void moduleCall(final Context context, String accessToken, String topicID) {
        apiService.getModulesForTopics(accessToken, topicID)
                .enqueue(new Callback<List<Module>>() {
                    @Override
                    public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                        if (response.isSuccessful()) {
                            modules.postValue(response.body());
                            Log.d(TAG, "Response moduleCall() success: " + response.body());
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            String errorCode = String.valueOf(error.status());
                            String errorMessage = error.message();
                            Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.d("moduleCall() error message", error.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Module>> call, Throwable t) {
                        Log.d(TAG, "Response moduleCall() failure");
                        Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                        modules.postValue(null);
                    }
                });
    }

}
