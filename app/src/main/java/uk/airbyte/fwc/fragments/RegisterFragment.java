package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.viewmodels.AuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    @BindView(R.id.createAccountBtn)
    Button createAccountBtn;
    @BindView(R.id.alreadyRegisteredBtn)
    Button registeredBtn;
    @BindView(R.id.inputFirstName)
    TextInputEditText inputFirstName;
    @BindView(R.id.inputLayoutFirstName)
    TextInputLayout inputLayoutFirstName;
    @BindView(R.id.inputLastName)
    TextInputEditText inputLastName;
    @BindView(R.id.inputLayoutLastName)
    TextInputLayout inputLayoutLastName;
    @BindView(R.id.inputEmailAddress)
    TextInputEditText inputEmailAddress;
    @BindView(R.id.inputLayoutEmailAddress)
    TextInputLayout inputLayoutEmailAddress;
    @BindView(R.id.inputPassword)
    TextInputEditText inputPassword;
    @BindView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private OnRegisterListener mListener;
    private AuthViewModel mAuthViewModel;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        //createAccountBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_home_dest));
        registeredBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_signInFragment));
        return view;
    }

    @OnClick(R.id.createAccountBtn)
    public void createNewAccount(){

        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        firstName = inputFirstName.getText().toString().trim();
        lastName = inputLastName.getText().toString().trim();
        email = inputEmailAddress.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        //TODO: send to view model to receive accessToken, to pass to OnRegisterListener

        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_registerFragment_to_home_dest);

    }

    private Boolean validateFirstName(){
        if(inputFirstName.getText().toString().trim().isEmpty()){
            inputLayoutFirstName.setError("Enter a valid first name");
            inputFirstName.requestFocus();
            return false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateLastName(){
        if(inputLastName.getText().toString().trim().isEmpty()){
            inputLayoutLastName.setError("Enter a valid second name");
            inputLastName.requestFocus();
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }
        return true;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuthViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        //mListener.onRegister();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterListener) {
            mListener = (OnRegisterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSignInListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRegisterListener {
        void onRegister(String accessToken);
    }
}

