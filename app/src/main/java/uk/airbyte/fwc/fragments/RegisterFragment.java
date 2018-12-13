package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import io.realm.Realm;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

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
    private String userID;
    private String accessToken;
    //private OnRegisterListener mListener;
    private AuthViewModel mAuthViewModel;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Realm realm;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        if (!validateFirstName()) { return; }

        if (!validateLastName()) { return; }

        if (!validateEmail()) { return; }

        if (!validatePassword()) { return; }

        firstName = inputFirstName.getText().toString().trim();
        lastName = inputLastName.getText().toString().trim();
        email = inputEmailAddress.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        mAuthViewModel.getUserFromRegister(password, email, lastName,firstName).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user != null){
                    //editor.putString(Const.USER_ID, user.getId());
                    Log.d(TAG, "User first name: " + user.getFirstName());
                    Log.d(TAG, "User last name: " + user.getLastName());
                    Log.d(TAG, "User id: " + user.getId());
                    Log.d(TAG, "User accessToken: " + user.getAccessToken());

                    userID = user.getId();
                    accessToken = user.getAccessToken();

                    //mListener.onRegister(user.getAccessToken());
                    editor = sharedPref.edit();
                    editor.putString(Const.USER_ID, userID);
                    editor.putString(Const.ACCESS_TOKEN, accessToken);
                    editor.apply();

                    //TODO: move realm stuff to viewmodel
                    realm.executeTransactionAsync(new Realm.Transaction(){

                        @Override
                        public void execute(Realm realm) {
                            User user = realm.createObject(User.class, userID);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setEmailAddress(email);
                            user.setAccessToken(accessToken);
                        }
                    });

                    Intent openMain = new Intent(getActivity(), MainActivity.class);
                    startActivity(openMain);
                }
            }
        });
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
    }

    /*@Override
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
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

