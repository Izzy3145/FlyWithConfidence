package uk.airbyte.fwc.fragments.account;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AccountViewModel;

public class AccountFragment extends Fragment {

    @BindView(R.id.currentUserTv)
    TextView currentUserTv;
    @BindView(R.id.updateDetailsTv)
    TextView updateDetailsTv;
    @BindView(R.id.changePasswordTv)
    TextView changePwTv;
    @BindView(R.id.findOutMoreTv)
    TextView findOutMoreTv;
    @BindView(R.id.logoutTv)
    TextView logoutTv;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private AccountViewModel mAccountViewModel;
    private String accessToken;
    private String fullName;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        accessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");

        mAccountViewModel = new AccountViewModel();
        mAccountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));
        ButterKnife.bind(this, view);
        updateDetailsTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_accountFragment_to_updateDetailsFragment));
        changePwTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_accountFragment_to_changePwFragment));
        findOutMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coursesWebsiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.COURSES_WEBSITE));
                startActivity(coursesWebsiteIntent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccountViewModel.getUserProfile(getActivity(), accessToken).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user != null){
                    fullName = user.getFirstName() + " " + user.getLastName();
                    currentUserTv.setText(fullName);
                }
            }
        });
    }

    @OnClick(R.id.logoutTv)
    public void logout() {
        editor = sharedPref.edit();
        editor.putString(Const.ACCESS_TOKEN, "");
        editor.putString(Const.USER_ID, "");
        editor.apply();

        mAccountViewModel.deleteRealmContents();

        Intent openMain = new Intent(getActivity(), MainActivity.class);
        startActivity(openMain);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccountViewModel.closeRealm();
    }
}
