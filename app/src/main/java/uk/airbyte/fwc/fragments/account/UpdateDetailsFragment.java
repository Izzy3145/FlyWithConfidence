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
import io.realm.Realm;
import io.realm.RealmResults;
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
    private String mUserID;
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
    private Realm realm;

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
        mUserID = sharedPref.getString(Const.USER_ID, "");

        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        Log.d(TAG, "User access token: " + mAccessToken);
        Log.d(TAG, "User ID: " + mUserID);

        realm = Realm.getDefaultInstance();
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
    public void saveUserProfile() {
        final String firstName = inputFirstName.getText().toString();
        final String lastName = inputLastName.getText().toString();
        final String email = inputEmail.getText().toString();
        mViewModel.updateUserProfile(mAccessToken, firstName, lastName, email).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                //TODO: show some snackbar with confirmation of update
                if (user != null) {

                    mUserID = user.getId();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            //TODO: move off fragment
                            User user = realm.where(User.class).equalTo("id", mUserID).findFirst();
                            if (user != null) {
                                user.setFirstName(firstName);
                                user.setLastName(lastName);
                                user.setEmailAddress(email);
                                Log.d(TAG, "User first name: " + user.getFirstName());
                                Log.d(TAG, "User last name: " + user.getLastName());
                                Log.d(TAG, "User id: " + user.getId());
                                Log.d(TAG, "User accessToken: " + user.getAccessToken());
                            }
                        }
                    });

                    Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).
                            navigate(R.id.action_updateDetailsFragment_to_accountFragment);
                }
            }
        });
    }


    public void getUserDetails() {
        mViewModel.getUserProfile(mAccessToken).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
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