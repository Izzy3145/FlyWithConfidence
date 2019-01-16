package uk.airbyte.fwc;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.ModuleViewModel;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sharedPref;
    private NavHost navHost;
    private NavController navController;
    private String mAccessToken;
    private BottomNavigationView bottomNavigation;
    private ModuleViewModel mModuleViewModel;
    private Boolean dataRetrieved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate called");

        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);

        mModuleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        Log.d(TAG, "AccessToken from shared pref: " + mAccessToken);

        if (savedInstanceState != null) {
            dataRetrieved = savedInstanceState.getBoolean(Const.DATA_RETRIEVED);
        }

        if (mAccessToken != null && mAccessToken.length() > 0) {
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
            if (dataRetrieved = false) {
                mModuleViewModel.knowledgeTopicAndModuleCall(this, mAccessToken);
                mModuleViewModel.preparationTopicAndModuleCall(this, mAccessToken);
                dataRetrieved = true;
            }
            //TODO: add progressBar
        } else {
            dataRetrieved = false;
            navController.navigate(R.id.splash_fragment);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dataRetrieved = savedInstanceState.getBoolean(Const.DATA_RETRIEVED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        Log.d(TAG, "AccessToken from shared pref: " + mAccessToken);

        if (mAccessToken != null && mAccessToken.length() > 0) {
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
            if (!dataRetrieved) {
                mModuleViewModel.knowledgeTopicAndModuleCall(this, mAccessToken);
                mModuleViewModel.preparationTopicAndModuleCall(this, mAccessToken);
                dataRetrieved = true;
                //TODO: add progressBar
            }
        } else {
            dataRetrieved = false;
            navController.navigate(R.id.splash_fragment);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }


    public void hideNavBar() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        bottomNavigation.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bottomNavigation.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataRetrieved != null) {
            outState.putBoolean(Const.DATA_RETRIEVED, dataRetrieved);
        }
    }
}