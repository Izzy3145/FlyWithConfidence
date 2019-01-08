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

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AccountViewModel;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        mUserID = sharedPref.getString(Const.USER_ID, "");
        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       //FragmentUpdateDetailsBinding binding = FragmentUpdateDetailsBinding.inflate(inflater, container, false);
       //binding.setAccountviewmodel(mViewModel);
        //binding.setLifecycleOwner(this);
       // User user = new User(null, "Izzy", "Stannett", "izzystannett@gmail.com");
        //binding.setUser(user);
        //View view = binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_update_details, container, false);
        ButterKnife.bind(this, view);
        cancelBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_updateDetailsFragment_to_accountFragment));
        getUserDetails();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
    }

    @OnClick(R.id.saveBtn)
    public void saveUserProfile() {
        final String firstName = inputFirstName.getText().toString();
        final String lastName = inputLastName.getText().toString();
        final String email = inputEmail.getText().toString();

        mViewModel.putUserProfile(getActivity(), mAccessToken, firstName, lastName, email);

        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).
                navigate(R.id.action_updateDetailsFragment_to_accountFragment);


       /* mViewModel.updateUserProfile(getActivity(), mAccessToken, firstName, lastName, email).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {

                    mUserID = user.getId();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            //TODO: move off fragment - no longer observe LiveData here
                            User user = realm.where(User.class).equalTo("id", mUserID).findFirst();
                            if (user != null) {
                                user.setFirstName(firstName);
                                user.setLastName(lastName);
                                user.setEmailAddress(email);
                                Log.d(TAG, "Realm user first name: " + user.getFirstName());
                                Log.d(TAG, "Realm user last name: " + user.getLastName());
                                Log.d(TAG, "Realm user id: " + user.getId());
                                Log.d(TAG, "Realm user accessToken: " + user.getAccessToken());
                            }
                        }
                    });
                }
            }
        });*/
    }


    public void getUserDetails() {
        mViewModel.getUserProfile(getActivity(), mAccessToken).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    inputFirstName.setText(user.getFirstName());
                    inputLastName.setText(user.getLastName());
                    inputEmail.setText(user.getEmailAddress());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
