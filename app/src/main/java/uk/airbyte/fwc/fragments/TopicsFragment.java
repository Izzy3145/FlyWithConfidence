package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.ModulesAdapter;
import uk.airbyte.fwc.adapters.TopicsAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.Topic;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.ModuleViewModel;

public class TopicsFragment extends Fragment implements TopicsAdapter.TopicsAdapterListener {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    //@BindView(R.id.topicsRv1)
    //RecyclerView mRecyclerView1;
    @BindView(R.id.verticalTopicsRv)
    RecyclerView verticalRv;

    private ModuleViewModel mModuleViewModel;
    private String accessToken;
    private SharedPreferences sharedPref;
    private RecyclerView.LayoutManager vertManager;
    private TopicsAdapter topicsAdapter;
    private String category;
    ArrayList<RealmResults<Module>> modulesTopics = new ArrayList<RealmResults<Module>>();
    private ArrayList<Module> moduleList = new ArrayList<Module>();
    private List<String> mTopicIDList = new ArrayList<String>();


    public static TopicsFragment newInstance() {
        return new TopicsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        accessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        mModuleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);
        topicsAdapter = new TopicsAdapter(modulesTopics, getActivity(), this);
        category = "knowledge";

        mModuleViewModel.topicAndModuleCall(getActivity(), accessToken, category);

        getModulesForCategory();

        /*mModuleViewModel.getAllResultsLive().observe(this, new Observer<RealmResults<Module>>() {
            @Override
            public void onChanged(@Nullable RealmResults<Module> modules) {
                realmModules = modules;
                setUpModulesAdapter();
                //TODO: shouldn't need to call setUpModulesAdapter...
            }
        });*/

        //get and save all modules - move this to viewmodel, or somewhere else?
        /*mModuleViewModel.getModulesFromTopics(getActivity(), accessToken, category).observe(this, new Observer<List<Module>>() {
            @Override
            public void onChanged(@Nullable List<Module> modules) {
                if (modules != null) {
                    moduleList.addAll(modules);
                    numberOfTopics++;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(moduleList);
                        }
                    });

                    for (int i = 0; i < modules.size(); i++) {
                        Log.d(TAG, "topicID: " + modules.get(i).getTopic().getId() + " Module name: " + modules.get(i).getName());
                    }
                    //modulesAdapter.setModulesToAdapter(moduleList);
                }
                Log.d(TAG, "moduleList size: " + moduleList.size());
                Log.d(TAG, "Number of Topics found: " + numberOfTopics);
            }
        });*/

        //method to receive all found topic IDs



    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topics_fragment, container, false);
        ButterKnife.bind(this, view);

        //TabLayout tabLayout = new TabLayout(getActivity());
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.top_tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        category = "knowledge";
                        Log.d(TAG, "Category selected: " + category);
                        getModulesForCategory();
                        break;
                    case 1:
                        category = "preparation";
                        Log.d(TAG, "Category selected: " + category);
                        getModulesForCategory();
                        break;
                    default:
                        category = "knowledge";
                        Log.d(TAG, "Category selected: " + category);
                        getModulesForCategory();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

       /* realmModules = realm.where(Module.class)
                .findAll();
        realmModules.sort("displayOrder");*/


        setUpTopicsAdapter();

        return view;
    }

   /* private void setUpModulesAdapter(){
       // Log.d(TAG, "Realm results size: " + realmModules.size());
        modulesAdapter = new ModulesAdapter(realmModules, getActivity(), this, 0);
        mRecyclerView1.setHasFixedSize(true);
        horizManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView1.setLayoutManager(horizManager);
        mRecyclerView1.setAdapter(modulesAdapter);
    }*/

   private void getModulesForCategory(){
       topicsAdapter.clearData();
       mModuleViewModel.getListOfTopics(getActivity(), accessToken, category).observe(this, new Observer<List<Topic>>() {
           @Override
           public void onChanged(@Nullable List<Topic> topics) {
               for (int i = 0; i < topics.size(); i++) {
                   mTopicIDList.add(topics.get(i).getId());
                   Log.d(TAG, "getListOfTopics() topicID size:" + mTopicIDList.size());
               }
               setUpTopicsAdapter();
               mModuleViewModel.getListOfTopics(getActivity(), accessToken, category).removeObservers(getActivity());
           }
       });
   }
    private void setUpTopicsAdapter() {
        modulesTopics = new ArrayList<RealmResults<Module>>();

        //TODO: change this to livedata?
        for (int i = 0; i < mTopicIDList.size(); i++) {
            RealmResults<Module> modules = mModuleViewModel.getModulesForTopic(mTopicIDList.get(i));
            Log.d(TAG, "setUpTopicsAdapter() RealmResults<Module> size: " + modules.size());
            if (modules.size() > 0) {
                modulesTopics.add(mModuleViewModel.getModulesForTopic(mTopicIDList.get(i)));
                Log.d(TAG, "setUpTopicsAdapter() ArrayList<RealmResults<Module>> modulesTopics size: " + modulesTopics.size());
            }
            topicsAdapter.setData(modulesTopics);
        }

        //topicsAdapter = new TopicsAdapter(modulesTopics, getActivity(), this);
        Log.d(TAG, "Setting up TopicsAdapter");
        verticalRv.setHasFixedSize(true);
        vertManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        verticalRv.setLayoutManager(vertManager);
        verticalRv.setAdapter(topicsAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mModuleViewModel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mModuleViewModel.onResume();
        //setUpModulesAdapter();
        setUpTopicsAdapter();
    }

    @Override
    public void onClickModuleInTopic(Module module, int position) {
        String selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_topicsFragment_to_moduleFragment, b);
    }
}
