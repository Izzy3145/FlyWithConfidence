package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.ModulesAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements ModulesAdapter.ModulesAdapterListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.myFavouritesRv)
    RecyclerView mFavouritesRv;
    @BindView(R.id.myRecentsRv)
    RecyclerView myRecentsRv;
    @BindView(R.id.watchNowBtn)
    Button watchNowBtn;
    @BindView(R.id.videoOverlayGroup)
    Group videoOverlayGroup;
    @BindView(R.id.recentsGroup)
    Group recentsRvGroup;
    @BindView(R.id.videoFragParent)
    ConstraintLayout videoFragParent;
    private HomeViewModel mHomeViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private ModulesAdapter mFavouritesAdapter;
    private ModulesAdapter mRecentsAdapter;
    private FragmentManager fragmentManager;
    private String videoSelected;
    private Fragment videoFragment;
    private Realm realm;
    private ArrayList<Module> favouritesList = new ArrayList<Module>(0);
    private ArrayList<Module> recentsList = new ArrayList<Module>(0);
    private String selectedModuleID;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        videoFragment = fragmentManager.findFragmentById(R.id.videoFragment);
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        //TODO: (4) add an edit button that writes to Realm to reset favourites/lastViewed values

        watchNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Watch now button clicked");
                videoSelected = "asset:///intro.mp4";
                watchNowBtn.setVisibility(View.GONE);
                videoOverlayGroup.setVisibility(View.GONE);
                mHomeViewModel.select(new ShowPlay(null, null, videoSelected));

                //TODO: (1) Make this work in landscape, implement onBackPressed
                //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                videoFragParent.setLayoutParams(p);
                videoFragParent.requestLayout();
                //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
        });

        //set up favourites recycler view and adapter, get info from Realm
        mFavouritesRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFavouritesRv.setLayoutManager(mLayoutManager);
        mFavouritesAdapter = new ModulesAdapter(getActivity(), favouritesList, this);
        mFavouritesRv.setAdapter(mFavouritesAdapter);

        //TODO: move this to ViewModel
        RealmResults<Module> realmFavourites = realm.where(Module.class)
                .equalTo("favourited", true)
                .findAll();

        Log.d(TAG, "Number of favourited videos: " + String.valueOf(realmFavourites.size()));
        favouritesList.clear();
        favouritesList.addAll(realm.copyFromRealm(realmFavourites));
        Collections.sort(favouritesList, new Comparator<Module>() {
            @Override
            public int compare(Module lhs, Module rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getLastViewed() > rhs.getLastViewed() ? -1 : (lhs.getLastViewed() < rhs.getLastViewed() ) ? 1 : 0;
            }
        });
        mFavouritesAdapter.setModulesToAdapter(favouritesList);
        for(int i = 0; i < realmFavourites.size(); i++){
            Log.d(TAG, "Favourited module title: " + realmFavourites.get(i).getName());
        }

        //set up recents recycler view and adapter
        myRecentsRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        myRecentsRv.setLayoutManager(mLayoutManager);

        mRecentsAdapter = new ModulesAdapter(getActivity(), recentsList, this);
        myRecentsRv.setAdapter(mRecentsAdapter);

        RealmResults<Module> realmRecents = realm.where(Module.class)
                .notEqualTo("lastViewed", 0)
                .findAll();
        Log.d(TAG, "Number of recently watched videos: " + String.valueOf(realmRecents.size()));
        recentsList.clear();
        recentsList.addAll(realm.copyFromRealm(realmRecents));
        Collections.sort(recentsList, new Comparator<Module>() {
            @Override
            public int compare(Module lhs, Module rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getLastViewed() > rhs.getLastViewed() ? -1 : (lhs.getLastViewed() < rhs.getLastViewed() ) ? 1 : 0;
            }
        });
        mRecentsAdapter.setModulesToAdapter(recentsList);
        if(recentsList.size() > 0){
            recentsRvGroup.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

    }

    @Override
    public void onClickMethod(Module module, int position) {
        Log.d(TAG, "OnClick method clicked");
        selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_home_dest_to_moduleFragment, b);

    }

    @Override
    public void onResume() {
        super.onResume();
       selectedModuleID = "";
    }
}
