package uk.airbyte.fwc;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import io.realm.Realm;
import uk.airbyte.fwc.fragments.RegisterFragment;
import uk.airbyte.fwc.fragments.SignInFragment;
import uk.airbyte.fwc.fragments.account.AccountFragment;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.AuthViewModel;


public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInListener, RegisterFragment.OnRegisterListener,
        AccountFragment.OnLogoutListener {
//TODO: should this extend LifecycleActivity

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private NavHost navHost;
    private NavController navController;
    private String mAccessToken;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        Log.d(TAG, "AccessToken from shared pref: " + mAccessToken);

        if(mAccessToken != null && mAccessToken.length()>0){
            BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
        } else {
            //TODO: toggle for reset password deeplink?
            navController.navigate(R.id.splash_fragment);

            //BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            //bottomNavigation.setVisibility(View.VISIBLE);
            //NavigationUI.setupWithNavController(bottomNavigation, navController);

            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        mAccessToken = sharedPref.getString(Const.ACCESS_TOKEN, "");
        Log.d(TAG, "AccessToken from shared pref: " + mAccessToken);

        if(mAccessToken != null && mAccessToken.length()>0){
            BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            bottomNavigation.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
        } else {
            //TODO: toggle for reset password deeplink?
            navController.navigate(R.id.splash_fragment);

            //BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
            //bottomNavigation.setVisibility(View.VISIBLE);
            //NavigationUI.setupWithNavController(bottomNavigation, navController);

        }
    }

    @Override
    public void onSignIn(String accessToken) {
            //TODO: remove this?
        mAccessToken = accessToken;
        Log.d(TAG, "UserID from onSignIn listener: " + userID);
        editor = sharedPref.edit();
        editor.putString(Const.ACCESS_TOKEN, mAccessToken);
        editor.apply();

    }

    @Override
    public void onRegister(String accessToken) {
        mAccessToken = accessToken;
        Log.d(TAG, "UserID from onRegister listener: " + userID);
        editor = sharedPref.edit();
        editor.putString(Const.ACCESS_TOKEN, mAccessToken);
        editor.apply();
        //TODO: remove this?
    }

    @Override
    public void onLogout(String accessToken) {
        /*mAccessToken = accessToken;
        Log.d(TAG, "UserID from onLogout listener: " + userID);
        editor = sharedPref.edit();
        editor.putString(Const.ACCESS_TOKEN, mAccessToken);
        editor.apply();*/
    }
}
