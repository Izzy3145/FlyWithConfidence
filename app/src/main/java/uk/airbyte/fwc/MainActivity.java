package uk.airbyte.fwc;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import uk.airbyte.fwc.fragments.ModuleFragment;
import uk.airbyte.fwc.model.ID;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.ModuleViewModel;
import uk.airbyte.fwc.viewmodels.PurchaseViewModel;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


public class MainActivity extends AppCompatActivity implements  ModuleFragment.OnPurchaseListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String PRODUCT_ID = "com.anjlab.test.iab.s2.p5";
    private static final String MERCHANT_ID=null;
    private SharedPreferences sharedPref;
    private NavHost navHost;
    private NavController navController;
    private String mAccessToken;
    private BottomNavigationView bottomNavigation;
    private ModuleViewModel mModuleViewModel;
    private Boolean dataRetrieved;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    private PurchaseViewModel mPurchaseViewModel;
    private String purchasedTopicID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCenter.start(getApplication(), "91731501-accf-493e-9ab7-ddf86fcd759e",
                Analytics.class, Crashes.class);

        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.btm_navigation);
        mModuleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);
        mPurchaseViewModel = ViewModelProviders.of(this).get(PurchaseViewModel.class);
        mPurchaseViewModel = new PurchaseViewModel();
        mPurchaseViewModel.postReceiptLive().observe(this, new Observer<ID>() {
            @Override
            public void onChanged(@Nullable ID id) {
                if(id != null){
                    navController.navigate(R.id.topicsFragment);
                }
            }
        });

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

        //TODO: enter proper license key from constants
        bp = new BillingProcessor(this, null, MERCHANT_ID,  new BillingProcessor.IBillingHandler(){

            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //TODO: change value for receipt from "test"
                mPurchaseViewModel.postReceipt(MainActivity.this, purchasedTopicID, "test");
                Toast.makeText(MainActivity.this, "New topic purchased!", Toast.LENGTH_LONG).show(); }

            @Override
            public void onPurchaseHistoryRestored() {

            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                Log.d(TAG, "OnBillingError: " + errorCode);
                if(error != null) {
                    Log.d(TAG, "OnBillingError: " + error.toString());
                }
                Toast.makeText(MainActivity.this, "Purchasing error, please try again.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;
                Log.d(TAG, "onBillingInitialized()");
            }
        });
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Navigation.findNavController(this, R.id.my_nav_host_fragment)
                .popBackStack();
        bottomNavigation.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.SIS_DATA_RETRIEVED, dataRetrieved);
        outState.putString(Const.SIS_ACCESS_TOKEN, mAccessToken);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onTopicPurchased(String topicID) {
        purchasedTopicID = topicID;
        if(!readyToPurchase){
            Toast.makeText(this, "Billing not initialised", Toast.LENGTH_LONG).show();
        } else {
            //TODO: get productID from topicID somehow?
            bp.purchase(this, PRODUCT_ID);
        }
    }

}