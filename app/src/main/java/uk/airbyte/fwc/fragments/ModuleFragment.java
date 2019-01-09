package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.VideoViewModel;

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
    private VideoViewModel mVideoViewModel;
    private Module mModule;
    private String introduction;
    private String notes;
    private ArrayList<Module> modulesInTopic = new ArrayList<>(0);
    private String topicID;
    private SpannableString spanString;
    private int currentWindow;
    private long playbackPosition;
    private Boolean isFavourite;

    public ModuleFragment() {
        // Required empty public constructor
    }

    //TODO: if module unlocked, show descriptions, otherwise show unlock button


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        realm = Realm.getDefaultInstance();
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
                    Log.d(TAG, "Module name: " + mModule + ". Favourite status: " + mModule.getFavourited().toString());
                    Toast.makeText(getActivity(), "Module favourited: " + isFavourite, Toast.LENGTH_SHORT).show();
                    //addFavouriteBtn.setText("REMOVE FROM FAVOURITES");
                    //addFavouriteBtn.setText("ADD TO FAVOURITES");
                    favouriteButtonToggle();
                } else {
                    isFavourite = true;
                    Toast.makeText(getActivity(), "Module favourited: " + isFavourite, Toast.LENGTH_SHORT).show();
                    favouriteButtonToggle();
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mModule.setFavourited(isFavourite);
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

                if (displayNumber < modulesInTopic.size()) {
                    mModule = modulesInTopic.get((displayNumber));
                    Log.d(TAG, "Next module name: " + mModule.getName());
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
        selectedModuleID = getArguments().getString(Const.MODULE_ID);
        Log.d(TAG, "Module ID: " + selectedModuleID);
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


    public void displayModuleInfo(Module module) {
        if (module != null) {
            moduleIntroTv.setText(module.getDescription());
            moduleNotesTv.setText(module.getNotes());
            isFavourite = mModule.getFavourited();
            if (module.getMedia().getVideo720() != null) {
                Log.d(TAG, "Selected module player position: " + module.getPlayerPosition());
                mVideoViewModel.select(new ShowPlay(module.getId(), null, null,
                        module.getMedia().getVideo720(), module.getCurrentWindow(), module.getPlayerPosition()));
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
        if(isFavourite != null){
            if(!isFavourite) {
                addFavouriteBtn.setText("ADD TO FAVOURITES");
            } else {
                addFavouriteBtn.setText("REMOVE FROM FAVOURITES");
            }
        } else {
            addFavouriteBtn.setText("ADD TO FAVOURITES");
        }
    }


    private void addBullet(String s, String txt) {
        int index = txt.indexOf(s);
        // You can change the attributes as you need ... I just added a bit of color and formating
        BulletSpan bullet = new BulletSpan(20, Color.BLACK);
        spanString.setSpan(bullet, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onPause() {
        super.onPause();

        // mVideoViewModel.select(new ShowPlay(null, null, null));
    }

    @Override
    public void onResume() {
        super.onResume();
        //getListOfModules();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
