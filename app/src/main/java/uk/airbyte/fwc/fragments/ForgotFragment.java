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
public class ForgotFragment extends Fragment {

    private Button signInBtn;
    private Button backSignInBtn;

    public ForgotFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);
        signInBtn = (Button) view.findViewById(R.id.btnSignIn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.resetPwFragment);
            }
        });
        signInBtn = (Button) view.findViewById(R.id.backSignIn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.signInFragment);
            }
        });
        return view;
    }
}
