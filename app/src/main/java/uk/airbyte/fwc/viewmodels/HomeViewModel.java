package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> selectedVideo = new MutableLiveData<String>();

    public void select(String string) {
        selectedVideo.setValue(string);
    }

    public LiveData<String> getSelected() {
        return selectedVideo;
    }
}
