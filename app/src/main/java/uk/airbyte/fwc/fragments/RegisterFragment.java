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
public class RegisterFragment extends Fragment {

    private Button createAccountBtn;
    private Button registeredBtn;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        createAccountBtn = (Button) view.findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_home_dest));
        registeredBtn = (Button) view.findViewById(R.id.alreadyRegisteredBtn);
        registeredBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_signInFragment));
        return view;
    }
}
