package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.api.APIClient;
import uk.airbyte.fwc.api.APIError;
import uk.airbyte.fwc.api.APIService;
import uk.airbyte.fwc.api.ErrorUtils;
import uk.airbyte.fwc.model.Login;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.model.User;
import uk.airbyte.fwc.repositories.AccountRepository;
import uk.airbyte.fwc.repositories.AuthRepository;
import uk.airbyte.fwc.utils.Const;


public class AuthViewModel extends ViewModel {

    private static final String TAG = AuthViewModel.class.getSimpleName();

    private MutableLiveData<User> user;
    private MutableLiveData<Reminder> reminderSent;
    private APIService apiService = APIClient.getClient().create(APIService.class);
    //private onErrorListener mListener;
    private final AuthRepository authRepository;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    //we will call this method to get the data
    public LiveData<User> getUserFromLogin(Context context, String password, String email) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            Log.d(TAG, "In method getUserFromLogin()! ");
            loginCall(context, password, email);
        }

        return user;
    }

    //we will call this method to get the data
    public LiveData<User> getUserFromRegister(Context context, String password, String email, String lName, String fName) {
        if (user == null) {
            user = new MutableLiveData<User>();
            //we will load it asynchronously from server in this method
            registerCall(context, password, email, lName, fName);
        }
        return user;
    }

    public LiveData<Reminder> getForgottenPw(Context context, String email) {

            reminderSent = new MutableLiveData<Reminder>();
            //we will load it asynchronously from server in this method
            forgotCall(context, email);

            return reminderSent;
    }

    private void loginCall(final Context context, String password, String email) {
        apiService.login(new Login(password, email))
                .enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    Log.d(TAG, "Response loginCall() success: " + response.body());
                    user.postValue(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String errorCode = String.valueOf(error.status());
                    String errorMessage = error.message();
                    Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("loginCall() error message", error.message());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Response loginCall() failure");
                user.postValue(null);
            }
        });
    }

    public void registerCall(final Context context, String password, String email, String lName, String fName) {
        apiService.registerUser(new Login(password, email, lName, fName))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            authRepository.registerUserRealm(response.body());
                            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                            editor = sharedPref.edit();
                            editor.putString(Const.USER_ID, response.body().getId());
                            editor.putString(Const.ACCESS_TOKEN, response.body().getAccessToken());
                            editor.apply();
                            Log.d(TAG, "Response registerCall() success: " + response.body());
                            //user.postValue(response.body());
                            Intent openMain = new Intent(context, MainActivity.class);
                            context.startActivity(openMain);

                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            String errorCode = String.valueOf(error.status());
                            String errorMessage = error.message();
                            Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.d("registerCall() error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "Response registerCall() failure");
                       // user.postValue(null);
                    }
                });
    }

    private void forgotCall(final Context context, String email) {
        apiService.forgotPassword(new Login(email)).enqueue(new Callback<Reminder>() {
            @Override
            public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response forgotCall() success: " + response.body());
                    reminderSent.postValue(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String errorCode = String.valueOf(error.status());
                    String errorMessage = error.message();
                    Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("forgotCall() error message", error.message());
                }
            }

            @Override
            public void onFailure(Call<Reminder> call, Throwable t) {
                Log.d(TAG, "Response forgotCall() failure");
                reminderSent.postValue(null);
            }
        });
    }
}
