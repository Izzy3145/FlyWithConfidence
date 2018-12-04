package uk.airbyte.fwc.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotFragment extends Fragment {
    @BindView(R.id.btnSignIn)
    Button signInBtn;
    @BindView(R.id.backSignIn)
    Button backSignInBtn;

    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);
        ButterKnife.bind(this, view);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Check your emails", Toast.LENGTH_SHORT).show();
            }
        });
        backSignInBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_forgotFragment_to_signInFragment));
        return view;
    }
}
