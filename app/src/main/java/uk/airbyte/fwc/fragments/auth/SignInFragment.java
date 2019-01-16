package uk.airbyte.fwc.fragments.auth;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
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

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AuthViewModel;


public class SignInFragment extends Fragment {

    private static final String TAG = SignInFragment.class.getSimpleName();

    @BindView(R.id.btnSignIn)
    Button signInBtn;
    @BindView(R.id.forgotPasswordBtn)
    Button forgotBtn;
    @BindView(R.id.createAccountBtn)
    Button createAccountBtn;
    @BindView(R.id.inputEmailAddress)
    TextInputEditText inputEmailAddress;
    @BindView(R.id.inputLayoutEmailAddress)
    TextInputLayout inputLayoutEmailAddress;
    @BindView(R.id.inputPassword)
    TextInputEditText inputPassword;
    @BindView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;

    private String email;
    private String password;
    private AuthViewModel mAuthViewModel;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        forgotBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_forgotFragment));
        createAccountBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_registerFragment));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuthViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
    }

    @OnClick(R.id.btnSignIn)
    public void signInAttempt(){
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        email = inputEmailAddress.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        mAuthViewModel.loginCall(getActivity() ,password, email);
    }

    private Boolean validateEmail(){
        String email = inputEmailAddress.getText().toString().trim();
        if(email.isEmpty() || !isValidEmail(email)){
            inputLayoutEmailAddress.setError("Enter a valid email address");
            inputEmailAddress.requestFocus();
            return false;
        } else {
            inputLayoutEmailAddress.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validatePassword(){
        if(inputPassword.getText().toString().trim().isEmpty()){
            inputLayoutPassword.setError("Enter the password");
            inputPassword.requestFocus();
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuthViewModel.closeRealm();
    }
}
