package uk.airbyte.fwc.fragments.account;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    @BindView(R.id.leftArrow)
    ImageView backArrow;

    public CoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        ButterKnife.bind(this, view);
        backArrow.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_coursesFragment2_to_accountFragment));
        return view;
    }

}
