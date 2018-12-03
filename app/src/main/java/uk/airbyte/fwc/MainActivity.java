package uk.airbyte.fwc;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import uk.airbyte.fwc.utils.Const;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private NavHost navHost;
    private String loggedInStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        loggedInStatus = sharedPref.getString(Const.LOGGED_IN_STATUS, Const.LOGGED_OUT);
        if(loggedInStatus.equals(Const.LOGGED_IN)){

        } else {

        }*/



        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        NavController navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        navController.navigate(R.id.splash_fragment);
    }
}
