package uk.airbyte.fwc.fragments.account;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.airbyte.fwc.R;
import uk.airbyte.fwc.viewmodels.AuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePwFragment extends Fragment {

    private AuthViewModel mAuthViewModel;

    public ChangePwFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_pw, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tells the fragment that its activity has completed its own Activity.onCreate()
        mAuthViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
    }
}
