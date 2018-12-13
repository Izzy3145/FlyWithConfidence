package uk.airbyte.fwc.fragments.account;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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
import uk.airbyte.fwc.viewmodels.AccountViewModel;
import uk.airbyte.fwc.viewmodels.AuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePwFragment extends Fragment {

    private AccountViewModel mViewModel;
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

    public ChangePwFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
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
        //TODO: pass info to viewmodel
        //TODO: save button update info in Realm

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tells the fragment that its activity has completed its own Activity.onCreate()
        mViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
    }
}
