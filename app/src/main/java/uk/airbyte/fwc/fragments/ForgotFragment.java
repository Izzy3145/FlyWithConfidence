package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.viewmodels.AuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotFragment extends Fragment {

    private static final String TAG = ForgotFragment.class.getSimpleName();

    @BindView(R.id.btnSignIn)
    Button signInBtn;
    @BindView(R.id.backSignIn)
    Button backSignInBtn;
    @BindView(R.id.inputEmailAddress)
    TextInputEditText inputEmailAddress;
    @BindView(R.id.inputLayoutEmailAddress)
    TextInputLayout inputLayoutEmailAddress;

    private String email;
    private AuthViewModel mAuthViewModel;


    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);
        ButterKnife.bind(this, view);
        /*signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Check your emails", Toast.LENGTH_SHORT).show();
            }
        });*/
        backSignInBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_forgotFragment_to_signInFragment));
        return view;
    }

    @OnClick(R.id.btnSignIn)
    public void signIn(){
        if (!validateEmail()) {
            return;
        }

        email = inputEmailAddress.getText().toString().trim();

        Log.d(TAG, "Email address: " + email);

        mAuthViewModel.getForgottenPw(email).observe(this, new Observer<Reminder>() {
            @Override
            public void onChanged(@Nullable Reminder reminder) {
                if(reminder != null){
                    Log.d(TAG, "Boolean received: " + reminder.toString());
                    Toast.makeText(getActivity(),"Check your emails", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Boolean validateEmail() {
        String email = inputEmailAddress.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmailAddress.setError("Enter a valid email address");
            inputEmailAddress.requestFocus();
            return false;
        } else {
            inputLayoutEmailAddress.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tells the fragment that its activity has completed its own Activity.onCreate()
        mAuthViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
    }
}
