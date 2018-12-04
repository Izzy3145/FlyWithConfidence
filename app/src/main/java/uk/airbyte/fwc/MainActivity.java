package uk.airbyte.fwc;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import uk.airbyte.fwc.fragments.RegisterFragment;
import uk.airbyte.fwc.fragments.SignInFragment;
import uk.airbyte.fwc.utils.Const;


public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInListener, RegisterFragment.OnRegisterListener {

    private SharedPreferences sharedPref;
    private NavHost navHost;
    private NavController navController;
    private String loggedInStatus;

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

        loggedInStatus = sharedPref.getString(Const.LOGGED_IN_STATUS, Const.LOGGED_OUT);
        if(loggedInStatus.equals(Const.LOGGED_IN)){
            BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
        } else {
            //TODO: toggle for reset password deeplink?
            navController.navigate(R.id.splash_fragment);
            }
        }


    @Override
    public void onSignIn(String accessToken) {
            //TODO: get accessToken, use it to control flow
    }

    @Override
    public void onRegister(String accessToken) {
        //TODO: get accessToken, use it to control flow
    }
}
