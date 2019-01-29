package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
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
import uk.airbyte.fwc.model.ID;
import uk.airbyte.fwc.model.Receipt;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.repositories.ModuleRepository;
import uk.airbyte.fwc.repositories.UserRepository;
import uk.airbyte.fwc.utils.Const;

public class PurchaseViewModel extends ViewModel {

    private static final String TAG = PurchaseViewModel.class.getSimpleName();

    private APIService apiService = APIClient.getClient().create(APIService.class);

    private final ModuleRepository moduleRepository;
    private String mTopicID;
    private MutableLiveData<ID> purchaseIDfromAPI = new MutableLiveData<ID>();


    public PurchaseViewModel() {
        moduleRepository = new ModuleRepository();
    }

    public LiveData<ID> postReceiptLive(){
        return purchaseIDfromAPI;
    }

    public void postReceipt(final Context context, String topicID, String receipt){
        mTopicID = topicID;
        apiService.purchaseTopic(new Receipt(mTopicID, receipt)).enqueue(new Callback<ID>() {
            @Override
            public void onResponse(Call<ID> call, Response<ID> response) {
                if (response.isSuccessful()) {
                    purchaseIDfromAPI.postValue(response.body());
                    moduleRepository.moduleCanViewTrue(mTopicID);
                    Log.d(TAG, "Response postReceipt() success: " + response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    String errorCode = String.valueOf(error.status());
                    String errorMessage = error.message();
                    Toast.makeText(context, "Error: " + errorCode + " " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("postReceipt() error message", error.message());
                }
            }

            @Override
            public void onFailure(Call<ID> call, Throwable t) {
                Log.d(TAG, "Response postReceipt() failure");
                purchaseIDfromAPI.postValue(null);
                Toast.makeText(context, "Error - please check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
        }
    }

