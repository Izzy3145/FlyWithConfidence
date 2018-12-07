package uk.airbyte.fwc.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.Navigation;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {

    //private SplashFragmentListener mListener;
    private Button getStartedBtn;
    private Button registeredBtn;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        //ButterKnife.bind(this, view);
        getStartedBtn = (Button) view.findViewById(R.id.startButton);
        getStartedBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_splash_fragment_to_registerFragment));
        registeredBtn = (Button) view.findViewById(R.id.registeredBtn);
        registeredBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_splash_fragment_to_signInFragment));
        return view;
    }
}
