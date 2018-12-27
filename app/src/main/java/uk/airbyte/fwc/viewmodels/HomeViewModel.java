package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

public class HomeViewModel extends ViewModel {

    private final static String TAG = HomeViewModel.class.getSimpleName();
    private MutableLiveData<String> selectedVideo = new MutableLiveData<String>();

    public void select(String video) {
        Log.d(TAG, "Video selected: "+ video);
        selectedVideo.setValue(video);
    }

    public LiveData<String> getSelected() {

        if(selectedVideo == null) {
            selectedVideo.setValue("testingtesting");
            Log.d(TAG, "Video retrieved " + selectedVideo);

        }
            return selectedVideo;
        }

        //TODO: get list of favourited Modules to display in recyclerView
    }

