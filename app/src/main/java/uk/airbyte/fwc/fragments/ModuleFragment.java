package uk.airbyte.fwc.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.utils.Const;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModuleFragment extends Fragment {

    private static final String TAG = ModuleFragment.class.getSimpleName();
    private String selectedModuleID;
    private Realm realm;

    public ModuleFragment() {
        // Required empty public constructor
    }

    //TODO: button to go to next module, from list of modules
    //TODO: if module unlocked, show descriptions, otherwise show unlock button


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedModuleID = getArguments().getString(Const.MODULE_ID);
        Log.d(TAG, "Selected Module ID: " + selectedModuleID);
        realm = Realm.getDefaultInstance();

        Module foundModule = realm.where(Module.class)
                .equalTo("id", selectedModuleID)
                .findFirst();

        Log.d(TAG, "Module Name: " + foundModule.getName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));
        ButterKnife.bind(this, view);
        return view;
    }

}
