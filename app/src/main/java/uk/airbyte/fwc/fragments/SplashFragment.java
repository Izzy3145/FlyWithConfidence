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
        getStartedBtn = (Button) view.findViewById(R.id.startButton);
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.registerFragment);
            }
        });
        registeredBtn = (Button) view.findViewById(R.id.registeredBtn);
        registeredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.signInFragment);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SplashFragmentListener) {
            mListener = (SplashFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SplashFragmentListener");
        }
    }

    public interface SplashFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInter*/
}
