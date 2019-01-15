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
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.TopicsAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.Topic;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.ModuleViewModel;

public class TopicsFragment extends Fragment implements TopicsAdapter.TopicsAdapterListener {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    @BindView(R.id.verticalTopicsRv)
    RecyclerView verticalRv;
    private ArrayList<RealmResults<Module>> preparationModules = new ArrayList<RealmResults<Module>>();
    private ArrayList<RealmResults<Module>> knowledgeModules = new ArrayList<RealmResults<Module>>();
    private ModuleViewModel mModuleViewModel;
    private String accessToken;
    private SharedPreferences sharedPref;
    private RecyclerView.LayoutManager vertManager;
    private TopicsAdapter topicsAdapter;
    private String category;
    private List<String> mKnowledgeTopicIDList;
    private List<String> mPreparationTopicIDList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        accessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        mModuleViewModel = ViewModelProviders.of(getActivity()).get(ModuleViewModel.class);
        topicsAdapter = new TopicsAdapter(knowledgeModules, getActivity(), this);
        category = Const.API_KNOWLEDGE;

        //getKnowledgeTopicsAndModules();
       // getPreparationTopicsAndModules();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topics_fragment, container, false);
        ButterKnife.bind(this, view);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.top_tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        category = Const.API_KNOWLEDGE;
                        topicsAdapter.clearData();
                        setUpKnowledgeAdapter();
                        break;
                    case 1:
                        category = Const.API_PREPARATION;
                        topicsAdapter.clearData();
                        setUpPreparationAdapter();
                        break;
                    default:
                        category = Const.API_KNOWLEDGE;
                        topicsAdapter.clearData();
                        setUpKnowledgeAdapter();

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

        setUpKnowledgeAdapter();

        return view;
    }

    /*private void getKnowledgeTopicsAndModules() {
        mKnowledgeTopicIDList = new ArrayList<String>();
        mModuleViewModel.getKnowledgeTopicsAndModules(getActivity(), accessToken).observe(this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(@Nullable List<Topic> topics) {
                for (int i = 0; i < topics.size(); i++) {
                    //create a list of local topic IDs, to help with setting up the double adapter

                    mKnowledgeTopicIDList.add(topics.get(i).getId());
                    Log.d(TAG, "getKnowledgeTopicsAndModules() topicID List size:" + mKnowledgeTopicIDList.size());
                }
                setUpTopicsAdapter();
                mModuleViewModel.getKnowledgeTopicsAndModules(getActivity(), accessToken).removeObservers(getActivity());
            }
        });
    }

    private void getPreparationTopicsAndModules() {
        mPreparationTopicIDList = new ArrayList<String>();
        mModuleViewModel.getPreparationTopicsAndModules(getActivity(), accessToken).observe(this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(@Nullable List<Topic> topics) {
                for (int i = 0; i < topics.size(); i++) {
                    //create a list of local topic IDs, to help with setting up the double adapter
                    mPreparationTopicIDList.add(topics.get(i).getId());
                    Log.d(TAG, "getPreparationTopicsAndModules() preparation topicID size:" + mPreparationTopicIDList.size());
                }
                setUpTopicsAdapter();
                mModuleViewModel.getPreparationTopicsAndModules(getActivity(), accessToken).removeObservers(getActivity());
            }
        });
    }

    private void setUpTopicsAdapter() {
        if (category.equals(Const.API_KNOWLEDGE)) {
            knowledgeModules = new ArrayList<RealmResults<Module>>();
            for (int i = 0; i < mKnowledgeTopicIDList.size(); i++) {
                //get individual list of modules for each topic from Realm
                RealmResults<Module> modules = mModuleViewModel.getModulesForTopic(mKnowledgeTopicIDList.get(i));
                Log.d(TAG, "setUpTopicsAdapter() RealmResults<Module> size: " + modules.size());
                //only create a recycler view if there are some modules in the topic
                if (modules.size() > 0) {
                    knowledgeModules.add(mModuleViewModel.getModulesForTopic(mKnowledgeTopicIDList.get(i)));
                    Log.d(TAG, "setUpTopicsAdapter() ArrayList<RealmResults<Module>> preparationModules size: " + knowledgeModules.size());
                }
                topicsAdapter.setData(knowledgeModules);
            }

        } else if (category.equals(Const.API_PREPARATION)) {
            preparationModules = new ArrayList<RealmResults<Module>>();
            for (int i = 0; i < mPreparationTopicIDList.size(); i++) {
                //get individual list of modules for each topic from Realm
                RealmResults<Module> modules = mModuleViewModel.getModulesForTopic(mPreparationTopicIDList.get(i));
                Log.d(TAG, "setUpTopicsAdapter() RealmResults<Module> size: " + modules.size());
                //only create a recycler view if there are some modules in the topic
                if (modules.size() > 0) {
                    preparationModules.add(mModuleViewModel.getModulesForTopic(mPreparationTopicIDList.get(i)));
                    Log.d(TAG, "setUpTopicsAdapter() ArrayList<RealmResults<Module>> preparationModules size: " + preparationModules.size());
                }
                topicsAdapter.setData(preparationModules);
            }
        }

        verticalRv.setHasFixedSize(true);
        vertManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        verticalRv.setLayoutManager(vertManager);
        verticalRv.setAdapter(topicsAdapter);
    }*/

    private void setUpKnowledgeAdapter(){
        knowledgeModules = mModuleViewModel.getKnowledgeAdapterData();
        topicsAdapter.setData(knowledgeModules);
        verticalRv.setHasFixedSize(true);
        vertManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        verticalRv.setLayoutManager(vertManager);
        verticalRv.setAdapter(topicsAdapter);
    }

    private void setUpPreparationAdapter(){
        preparationModules = mModuleViewModel.getKnowledgeAdapterData();
        topicsAdapter.setData(preparationModules);
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
        if (category.equals(Const.API_KNOWLEDGE)) {
            topicsAdapter.clearData();
            setUpKnowledgeAdapter();
        } else {
            category = Const.API_PREPARATION;
            topicsAdapter.clearData();
            setUpPreparationAdapter();
        }
    }

    @Override
    public void onClickModuleInTopic(Module module, int position) {
        String selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_topicsFragment_to_moduleFragment, b);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mModuleViewModel.closeRealm();
    }
}
