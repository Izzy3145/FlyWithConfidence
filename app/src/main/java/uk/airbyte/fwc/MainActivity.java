package uk.airbyte.fwc;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
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
import uk.airbyte.fwc.fragments.RegisterFragment;
import uk.airbyte.fwc.fragments.SignInFragment;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AuthViewModel;


public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInListener, RegisterFragment.OnRegisterListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sharedPref;
    private NavHost navHost;
    private NavController navController;
    private String loggedInStatus;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //TODO: fix this up
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Const.LOGGED_IN_STATUS, Const.LOGGED_OUT);
        editor.apply();

        //userID = sharedPref.getString(Const.USER_ID, "100");
        //Log.d(TAG, "UserID from shared pref: " + userID);

        loggedInStatus = sharedPref.getString(Const.LOGGED_IN_STATUS, Const.LOGGED_OUT);
        if(loggedInStatus.equals(Const.LOGGED_IN)){
            BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
        } else {
            //TODO: toggle for reset password deeplink?
            //TODO: reverse these changes to get startup screen
            //navController.navigate(R.id.splash_fragment);

            BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);

            }
        }


    @Override
    public void onSignIn(String accessToken) {
            //TODO: get accessToken, use it to control flow
        userID = accessToken;
        Log.d(TAG, "UserID from onSignIn listener: " + userID);

    }

    @Override
    public void onRegister(String accessToken) {
        userID = accessToken;
        Log.d(TAG, "UserID from onRegister listener: " + userID);

        //TODO: get accessToken, use it to control flow
    }
}
