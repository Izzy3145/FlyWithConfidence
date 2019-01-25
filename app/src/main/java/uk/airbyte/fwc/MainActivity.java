package uk.airbyte.fwc;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import uk.airbyte.fwc.fragments.HomeFragment;
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

        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
        mModuleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);

        if (savedInstanceState != null) {
            mAccessToken = savedInstanceState.getString(Const.SIS_ACCESS_TOKEN);
            dataRetrieved = savedInstanceState.getBoolean(Const.SIS_DATA_RETRIEVED);
        } else {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
            dataRetrieved = false;
        }


        if (mAccessToken != null && mAccessToken.length() > 0) {
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
            if (!dataRetrieved) {
                mModuleViewModel.knowledgeTopicAndModuleCall(this, mAccessToken);
                mModuleViewModel.preparationTopicAndModuleCall(this, mAccessToken);
                dataRetrieved = true;
            }
        } else {
            navController.navigate(R.id.splash_fragment);
            bottomNavigation.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dataRetrieved = savedInstanceState.getBoolean(Const.SIS_DATA_RETRIEVED);
            mAccessToken = savedInstanceState.getString(Const.SIS_ACCESS_TOKEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        dataRetrieved = sharedPref.getBoolean(Const.DATA_RETRIEVED, false);

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
            navController.navigate(R.id.splash_fragment);
            bottomNavigation.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // sharedPref.edit().putString(Const.ACCESS_TOKEN, mAccessToken).apply();
        //sharedPref.edit().putBoolean(Const.DATA_RETRIEVED, dataRetrieved).apply();
    }


    public void hideNavBarAndLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        bottomNavigation.setVisibility(View.GONE);
    }

    public void hideNavBar() {
        bottomNavigation.setVisibility(View.GONE);
    }

    public void showNavBar() {
        bottomNavigation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (navHost.getNavController().getCurrentDestination().getId() == R.id.homeFragment) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            bottomNavigation.setVisibility(View.VISIBLE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            bottomNavigation.setVisibility(View.VISIBLE);
            Navigation.findNavController(this, R.id.my_nav_host_fragment)
                    .popBackStack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.SIS_DATA_RETRIEVED, dataRetrieved);
        outState.putString(Const.SIS_ACCESS_TOKEN, mAccessToken);
    }
}