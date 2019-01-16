package uk.airbyte.fwc.fragments.account;


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
import android.widget.Toast;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AccountViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePwFragment extends Fragment {

    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.inputCurrentPw)
    TextInputEditText inputCurrentPw;
    @BindView(R.id.inputNewPw)
    TextInputEditText inputNewPw;
    @BindView(R.id.inputConfPw)
    TextInputEditText inputConfPw;
    private AccountViewModel mAccountViewModel;
    private SharedPreferences sharedPref;
    private String accessToken;
    private String currentPassword;
    private String confPassword;
    private String newPassword;

    public ChangePwFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        accessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_pw, container, false);
        ButterKnife.bind(this, view);
        cancelBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_changePwFragment_to_accountFragment));
        return view;
    }

    @OnClick(R.id.saveBtn)
    public void savePassword(){

        if(inputCurrentPw.getText().toString().length() == 0){
            Toast.makeText(getActivity(), "Please enter your current password!", Toast.LENGTH_SHORT).show();
        } else if(inputConfPw.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please confirm your new password!", Toast.LENGTH_SHORT).show();
        } else if (inputNewPw.getText().toString().length() == 0){
            Toast.makeText(getActivity(), "Please enter your new password!", Toast.LENGTH_SHORT).show();
        } else if (!inputNewPw.getText().toString().equals(inputConfPw.getText().toString())) {
            Toast.makeText(getActivity(), "Passwords must match!", Toast.LENGTH_SHORT).show();
        } else {
            currentPassword = inputCurrentPw.getText().toString();
            confPassword = inputConfPw.getText().toString();
            newPassword = inputNewPw.getText().toString();
            mAccountViewModel.putNewPassword(getActivity(), accessToken, currentPassword, newPassword);
            Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.action_changePwFragment_to_accountFragment);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tells the fragment that its activity has completed its own Activity.onCreate()
        mAccountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccountViewModel.closeRealm();
    }
}
