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
import uk.airbyte.fwc.repositories.UserRepository;
import uk.airbyte.fwc.utils.Const;


public class AuthViewModel extends ViewModel {

    private static final String TAG = AuthViewModel.class.getSimpleName();

    private MutableLiveData<Reminder> reminderSent;
    private APIService apiService = APIClient.getClient().create(APIService.class);
    private final UserRepository userRepository;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public AuthViewModel() {
        userRepository = new UserRepository();
    }

    public void closeRealm(){
        userRepository.onDestroy();
    }

    public LiveData<Reminder> getForgottenPw(Context context, String email) {
            reminderSent = new MutableLiveData<Reminder>();
            //we will load it asynchronously from server in this method
            forgotCall(context, email);
            return reminderSent;
    }

    public void loginCall(final Context context, String password, String email) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        apiService.login(new Login(password, email))
                .enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userRepository.saveUserRealm(response.body());
                    editor.putString(Const.ACCESS_TOKEN, response.body().getAccessToken());
                    editor.putString(Const.USER_ID, response.body().getId());
                    editor.apply();

                    Intent openMain = new Intent(context, MainActivity.class);
                    context.startActivity(openMain);
                    Log.d(TAG, "Response loginCall() success: " + response.body());
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
                Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                editor.putString(Const.ACCESS_TOKEN, "");
                editor.putString(Const.USER_ID, "");
                editor.apply();
            }
        });
    }

    public void registerCall(final Context context, String password, String email, String lName, String fName) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        apiService.registerUser(new Login(password, email, lName, fName))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            userRepository.saveUserRealm(response.body());

                            editor.putString(Const.USER_ID, response.body().getId());
                            editor.putString(Const.ACCESS_TOKEN, response.body().getAccessToken());
                            editor.apply();
                            Log.d(TAG, "Response registerCall() success: " + response.body());

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
                        Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();

                        editor.putString(Const.USER_ID, "");
                        editor.putString(Const.ACCESS_TOKEN, "");
                        editor.apply();
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
                Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
                reminderSent.postValue(null);
            }
        });
    }
}
