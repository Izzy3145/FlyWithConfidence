package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.FavouritesAdapter;
import uk.airbyte.fwc.fragments.account.UpdateDetailsFragment;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements FavouritesAdapter.FavouritesAdapterListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.myFavouritesRv)
    RecyclerView mRecyclerView;
    @BindView(R.id.watchNowBtn)
    Button watchNowBtn;
    @BindView(R.id.video_overlay)
    ConstraintLayout videoOverlay;
    private HomeViewModel mHomeViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavouritesAdapter mAdapter;
    private FragmentManager fragmentManager;
    private String videoSelected;
    private Fragment videoFragment;
    private Realm realm;

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

        //TODO: get list of favourites from db via viewmodel, set them to the adapter

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FavouritesAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);

        watchNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Watch now button clicked");
                videoOverlay.setVisibility(View.GONE);
                videoSelected = "asset:///intro.mp4";
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                //TODO: make this work
                resizeFragment(videoFragment, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                mHomeViewModel.select(videoSelected);
            }
        });

        //TODO: move this to ViewModel
        RealmResults<Module> result2 = realm.where(Module.class)
                .equalTo("favourited", true)
                .findAll();

        Log.d(TAG, "Number of favourited videos: " + String.valueOf(result2.size()));


        for(int i = 0; i < result2.size(); i++){
            Log.d(TAG, "Favourited module title: " + result2.get(i).getName());
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

    }

    @Override
    public void onClickMethod(int position) {
        Log.d(TAG, "OnClick method clicked");
        //TODO: make nav controller work
        //Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_home_to_module);
        ModuleFragment moduleFragment = new ModuleFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.my_nav_host_fragment, moduleFragment)
                //.addToBackStack(null)
                .commit();
    }

    private void resizeFragment(Fragment f, int newWidth, int newHeight) {
        if (f != null) {
            View view = f.getView();
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(newWidth, newHeight);
            view.setLayoutParams(p);
            view.requestLayout();


        }
    }
}
