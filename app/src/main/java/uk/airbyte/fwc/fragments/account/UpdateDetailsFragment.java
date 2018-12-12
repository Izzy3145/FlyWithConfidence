package uk.airbyte.fwc.fragments.account;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import uk.airbyte.fwc.fragments.ForgotFragment;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AccountViewModel;
import uk.airbyte.fwc.viewmodels.AuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateDetailsFragment extends Fragment {

    private static final String TAG = UpdateDetailsFragment.class.getSimpleName();

    private AccountViewModel mViewModel;
    private String mAccessToken;
    private SharedPreferences sharedPref;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.inputFirstName)
    TextInputEditText inputFirstName;
    @BindView(R.id.inputLastName)
    TextInputEditText inputLastName;
    @BindView(R.id.inputEmail)
    TextInputEditText inputEmail;

    public UpdateDetailsFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        Log.d(TAG, "User access token: " + mAccessToken);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_details, container, false);
        ButterKnife.bind(this, view);
        cancelBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_updateDetailsFragment_to_accountFragment));
        getUserDetails();
        return view;
    }

    @OnClick(R.id.saveBtn)
    public void saveUserProfile(){
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String email = inputEmail.getText().toString();
        mViewModel.updateUserProfile(mAccessToken, firstName, lastName, email).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                //TODO: show some snackbar with confirmation of update
                //TODO: update Realm object
                Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).
                        navigate(R.id.action_updateDetailsFragment_to_accountFragment);
            }
        });
    }


    public void getUserDetails(){
        mViewModel.getUserProfile(mAccessToken).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user != null){
                    Log.d(TAG, "User first name: " + user.getFirstName());
                    Log.d(TAG, "User last name: " + user.getLastName());
                    Log.d(TAG, "User id: " + user.getId());
                    Log.d(TAG, "User accessToken: " + user.getAccessToken());
                    inputFirstName.setText(user.getFirstName());
                    inputLastName.setText(user.getLastName());
                    inputEmail.setText(user.getEmailAddress());
                }
            }
        });
    }
}
