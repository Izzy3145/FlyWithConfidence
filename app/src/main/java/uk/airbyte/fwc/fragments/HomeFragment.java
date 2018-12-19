package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.FavouritesAdapter;
import uk.airbyte.fwc.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements FavouritesAdapter.FavouritesAdapterListener {

    private HomeViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavouritesAdapter mAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        //TODO: get list of favourites from db via viewmodel, set them to the adapter

        mRecyclerView = view.findViewById(R.id.myFavouritesRv);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FavouritesAdapter(getContext(), this);
        //set adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClickMethod(int position) {
        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_homeFragment_to_moduleFragment);

    }
}
