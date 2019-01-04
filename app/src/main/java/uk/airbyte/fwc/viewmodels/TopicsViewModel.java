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
import uk.airbyte.fwc.model.Topic;
import uk.airbyte.fwc.model.User;

import static io.realm.Realm.getDefaultInstance;

public class TopicsViewModel extends ViewModel {

    private final static String TAG = TopicsViewModel.class.getSimpleName();


    private MutableLiveData<List<Module>> modules;
    //private MutableLiveData<List<Topic>> topics;
    private List<Topic> listOfTopics;

    private APIService apiService = APIClient.getClient().create(APIService.class);



    public LiveData<List<Module>> getModulesFromTopics(Context context, String accessToken, String category){
        modules = new MutableLiveData<>();
        topicAndModuleCall(context, accessToken, category);
        return modules;
    }

    private void topicAndModuleCall(final Context context, final String accessToken, String category) {
        apiService.getTopics(accessToken, category).enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful()) {

                    listOfTopics = response.body();
                    for(int i = 0; i < listOfTopics.size(); i++){
                        Log.d(TAG, "TopicsVM Topic ID: " + listOfTopics.get(i).getId());
                        String topicID = listOfTopics.get(i).getId();
                        apiService.getModulesForTopics(accessToken, topicID).enqueue(new Callback<List<Module>>() {
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

                    Log.d(TAG, "Response topicAndModuleCall() success: " + response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String errorCode = String.valueOf(error.status());
                    String errorMessage = error.message();
                    Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("topicAndModuleCall() error message", error.message());
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.d(TAG, "Response topicAndModuleCall() failure");
                Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                modules.postValue(null);
            }
        });
    }

        /*public LiveData<List<Module>> getModulesForTopic(Context context, String accessToken, String topicID) {
        if(modules == null) {
            modules = new MutableLiveData<List<Module>>();
            moduleCall(context, accessToken, topicID);
        }
        return modules;
    }

        public LiveData<List<Module>> getTopics(Context context, String accessToken, String category) {
           if (topics == null) {
                topics = new MutableLiveData<List<Topic>>();
             topicCall(context, accessToken, category);
          }
         return topics;
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
    }*/
}
