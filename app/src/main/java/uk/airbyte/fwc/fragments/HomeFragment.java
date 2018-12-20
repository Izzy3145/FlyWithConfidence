package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.FavouritesAdapter;
import uk.airbyte.fwc.fragments.account.UpdateDetailsFragment;
import uk.airbyte.fwc.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements FavouritesAdapter.FavouritesAdapterListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.myFavouritesRv)
    RecyclerView mRecyclerView;
    @BindView(R.id.watchNowBtn)
    Button watchNowBtn;
    private HomeViewModel mHomeViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavouritesAdapter mAdapter;
    private FragmentManager fragmentManager;
    private String videoSelected;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        //TODO: get list of favourites from db via viewmodel, set them to the adapter

        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FavouritesAdapter(getActivity(), this);
        //set adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);

        //videoSelected = "android.resource://" + getActivity().getPackageName() +"/" + R.raw.intro_video;
        videoSelected = "https://player.vimeo.com/external/231066073.hd.mp4?s=e53afa45b4ad1b2848499fca912607b98b80e8bb&profile_id=175";
        Log.d(TAG, "Video selected: " + videoSelected);
        mHomeViewModel.select(videoSelected);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        watchNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Watch now button clicked");
                //videoSelected = "android.resource://" + getActivity().getPackageName() +"/" + R.raw.intro_video;

                mHomeViewModel.select(videoSelected);

                // https://player.vimeo.com/external/231066073.hd.mp4?s=e53afa45b4ad1b2848499fca912607b98b80e8bb&profile_id=175
            }
        });
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
}
