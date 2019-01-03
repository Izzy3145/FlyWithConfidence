package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import uk.airbyte.fwc.model.ShowPlay;

public class HomeViewModel extends ViewModel {

    private final static String TAG = HomeViewModel.class.getSimpleName();
    private MutableLiveData<ShowPlay> selectedVideo = new MutableLiveData<ShowPlay>();

    public void select(ShowPlay imageOrVideo) {
        Log.d(TAG, "Video selected: " + imageOrVideo);
        selectedVideo.setValue(imageOrVideo);
    }

    public LiveData<ShowPlay> getSelected() {
        return selectedVideo;
    }

    public void clearVideo() {
        selectedVideo =  new MutableLiveData<ShowPlay>();
    }

}

