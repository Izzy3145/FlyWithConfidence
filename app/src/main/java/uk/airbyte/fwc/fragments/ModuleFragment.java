package uk.airbyte.fwc.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import uk.airbyte.fwc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModuleFragment extends Fragment {


    public ModuleFragment() {
        // Required empty public constructor
    }

    //TODO: button to go to next module, from list of modules
    //TODO: if module unlocked, show descriptions, otherwise show unlock button

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));
        ButterKnife.bind(this, view);
        return view;
    }

}
