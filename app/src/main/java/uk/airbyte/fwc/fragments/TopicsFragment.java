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
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.ModulesAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.Topic;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.TopicsViewModel;

public class TopicsFragment extends Fragment implements ModulesAdapter.ModulesAdapterListener {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    @BindView(R.id.topicsRv1)
    RecyclerView mRecyclerView1;
    @BindView(R.id.topicsRv2)
    RecyclerView mRecyclerView2;
    @BindView(R.id.topicsRv3)
    RecyclerView mRecyclerView3;
    @BindView(R.id.topicsRv4)
    RecyclerView mRecyclerView4;
    @BindView(R.id.topicsRv5)
    RecyclerView mRecyclerView5;
    @BindView(R.id.topicsRv6)
    RecyclerView mRecyclerView6;
    @BindView(R.id.topicsRv7)
    RecyclerView mRecyclerView7;
    @BindView(R.id.topicsRv8)
    RecyclerView mRecyclerView8;
    @BindView(R.id.topicsRv9)
    RecyclerView mRecyclerView9;
    @BindView(R.id.topicsRv10)
    RecyclerView mRecyclerView10;

    private TopicsViewModel mViewModel;
    private String accessToken;
    private SharedPreferences sharedPref;
    private RecyclerView.LayoutManager mLayoutManager;
    private ModulesAdapter mAdapter;
    private Realm realm;
    private FragmentManager fragmentManager;
    private String category;
    private ArrayList<Module> moduleList = new ArrayList<Module>();
    private List<String> mTopicIDList = new ArrayList<String>();
    private int numberOfTopics = 0;

    public static TopicsFragment newInstance() {
        return new TopicsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        accessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        fragmentManager = getFragmentManager();
        mViewModel = ViewModelProviders.of(this).get(TopicsViewModel.class);
        realm = Realm.getDefaultInstance();

        category = "knowledge";

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topics_fragment, container, false);
        ButterKnife.bind(this, view);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.top_tabs);
        tablayout.setVisibility(View.VISIBLE);
        //TODO: set up onClickListener on TabLayout to query other endpoint

        //TODO: learn DataBinding to make this easier?
        mRecyclerView1.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView1.setLayoutManager(mLayoutManager);
        mAdapter = new ModulesAdapter(getActivity(), moduleList, this);
        mRecyclerView1.setAdapter(mAdapter);

        mRecyclerView2.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel.getModulesFromTopics(getActivity(), accessToken, category).observe(this, new Observer<List<Module>>() {
            @Override
            public void onChanged(@Nullable List<Module> modules) {
                if (modules != null) {
                    moduleList.addAll(modules);
                    numberOfTopics++;

                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(moduleList);
                        }
                    });

                    for (int i = 0; i < modules.size(); i++) {
                        Log.d(TAG, "topicID: " + modules.get(i).getTopic().getId() + " Module name: " + modules.get(i).getName());
                    }

                    mAdapter.setModulesToAdapter(moduleList);

                }

                //TODO: check this is looping correctly when data from other topics is not null

                Log.d(TAG, "moduleList size: " + moduleList.size());
                Log.d(TAG, "Number of Topics found: " + numberOfTopics);

            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onClickMethod(Module module, int position) {
        String selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_topicsFragment_to_moduleFragment, b);
    }

    @Override
    public void onClickRecentsDeleteMethod(Module module, int position) {

    }
}
