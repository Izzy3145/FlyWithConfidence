package uk.airbyte.fwc.fragments.auth;


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
 */
public class ResetPwFragment extends Fragment {

    private Button setPasswordBtn;

    public ResetPwFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_reset_pw, container, false);
        //ButterKnife.bind(this, view);
        setPasswordBtn = (Button) view.findViewById(R.id.btnSetPassword);
        setPasswordBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_resetPwFragment_to_signInFragment));
        return view;
    }
}
