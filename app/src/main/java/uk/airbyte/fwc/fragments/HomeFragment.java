package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.FavouritesAdapter;
import uk.airbyte.fwc.adapters.ModulesAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements FavouritesAdapter.ModulesAdapterListener, ModulesAdapter.ModulesAdapterListener {

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
    @BindView(R.id.edit_fav_button)
    Button editFavButton;
    private HomeViewModel mHomeViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavouritesAdapter mFavouritesAdapter;
    private ModulesAdapter mModulesAdapter;
    private FragmentManager fragmentManager;
    private String videoSelected;
    private Fragment videoFragment;
    private Realm realm;
    private ArrayList<Module> favouritesList = new ArrayList<Module>(0);
    private ArrayList<Module> recentsList = new ArrayList<Module>(0);
    private String selectedModuleID;
    private int mEditting = 0;

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

        watchNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Watch now button clicked");
                videoSelected = "asset:///intro.mp4";
                watchNowBtn.setVisibility(View.GONE);
                videoOverlayGroup.setVisibility(View.GONE);
                mHomeViewModel.select(new ShowPlay(null, null, null, videoSelected));

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
        setUpFavouritesAdapter();

        editFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditting == 0) {
                    mEditting = 1;
                    editFavButton.setText(getResources().getString(R.string.done));
                    setUpFavouritesAdapter();
                    setUpRecentsAdapter();
                } else if(mEditting == 1){
                    mEditting = 0;
                    editFavButton.setText(getResources().getString(R.string.edit));
                    setUpFavouritesAdapter();
                    setUpRecentsAdapter();
                }
            }
        });

        setUpRecentsAdapter();

        return view;
    }

    private ArrayList<Module> orderModules (ArrayList<Module> listOfModules){
        Collections.sort(listOfModules, new Comparator<Module>() {
            @Override
            public int compare(Module lhs, Module rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getLastViewed() > rhs.getLastViewed() ? -1 : (lhs.getLastViewed() < rhs.getLastViewed() ) ? 1 : 0;
            }
        });

        return listOfModules;
    }

    private void setUpFavouritesAdapter(){
        mFavouritesAdapter = new FavouritesAdapter(getActivity(), favouritesList, this, mEditting);
        mFavouritesRv.setAdapter(mFavouritesAdapter);
        //TODO: move this to ViewModel
        RealmResults<Module> realmFavourites = realm.where(Module.class)
                .equalTo("favourited", true)
                .findAll();

        Log.d(TAG, "Number of favourited videos: " + String.valueOf(realmFavourites.size()));
        favouritesList.clear();
        favouritesList.addAll(realm.copyFromRealm(realmFavourites));
        orderModules(favouritesList);
        mFavouritesAdapter.setModulesToAdapter(favouritesList);
    }

    private void setUpRecentsAdapter(){
        //TODO: if all recently watched videos have been deleted, remove "Recently Watched" title
        myRecentsRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        myRecentsRv.setLayoutManager(mLayoutManager);
        mModulesAdapter = new ModulesAdapter(getActivity(), recentsList, this, mEditting);
        myRecentsRv.setAdapter(mModulesAdapter);
        //TODO: move this to ViewModel
        RealmResults<Module> realmRecents = realm.where(Module.class)
                .notEqualTo("lastViewed", 0)
                .findAll();
        Log.d(TAG, "Number of recently watched videos: " + String.valueOf(realmRecents.size()));
        recentsList.clear();
        recentsList.addAll(realm.copyFromRealm(realmRecents));
        orderModules(recentsList);
        mModulesAdapter.setModulesToAdapter(recentsList);
        if(recentsList.size() > 0){
            recentsRvGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

    }

    @Override
    public void onClickMethod(Module module, int position) {
        Log.d(TAG, "OnClickMethod clicked");
        selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_home_dest_to_moduleFragment, b);

    }

    @Override
    public void onClickRecentsDeleteMethod(Module module, int position) {
        final String unfavouritedModuleID = module.getId();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", unfavouritedModuleID)
                        .findFirst();
                unfavouritedModule.setLastViewed(0);
            }
        });

        setUpRecentsAdapter();
    }

    @Override
    public void onClickDeleteMethod(Module module, int position) {
        Log.d(TAG, "OnClickDeleteMethod clicked");
        final String unfavouritedModuleID = module.getId();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", unfavouritedModuleID)
                        .findFirst();
                unfavouritedModule.setFavourited(false);
            }
        });

        setUpFavouritesAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
       selectedModuleID = "";
       setUpFavouritesAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
      //  mHomeViewModel.getSelected().removeObservers(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
