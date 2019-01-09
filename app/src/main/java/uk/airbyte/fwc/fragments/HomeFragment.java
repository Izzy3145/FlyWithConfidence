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
import uk.airbyte.fwc.viewmodels.ModuleViewModel;
import uk.airbyte.fwc.viewmodels.VideoViewModel;

public class HomeFragment extends Fragment implements FavouritesAdapter.FavouritesAdapterListener, ModulesAdapter.ModulesAdapterListener {

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

    private VideoViewModel mVideoViewModel;
    private ModuleViewModel mModuleViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavouritesAdapter mFavouritesAdapter;
    private ModulesAdapter mModulesAdapter;
    private FragmentManager fragmentManager;
    private String videoSelected;
    private Fragment videoFragment;
    private Realm realm;
    private String selectedModuleID;
    private int mEditing = 0;
    private RealmResults<Module> realmRecents;
    private RealmResults<Module> realmFavourites;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        videoFragment = fragmentManager.findFragmentById(R.id.videoFragment);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mModuleViewModel = ViewModelProviders.of(getActivity()).get(ModuleViewModel.class);
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
                //TODO: get player position of intro video (not cached in db)
                mVideoViewModel.select(new ShowPlay(null, null, null, videoSelected, 0, 0));

                //TODO: (1) Make this work in landscape, implement onBackPressed
                //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                videoFragParent.setLayoutParams(p);
                videoFragParent.requestLayout();
                //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
        });

        setUpFavouritesAdapter();

        editFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditing == 0) {
                    mEditing = 1;
                    editFavButton.setText(getResources().getString(R.string.done));
                    setUpRecentsAdapter();
                    setUpFavouritesAdapter();
                } else if(mEditing == 1){
                    mEditing = 0;
                    editFavButton.setText(getResources().getString(R.string.edit));
                    setUpRecentsAdapter();
                    setUpFavouritesAdapter();
                }
            }
        });

        setUpRecentsAdapter();

        return view;
    }

    private void setUpFavouritesAdapter(){

        //TODO: move this to ViewModel
        /*realmFavourites = realm.where(Module.class)
                .equalTo("favourited", true)
                .findAll();
        realmFavourites.sort("lastViewed", Sort.DESCENDING);*/
        realmFavourites = mModuleViewModel.getFavourites();
        mFavouritesRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFavouritesRv.setLayoutManager(mLayoutManager);
        mFavouritesAdapter = new FavouritesAdapter(realmFavourites, getActivity(), this, mEditing);
        mFavouritesRv.setAdapter(mFavouritesAdapter);

        Log.d(TAG, "Number of favourited videos: " + String.valueOf(realmFavourites.size()));
    }

    private void setUpRecentsAdapter(){
       /* realmRecents = realm.where(Module.class)
                .notEqualTo("lastViewed", 0)
                .findAll();
        realmRecents.sort("lastViewed", Sort.DESCENDING);*/
        realmRecents = mModuleViewModel.getRecents();
        if(realmRecents!=null){
            recentsRvGroup.setVisibility(View.VISIBLE);
        }
        myRecentsRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        myRecentsRv.setLayoutManager(mLayoutManager);
        mModulesAdapter = new ModulesAdapter(realmRecents, getActivity(), this, mEditing);
        myRecentsRv.setAdapter(mModulesAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);

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
        /*realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", unfavouritedModuleID)
                        .findFirst();
                unfavouritedModule.setLastViewed(0);
            }
        });*/

        mModuleViewModel.deleteRecent(module.getId());
        setUpRecentsAdapter();
    }

    @Override
    public void onClickDeleteMethod(Module module, int position) {
        mModuleViewModel.deleteFavourite(module.getId());
        /*final String unfavouritedModuleID = module.getId();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", unfavouritedModuleID)
                        .findFirst();
                unfavouritedModule.setFavourited(false);
            }
        });
*/
        setUpFavouritesAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
       selectedModuleID = "";
       setUpFavouritesAdapter();
      mModuleViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
       mModuleViewModel.onPause();
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
