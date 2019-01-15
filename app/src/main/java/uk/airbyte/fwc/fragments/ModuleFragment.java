package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.adapters.ModulesAdapter;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.ModuleViewModel;
import uk.airbyte.fwc.viewmodels.VideoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModuleFragment extends Fragment {

    private static final String TAG = ModuleFragment.class.getSimpleName();

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
    private String selectedModuleID;
    private VideoViewModel mVideoViewModel;
    private ModuleViewModel mModuleViewModel;
    private Module mModule;
    private ArrayList<Module> modulesInTopic = new ArrayList<>(0);
    private String topicID;
    private SpannableString spanString;
    private Boolean isFavourite;

    public ModuleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mModuleViewModel = ViewModelProviders.of(getActivity()).get(ModuleViewModel.class);
        if(savedInstanceState!=null){
            selectedModuleID = savedInstanceState.getString(Const.MODULE_ID);
        } else {
            selectedModuleID = getArguments().getString(Const.MODULE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));
        ButterKnife.bind(this, view);

        getListOfModules();
        displayModuleInfo(mModule);

        favouriteButtonToggle();

        addFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite != null) {
                    isFavourite = !isFavourite;
                    Toast.makeText(getActivity(), "Module favourited: " + isFavourite, Toast.LENGTH_SHORT).show();
                    favouriteButtonToggle();
                } else {
                    isFavourite = true;
                    Toast.makeText(getActivity(), "Module favourited: " + isFavourite, Toast.LENGTH_SHORT).show();
                    favouriteButtonToggle();
                }

                mModuleViewModel.setFavourite(isFavourite, mModule);
            }
        });

        nextModuleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int displayNumber = Integer.parseInt(mModule.getDisplayOrder());
                if (displayNumber < modulesInTopic.size()) {
                    mModule = modulesInTopic.get((displayNumber));
                    mVideoViewModel.clearVideo();
                    displayModuleInfo(mModule);
                    favouriteButtonToggle();
                } else {
                    Toast.makeText(getActivity(), "No more modules in this topic!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    public void getListOfModules() {
        mModule = mModuleViewModel.getModuleFromId(selectedModuleID);
        Log.d(TAG, "getListOfModules() selectedModuleID: " + selectedModuleID);
        topicID = mModule.getTopic().getId();
        modulesInTopic = new ArrayList<>(mModuleViewModel.getModulesForTopic(topicID));
        Log.d(TAG, "getListOfModules() modulesInTopic.size(): " + modulesInTopic.size());
    }


    public void displayModuleInfo(Module module) {
        if (module != null) {
            moduleIntroTv.setText(module.getDescription());
            moduleNotesTv.setText(module.getNotes());
            isFavourite = mModule.getFavourited();
            if (module.getMedia().getVideo1080() != null) {
                Log.d(TAG, "displayModuleInfo() media;" + module.getMedia().getVideo1080());
                mVideoViewModel.select(new ShowPlay(module.getId(), null, null,
                        module.getMedia().getVideo1080(), module.getCurrentWindow(), module.getPlayerPosition()));
            }

            String sep = System.lineSeparator();
            StringBuilder sb = new StringBuilder();
            //TODO: check this with full API response
            /*ArrayList<String> bulletsList = new ArrayList<>(0);
            for(int i = 0; i < module.getBullets().size(); i++){
                String bulletNote =  module.getBullets().get(i);
                bulletsList.add(bulletNote);
            }*/
            ArrayList<String> bulletsList = new ArrayList<>();
            bulletsList.add("Test 1");
            bulletsList.add("Test 2");
            bulletsList.add("Test 3");
            bulletsList.add("Test 4");
            for (String s : bulletsList) {
                sb.append(sep + s);
            }
            String concat = sb.toString();
            spanString = new SpannableString(concat);
            for (String s : bulletsList) {
                addBullet(s, concat);
            }
            thingsToTv.setText(spanString);
        }
    }

    public void favouriteButtonToggle() {
        if (isFavourite != null) {
            if (!isFavourite) {
                addFavouriteBtn.setText(getString(R.string.add_to_favourites));
            } else {
                addFavouriteBtn.setText(getString(R.string.remove_favourite));
            }
        } else {
            addFavouriteBtn.setText(R.string.add_to_favourites);
        }
    }

    private void addBullet(String s, String txt) {
        int index = txt.indexOf(s);
        BulletSpan bullet = new BulletSpan(20, Color.BLACK);
        spanString.setSpan(bullet, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        getListOfModules();
        displayModuleInfo(mModule);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Const.MODULE_ID, selectedModuleID);
        Log.d(TAG, "moduleID to outState: " + selectedModuleID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mVideoViewModel.closeRealm();
        //mModuleViewModel.closeRealm();
    }
}
