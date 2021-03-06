package uk.airbyte.fwc.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
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
    @BindView(R.id.removeFavouritesBtn)
    Button removeFavouriteBtn;
    @BindView(R.id.nextModuleBtn)
    Button nextModuleBtn;
    @BindView(R.id.unlockTopicBtn)
    Button unlockTopicBtn;
    @BindView(R.id.locked_topic_title)
    TextView lockedTopicTitle;
    @BindView(R.id.locked_module_title)
    TextView lockedModuleTitle;
    @BindView(R.id.locked_module_desc)
    TextView lockedModuleDesc;
    @BindView(R.id.lockedCloseBtn)
    Button lockedCloseBtn;
    @BindView(R.id.unlockedModuleGroup)
    Group unlockedModuleGroup;
    @BindView(R.id.lockedModuleGroup)
    Group lockedModuleGroup;

    private String selectedModuleID;
    private VideoViewModel mVideoViewModel;
    private ModuleViewModel mModuleViewModel;
    private Module mModule;
    private Module nextModule;
    private ArrayList<Module> modulesInTopic = new ArrayList<>(0);
    private String topicID;
    private SpannableString spanString;
    private Boolean isFavourite;
    private Boolean canView;
    private OnPurchaseListener mListener;

    public ModuleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).hideNavBar();

        getModulesFromRealm();
        canView = mModule.getCanView();

        if(canView) {
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_grey));
            unlockedModuleGroup.setVisibility(View.VISIBLE);
            lockedModuleGroup.setVisibility(View.GONE);
            favouriteButtonToggle();

            addFavouriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavourite != null) {
                        isFavourite = !isFavourite;
                        favouriteButtonToggle();
                    } else {
                        isFavourite = true;
                        favouriteButtonToggle();
                    }
                    mModuleViewModel.setFavourite(isFavourite, mModule);
                }
            });

            removeFavouriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavourite != null) {
                        isFavourite = !isFavourite;
                        favouriteButtonToggle();
                    } else {
                        isFavourite = true;
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
                        mVideoViewModel.clearVideo();
                        nextModule = modulesInTopic.get(displayNumber);
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.MODULE_ID, nextModule.getId());
                        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.moduleFragment, bundle);
                    } else {
                        Toast.makeText(getActivity(), "No more modules in this topic!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            unlockedModuleGroup.setVisibility(View.GONE);
            lockedModuleGroup.setVisibility(View.VISIBLE);
            if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
                removeFavouriteBtn.setVisibility(View.GONE);
            }

                unlockTopicBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //TODO: will be some other topic attribute
                        mListener.onTopicPurchased(topicID);
                    }
                });
                lockedCloseBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                                .popBackStack();
                    }
                });
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mVideoViewModel.getFav().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean favOnOff) {
                if(favOnOff != null){
                    isFavourite = favOnOff;
                    favouriteButtonToggle();
                }
            }
        });
        displayModuleInfo(mModule);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPurchaseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnPurchaseListener");
        }
    }

    public void getModulesFromRealm() {
        mModule = mModuleViewModel.getModuleFromId(selectedModuleID);
        topicID = mModule.getTopic().getId();
        modulesInTopic = new ArrayList<>(mModuleViewModel.getModulesForTopic(topicID));
    }


    public void displayModuleInfo(Module module) {
        if(canView) {
            if (module != null) {
                String description = module.getDescription();
                String notes = module.getNotes();
                Boolean isFavourite = module.getFavourited();
                String video = module.getMedia().getVideo1080();

                moduleIntroTv.setText(module.getDescription());
                moduleNotesTv.setText(module.getNotes());
                isFavourite = module.getFavourited();
                mVideoViewModel.select(new ShowPlay(module.getId(), null, null,
                        module.getMedia().getVideo1080(), module.getCurrentWindow(), module.getPlayerPosition(), false));

                String sep = System.lineSeparator();
                StringBuilder sb = new StringBuilder();
                //TODO: check this with full API response
            /*ArrayList<String> bulletsList = new ArrayList<>(0);
            for(int i = 0; i < module.getBullets().size(); i++){
                String bulletNote =  module.getBullets().get(i);
                bulletsList.add(bulletNote);
            }*/
                ArrayList<String> bulletsList = new ArrayList<>(0);
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
        } else {
            lockedTopicTitle.setText(module.getTopic().getName());
            lockedModuleTitle.setText(module.getName());
            lockedModuleDesc.setText(module.getDescription());
        }
    }

    public void favouriteButtonToggle() {
        if (isFavourite != null) {
            if (!isFavourite) {
                addFavouriteBtn.setVisibility(View.VISIBLE);
                removeFavouriteBtn.setVisibility(View.GONE);
            } else {
                addFavouriteBtn.setVisibility(View.GONE);
                removeFavouriteBtn.setVisibility(View.VISIBLE);
            }
        } else {
            addFavouriteBtn.setVisibility(View.VISIBLE);
            removeFavouriteBtn.setVisibility(View.GONE);
        }
    }

    private void addBullet(String s, String txt) {
        int index = txt.indexOf(s);
        BulletSpan bullet = new BulletSpan(20, Color.BLACK);
        spanString.setSpan(bullet, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).hideNavBar();
        getModulesFromRealm();
        displayModuleInfo(mModule);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Const.MODULE_ID, selectedModuleID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoViewModel.closeRealm();
        mModuleViewModel.closeRealm();
    }

    public interface OnPurchaseListener{
        void onTopicPurchased(String topicID);
    }
}
