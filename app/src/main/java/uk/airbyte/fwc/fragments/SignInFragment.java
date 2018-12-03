package uk.airbyte.fwc.fragments;


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
 */
public class SignInFragment extends Fragment {

    private Button signInBtn;
    private Button forgotBtn;
    private Button createAccountBtn;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        signInBtn = (Button) view.findViewById(R.id.btnSignIn);
        signInBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_home_dest));
        forgotBtn = (Button) view.findViewById(R.id.forgotPasswordBtn);
        forgotBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_forgotFragment));
        createAccountBtn = (Button) view.findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_registerFragment));
        return view;
    }

}
