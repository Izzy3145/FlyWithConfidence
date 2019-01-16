package uk.airbyte.fwc.fragments.account;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AccountViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateDetailsFragment extends Fragment {

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
    private AccountViewModel mAccountViewModel;
    private String mAccessToken;
    private SharedPreferences sharedPref;

    public UpdateDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        mAccountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        mAccountViewModel.putUserProfile(getActivity(), mAccessToken, firstName, lastName, email);

        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).
                navigate(R.id.action_updateDetailsFragment_to_accountFragment);
    }

    public void getUserDetails() {
        mAccountViewModel.getUserProfile(getActivity(), mAccessToken).observe(this, new Observer<User>() {
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
        mAccountViewModel.closeRealm();
    }
}
