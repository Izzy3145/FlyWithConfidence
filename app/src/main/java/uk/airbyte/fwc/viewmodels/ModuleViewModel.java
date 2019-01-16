package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIError;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.api.ErrorUtils;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.RealmListLiveData;
import uk.airbyte.fwc.model.Topic;
import uk.airbyte.fwc.repositories.ModuleRepository;
import uk.airbyte.fwc.utils.Const;

public class ModuleViewModel extends ViewModel implements OrderedRealmCollectionChangeListener<RealmResults<Module>> {

    private final static String TAG = ModuleViewModel.class.getSimpleName();

    private ArrayList<RealmResults<Module>> knowledgeModules = new ArrayList<RealmResults<Module>>();
    private List<Topic> knowledgeTopics;
    private int numKnowledgeTopics;
    private int numKnowledgeModules;

    private ArrayList<RealmResults<Module>> preparationModules = new ArrayList<RealmResults<Module>>();
    private List<Topic> preparationTopics;
    private int numPreparationTopics;
    private int numPreparationModules;

    private APIService apiService = APIClient.getClient().create(APIService.class);
    private final ModuleRepository moduleRepository;
    private RealmResults<Module> favouriteResults;
    private RealmResults<Module> recentsResults;
    private RealmResults<Module> topicResults;
    private RealmResults<Module> allResults;

    public ModuleViewModel() {
        moduleRepository = new ModuleRepository();
    }

    public void onResume() {
        moduleRepository.addChangeListener(this);
    }

    public void onPause() {
        moduleRepository.clearListeners();
    }

    public void closeRealm(){
        moduleRepository.onDestroy();
    }

    public void setFavourite(Boolean isFavourite, Module module) {
        moduleRepository.setRealmFavourite(isFavourite, module);
    }

    public RealmResults<Module> getAll() {
        allResults = moduleRepository.getModulesToDisplay();
        return allResults;
    }

    public RealmResults<Module> getModulesForTopic(String topicID) {
        topicResults = moduleRepository.getModulesForTopic(topicID);
        return topicResults;
    }

    public Module getModuleFromId(String moduleID) {
        return moduleRepository.getModuleFromID(moduleID);
    }

    public RealmResults<Module> getFavourites() {
        favouriteResults = moduleRepository.getRealmFavourites();
        return favouriteResults;
    }

    public RealmResults<Module> getRecents() {
        recentsResults = moduleRepository.getRealmRecents();
        return recentsResults;
    }

    public void deleteRecent(String moduleID) {
        moduleRepository.deleteRealmRecent(moduleID);
    }

    public void deleteFavourite(String moduleID) {
        moduleRepository.deleteRealmFavourite(moduleID);
    }

    //methods to get all KNOWLEDGE topics and modules, and return list of topics as liveData
    public ArrayList<RealmResults<Module>> getKnowledgeAdapterData() {
        knowledgeModules = new ArrayList<RealmResults<Module>>();
        for (int i = 0; i < knowledgeTopics.size(); i++) {
            String topicID = knowledgeTopics.get(i).getId();
            RealmResults<Module> modules = moduleRepository.getModulesForCategory(topicID, Const.REALM_KNOWLEDGE);
            if (modules.size() > 0) {
                knowledgeModules.add(modules);
            }
        }
        return knowledgeModules;
    }

    public void knowledgeTopicAndModuleCall(final Context context, final String accessToken) {
        numKnowledgeTopics = 0;
        numKnowledgeModules = 0;
        knowledgeTopics = new ArrayList<Topic>();
        apiService.getTopics(accessToken, Const.API_KNOWLEDGE).enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful()) {
                    knowledgeTopics = response.body();
                    //liveKnowledgeTopics.postValue(knowledgeTopics);
                    for (int i = 0; i < knowledgeTopics.size(); i++) {
                        numKnowledgeTopics++;
                        Log.d(TAG, "Number of Knowledge Topics found: " + numKnowledgeTopics);
                        String topicID = knowledgeTopics.get(i).getId();
                        apiService.getModulesForTopics(accessToken, topicID).enqueue(new Callback<List<Module>>() {
                            @Override
                            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Response moduleCall() success: " + response.body());
                                    moduleRepository.copyTopicModulesToRealm(response.body());
                                    numKnowledgeModules = numKnowledgeModules + response.body().size();
                                    Log.d(TAG, "Knowledge knowledgeTopicAndModuleCall() moduleList size: " + numKnowledgeModules);
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
                            }
                        });
                    }
                    Log.d(TAG, "Response knowledgeTopicAndModuleCall() success: " + response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String errorCode = String.valueOf(error.status());
                    String errorMessage = error.message();
                    Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("knowledgeTopicAndModuleCall() error message", error.message());
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.d(TAG, "Response knowledgeTopicAndModuleCall() failure");
                Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //methods to get all PREAPARTION topics and modules, and return list of topics as liveData
    public ArrayList<RealmResults<Module>> getPreparationAdapterData() {
        preparationModules = new ArrayList<RealmResults<Module>>();
        for (int i = 0; i < preparationTopics.size(); i++) {
            String topicID = preparationTopics.get(i).getId();
            RealmResults<Module> modules = moduleRepository.getModulesForCategory(topicID, Const.REALM_PREPARATION);
            if (modules.size() > 0) {
                preparationModules.add(modules);
            }
        }
        return preparationModules;
    }

    public void preparationTopicAndModuleCall(final Context context, final String accessToken) {
        numPreparationTopics = 0;
        numPreparationModules = 0;
        preparationTopics = new ArrayList<Topic>();
        apiService.getTopics(accessToken, Const.API_PREPARATION).enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful()) {
                    preparationTopics = response.body();
                    // livePreparationTopics.postValue(preparationTopics);
                    for (int i = 0; i < preparationTopics.size(); i++) {
                        numPreparationTopics++;
                        Log.d(TAG, "Number of Preparation Topics found: " + numPreparationTopics);
                        String topicID = preparationTopics.get(i).getId();
                        apiService.getModulesForTopics(accessToken, topicID).enqueue(new Callback<List<Module>>() {
                            @Override
                            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Response moduleCall() success: " + response.body());
                                    moduleRepository.copyTopicModulesToRealm(response.body());
                                    numPreparationModules = numPreparationModules + response.body().size();
                                    Log.d(TAG, "Preparation TopicAndModuleCall() moduleList size: " + numPreparationModules);
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
                                //modules.postValue(null);
                            }
                        });
                    }
                    Log.d(TAG, "Response preparationTopicAndModuleCall() success: " + response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String errorCode = String.valueOf(error.status());
                    String errorMessage = error.message();
                    Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("preparationTopicAndModuleCall() error message", error.message());
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.d(TAG, "Response preparationTopicAndModuleCall() failure");
                Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onChange(RealmResults<Module> modules, OrderedCollectionChangeSet changeSet) {
        favouriteResults = moduleRepository.getRealmFavourites();
        recentsResults = moduleRepository.getRealmRecents();
        //categoryModuleResults = moduleRepository.get
        //topicResults = moduleRepository.getModulesForTopic()
        allResults = moduleRepository.getModulesToDisplay();
    }
}