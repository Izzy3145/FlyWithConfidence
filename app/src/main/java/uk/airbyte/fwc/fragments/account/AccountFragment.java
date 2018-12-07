package uk.airbyte.fwc.fragments.account;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;

public class AccountFragment extends Fragment {

    @BindView(R.id.updateDetailsTv)
    TextView updateDetailsTv;
    @BindView(R.id.changePasswordTv)
    TextView changePwTv;
    @BindView(R.id.findOutMoreTv)
    TextView findOutMoreTv;
    @BindView(R.id.upcomingCoursesTv)
    TextView upcomingCoursesTv;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        ButterKnife.bind(this, view);
        updateDetailsTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_accountFragment_to_updateDetailsFragment));
        changePwTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_accountFragment_to_changePwFragment));
        //TODO: add link to website
        //findOutMoreTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_accountFragment_to_));
        upcomingCoursesTv.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_accountFragment_to_coursesFragment2));
        return view;
    }

    //TODO: add SnackBar with message confirming profile/password? has been udpated
}
