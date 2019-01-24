package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import uk.airbyte.fwc.MainActivity;
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
    @BindView(R.id.home_intro_play_btn)
    Button introPlayBtn;
    ///@BindView(R.id.intro_pause_btn)
   // Button introPauseBtn;
    //@BindView(R.id.videoOverlayGroup)
    //Group videoOverlayGroup;
    @BindView(R.id.recentsGroup)
    Group recentsGroup;
    @BindView(R.id.favouritesGroup)
    Group favouritesGroup;
    @BindView(R.id.homeLayout)
    ConstraintLayout videoParent;
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
    private String selectedModuleID;
    private int mEditing;
    private RealmResults<Module> realmRecents;
    private RealmResults<Module> realmFavourites;
    private long mLastClickTime = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        videoFragment = fragmentManager.findFragmentById(R.id.videoFragment);
        mVideoViewModel = new VideoViewModel();
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mModuleViewModel = new ModuleViewModel();
        mModuleViewModel = ViewModelProviders.of(getActivity()).get(ModuleViewModel.class);

        if (savedInstanceState == null) {
            mEditing = 0;
        } else {
            mEditing = savedInstanceState.getInt(Const.EDITING_STATUS);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        introPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSelected = "asset:///intro.mp4";
                introPlayBtn.setVisibility(View.GONE);
                /*introPauseBtn.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(3000);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                videoOverlayGroup.startAnimation(anim);*/
                mVideoViewModel.select(new ShowPlay(null, null, null,
                        videoSelected, 0, 0, true));
                ((MainActivity) getActivity()).hideNavBarAndLandscape();
            }
        });

        setUpFavouritesAdapter();
        setUpRecentsAdapter();

        editFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditing == 0) {
                    mEditing = 1;
                    setEditBtn();
                    setUpRecentsAdapter();
                    setUpFavouritesAdapter();
                } else if (mEditing == 1) {
                    mEditing = 0;
                    setEditBtn();
                    setUpRecentsAdapter();
                    setUpFavouritesAdapter();
                }
            }
        });
        setEditBtn();
        return view;
    }

    private void setEditBtn() {
        if (mEditing == 0) {
            editFavButton.setText(getResources().getString(R.string.edit));
        } else if (mEditing == 1) {
            editFavButton.setText(getResources().getString(R.string.done));
        }
    }

    private void setUpFavouritesAdapter() {
        realmFavourites = mModuleViewModel.getFavourites();
        mFavouritesRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFavouritesRv.setLayoutManager(mLayoutManager);
        mFavouritesAdapter = new FavouritesAdapter(realmFavourites, getActivity(), this, mEditing);
        mFavouritesRv.setAdapter(mFavouritesAdapter);
    }

    private void setUpRecentsAdapter() {
        realmRecents = mModuleViewModel.getRecents();
        if (realmRecents != null && realmRecents.size() > 0) {
            recentsGroup.setVisibility(View.VISIBLE);
        }
        myRecentsRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        myRecentsRv.setLayoutManager(mLayoutManager);
        mModulesAdapter = new ModulesAdapter(realmRecents, getActivity(), this, mEditing, true);
        myRecentsRv.setAdapter(mModulesAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
    }

    @Override
    public void onClickModule(Module module, int position) {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_home_to_module, b);
    }

    @Override
    public void onClickRecentsDelete(Module module, int position) {
        mModuleViewModel.deleteRecent(module.getId());
        setUpRecentsAdapter();
    }

    @Override
    public void onClickFavMethod(Module module, int position) {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        selectedModuleID = "";
        selectedModuleID = module.getId();
        Bundle b = new Bundle();
        b.putString(Const.MODULE_ID, selectedModuleID);
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_home_to_module, b);
    }

    @Override
    public void onClickFavDeleteMethod(Module module, int position) {
        mModuleViewModel.deleteFavourite(module.getId());
        setUpFavouritesAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedModuleID = "";
        setUpFavouritesAdapter();
        setUpRecentsAdapter();
        mModuleViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mModuleViewModel.onPause();
        setUpFavouritesAdapter();
        setUpRecentsAdapter();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Const.EDITING_STATUS, mEditing);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mModuleViewModel.closeRealm();
        mVideoViewModel.closeRealm();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // landscape!
            recentsGroup.setVisibility(View.GONE);
            favouritesGroup.setVisibility(View.GONE);
            ((MainActivity) getActivity()).hideNavBar();

            ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT);
            videoFragParent.setLayoutParams(p);
            videoFragParent.requestLayout();

            videoFragParent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
          //          videoOverlayGroup.setVisibility(View.VISIBLE);
                    return false;
                }
            });
  ;

        } else {
            // portrait!
            ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD);
            p.dimensionRatio = "h, 1:1";
            videoFragParent.setLayoutParams(p);

            ConstraintSet set = new ConstraintSet();
            set.clone(videoFragParent);
            set.connect(R.id.videoFragParent, ConstraintSet.END, R.id.homeLayout, ConstraintSet.END);
            set.connect(R.id.videoFragParent, ConstraintSet.START, R.id.homeLayout, ConstraintSet.START);
            set.connect(R.id.videoFragParent, ConstraintSet.TOP, R.id.homeLayout, ConstraintSet.TOP);
            set.applyTo(videoFragParent);

            introPlayBtn.setVisibility(View.VISIBLE);
         //   videoOverlayGroup.setVisibility(View.VISIBLE);
            recentsGroup.setVisibility(View.VISIBLE);
            favouritesGroup.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).showNavBar();

        }
    }

}
