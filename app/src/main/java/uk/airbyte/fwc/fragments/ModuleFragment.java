package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.HomeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModuleFragment extends Fragment {

    private static final String TAG = ModuleFragment.class.getSimpleName();
    private String selectedModuleID;
    private Realm realm;
    @BindView(R.id.moduleIntroTv)
    TextView moduleIntroTv;
    @BindView(R.id.moduleNotesTv)
    TextView moduleNotesTv;
    @BindView(R.id.thingsToTv)
    TextView thingsToTv;
    private HomeViewModel mHomeViewModel;
    private Module mModule;
    private String introduction;
    private String notes;

    public ModuleFragment() {
        // Required empty public constructor
    }

    //TODO: button to go to next module, from list of modules
    //TODO: if module unlocked, show descriptions, otherwise show unlock button


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

        selectedModuleID = getArguments().getString(Const.MODULE_ID);
        Log.d(TAG, "Selected Module ID: " + selectedModuleID);
        realm = Realm.getDefaultInstance();

        mModule = realm.where(Module.class)
                .equalTo("id", selectedModuleID)
                .findFirst();

        Log.d(TAG, "Module Name: " + mModule.getName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));
        ButterKnife.bind(this, view);
        if(mModule!=null){
            moduleIntroTv.setText(mModule.getDescription());
            moduleNotesTv.setText(mModule.getNotes());
            if(mModule.getMedia().getVideo720()!=null){
                mHomeViewModel.select(new ShowPlay(null, null, mModule.getMedia().getVideo720()));
            }

            //TODO: make this work - stackOverflow post
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < mModule.getBullets().size(); i++){
                String stringB = "\r\n" + mModule.getBullets().get(i);
                SpannableString string = new SpannableString(stringB);
                string.setSpan(new BulletSpan(), 0, stringB.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.append(stringB);
            }

            thingsToTv.setText(sb, TextView.BufferType.SPANNABLE);

        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
       // mHomeViewModel.select(new ShowPlay(null, null, null));
    }
}
