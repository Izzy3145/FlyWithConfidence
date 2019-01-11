package uk.airbyte.fwc.repositories;

import android.util.Log;

import com.google.android.exoplayer2.SimpleExoPlayer;

import io.realm.Realm;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;

public class VideoRepository {

    private static final String TAG = VideoRepository.class.getSimpleName();

    private final Realm realmInstance;
    private Module mModule;

    public VideoRepository() {
        realmInstance = Realm.getDefaultInstance();
    }

    public void setPositionRealm(ShowPlay showPlayObj, final SimpleExoPlayer mSimpleExoPlayer){
                mModule = realmInstance.where(Module.class)
                .equalTo("id", showPlayObj.getModuleID())
                .findFirst();
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mModule.setCurrentWindow(mSimpleExoPlayer.getCurrentWindowIndex());
                mModule.setPlayerPosition((int) mSimpleExoPlayer.getCurrentPosition());
                mModule.setLastViewed(System.currentTimeMillis());
                Log.d(TAG, "Current time: " + System.currentTimeMillis());
                realm.copyToRealmOrUpdate(mModule);
            }
        });
    }

}
