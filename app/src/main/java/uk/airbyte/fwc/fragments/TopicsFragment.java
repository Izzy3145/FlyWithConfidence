package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.realm.Realm;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.TopicsViewModel;

public class TopicsFragment extends Fragment {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    private TopicsViewModel mViewModel;
    private String accessToken;
    private SharedPreferences sharedPref;
    private Realm realm;

    public static TopicsFragment newInstance() {
        return new TopicsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        accessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        realm = Realm.getDefaultInstance();

        /*mViewModel.getModulesForTopic(getActivity(), accessToken, "74e89e6e-fb05-4a59-ac5d-eb2e937bad16")
                .observe(this, new Observer<List<Module>>() {
            @Override
            public void onChanged(@Nullable final List<Module> modules) {
                if(modules != null){
                    realm.executeTransactionAsync(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm) {
                            for(int i = 0; i<modules.size(); i++){
                                Module module = modules.get(i);
                                realm.copyToRealm(module, null);
                            }
                        }
                    });
                }
            }
        });*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.topics_fragment, container, false);
        TabLayout tablayout = (TabLayout) view.findViewById(R.id.top_tabs);
        tablayout.setVisibility(View.VISIBLE);
        //TODO: set up onClickListener

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TopicsViewModel.class);
        // TODO: Use the ViewModel
    }

}
