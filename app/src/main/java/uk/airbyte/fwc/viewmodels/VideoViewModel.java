package uk.airbyte.fwc.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.android.exoplayer2.SimpleExoPlayer;

import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.repositories.ModuleRepository;
import uk.airbyte.fwc.repositories.VideoRepository;

public class VideoViewModel extends ViewModel {

    private final static String TAG = VideoViewModel.class.getSimpleName();
    private MutableLiveData<ShowPlay> selectedVideo = new MutableLiveData<ShowPlay>();
    private MutableLiveData<Boolean> favStatus = new MutableLiveData<Boolean>();

    private final VideoRepository videoRepository;

    public VideoViewModel() {
        videoRepository = new VideoRepository();
    }

    public void select(ShowPlay imageOrVideo) {
        Log.d(TAG, "Video selected: " + imageOrVideo);
        selectedVideo.postValue(imageOrVideo);
    }

    public LiveData<ShowPlay> getSelected() {
        return selectedVideo;
    }

    public LiveData<Boolean> getFav() {
        return favStatus;
    }

    public void setFav(Boolean favOnOff){
        favStatus.postValue(favOnOff);
    }

    public void clearVideo() {
        selectedVideo.setValue(null);
    }

    public void setVideoPosition(ShowPlay showPlay, SimpleExoPlayer mSimpleExoPlayer) {
        if (!showPlay.isIntro()) {
            videoRepository.setPositionRealm(showPlay, mSimpleExoPlayer);
        }
    }

    public void closeRealm(){
        videoRepository.onDestroy();
    }

}

