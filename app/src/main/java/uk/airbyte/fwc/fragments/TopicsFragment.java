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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.FavouritesAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.TopicsViewModel;

public class TopicsFragment extends Fragment implements FavouritesAdapter.ModulesAdapterListener {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    @BindView (R.id.topicsRv)
    RecyclerView mRecyclerView;
    private TopicsViewModel mViewModel;
    private String accessToken;
    private SharedPreferences sharedPref;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavouritesAdapter mAdapter;
    private Realm realm;
    private FragmentManager fragmentManager;

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


        mViewModel.getModulesForTopic(getActivity(), accessToken, "74e89e6e-fb05-4a59-ac5d-eb2e937bad16")
                .observe(this, new Observer<List<Module>>() {
            @Override
            public void onChanged(@Nullable final List<Module> modules) {
                if(modules != null){
                    for(int i = 0; i<modules.size(); i++){
                        Module module = modules.get(i);
                        //TODO: remove
                        module.setFavourited(true);
                        Log.d(TAG, module.getName());
                    }

                    //TODO: remove
                    Module recentModule1 = modules.get(1);
                    recentModule1.setLastViewed(3);
                    Module recentModule2 = modules.get(1);
                    recentModule2.setLastViewed(2);
                    Module recentModule3 = modules.get(1);
                    recentModule3.setLastViewed(1);

                    realm.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm) {
                            for(int i = 0; i<modules.size(); i++){
                                Module module = modules.get(i);
                                realm.copyToRealmOrUpdate(module);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.topics_fragment, container, false);
        ButterKnife.bind(this, view);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.top_tabs);
        tablayout.setVisibility(View.VISIBLE);
        //TODO: set up onClickListener to query other endpoint

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FavouritesAdapter(getActivity(), new ArrayList<Module>(0), this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TopicsViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onClickMethod(Module module, int position) {
        Log.d(TAG, "OnClick method clicked");
        //TODO: (2) pass module item to ModuleFragment for viewing, implement onBackPressed
        //Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_home_to_module);
        ModuleFragment moduleFragment = new ModuleFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.my_nav_host_fragment, moduleFragment)
                //.addToBackStack(null)
                .commit();
    }

    @Override
    public void onClickDeleteMethod(Module module, int position) {
        Log.d(TAG, "OnClickDeleteMethod clicked");

    }
}
