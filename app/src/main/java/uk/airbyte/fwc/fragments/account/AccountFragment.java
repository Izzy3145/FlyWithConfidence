package uk.airbyte.fwc.fragments.account;


import android.accounts.Account;
import android.content.Context;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.fragments.RegisterFragment;
import uk.airbyte.fwc.utils.Const;

public class AccountFragment extends Fragment {

    @BindView(R.id.updateDetailsTv)
    TextView updateDetailsTv;
    @BindView(R.id.changePasswordTv)
    TextView changePwTv;
    @BindView(R.id.findOutMoreTv)
    TextView findOutMoreTv;
    @BindView(R.id.logoutTv)
    TextView logoutTv;
    private OnLogoutListener mListener;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
        //TODO: add link to website
        findOutMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coursesWebsiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.COURSES_WEBSITE));
                startActivity(coursesWebsiteIntent);
            }
        });
        return view;
    }

    @OnClick(R.id.logoutTv)
    public void logout() {
        //mListener.onLogout("");
        //Toast.makeText(getActivity(), "Log Out Clicked", Toast.LENGTH_SHORT).show();
        editor = sharedPref.edit();
        editor.putString(Const.ACCESS_TOKEN, "");
        editor.putString(Const.USER_ID, "");
        editor.apply();

        Intent openMain = new Intent(getActivity(), MainActivity.class);
        startActivity(openMain);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountFragment.OnLogoutListener) {
            mListener = (AccountFragment.OnLogoutListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLogoutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLogoutListener {
        void onLogout(String accessToken);
    }

    //TODO: add SnackBar with message confirming profile/password? has been udpated
}
