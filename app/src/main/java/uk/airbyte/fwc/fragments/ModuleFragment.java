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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.addToFavouritesBtn)
    Button addFavouriteBtn;
    @BindView(R.id.nextModuleBtn)
    Button nextModuleBtn;
    private HomeViewModel mHomeViewModel;
    private Module mModule;
    private String introduction;
    private String notes;
    private ArrayList<Module> modulesInTopic = new ArrayList<>(0);
    private String topicID;

    public ModuleFragment() {
        // Required empty public constructor
    }

    //TODO: if module unlocked, show descriptions, otherwise show unlock button


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        realm = Realm.getDefaultInstance();
        getListOfModules();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));
        ButterKnife.bind(this, view);

        displayModuleInfo(mModule);

        addFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mModule.setFavourited(true);
                        realm.copyToRealmOrUpdate(mModule);
                    }
                });
            }
        });

        nextModuleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int displayNumber = Integer.parseInt(mModule.getDisplayOrder());
                Log.d(TAG, "Next nextToDisplay: " + String.valueOf(displayNumber));

                if(displayNumber < modulesInTopic.size()) {
                    mModule = modulesInTopic.get((displayNumber));
                    Log.d(TAG, "Next module name: " + mModule.getName());
                    displayModuleInfo(mModule);
                } else {
                    Toast.makeText(getActivity(), "No more modules in this topic!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    public void getListOfModules(){
        selectedModuleID = getArguments().getString(Const.MODULE_ID);
        mModule = realm.where(Module.class)
                .equalTo("id", selectedModuleID)
                .findFirst();
        topicID = mModule.getTopic().getId();

        RealmResults<Module> topicModulesRealm = realm.where(Module.class)
                .equalTo("topic.id", topicID)
                .findAll();
        modulesInTopic.clear();
        modulesInTopic.addAll(realm.copyFromRealm(topicModulesRealm));
        Log.d(TAG, "Module List size: " + modulesInTopic.size());

    }

    public void displayModuleInfo(Module module){
        if(module!=null){
            moduleIntroTv.setText(module.getDescription());
            moduleNotesTv.setText(module.getNotes());
            if(module.getMedia().getVideo720()!=null){
                mHomeViewModel.select(new ShowPlay(null, null, module.getMedia().getVideo720()));
            }

            //TODO: make this work - stackOverflow post
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < module.getBullets().size(); i++){
                String stringB = "\r\n" + module.getBullets().get(i);
                SpannableString string = new SpannableString(stringB);
                string.setSpan(new BulletSpan(), 0, stringB.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.append(stringB);
            }

            thingsToTv.setText(sb, TextView.BufferType.SPANNABLE);

        }
    }
    @Override
    public void onPause() {
        super.onPause();
       // mHomeViewModel.select(new ShowPlay(null, null, null));
    }

    @Override
    public void onResume() {
        super.onResume();
        getListOfModules();
    }
}
