package uk.airbyte.fwc.repositories;

import com.google.android.exoplayer2.SimpleExoPlayer;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.model.User;

public class VideoRepository {

    private static final String TAG = VideoRepository.class.getSimpleName();

    private final Realm realmInstance;

    public VideoRepository() {
        realmInstance = Realm.getDefaultInstance();
    }

    public void setPositionRealm(ShowPlay showPlayObj, final SimpleExoPlayer mSimpleExoPlayer){
        final Module mModule = realmInstance.where(Module.class)
                .equalTo("id", showPlayObj.getModuleID())
                .findFirst();
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mModule.setCurrentWindow(mSimpleExoPlayer.getCurrentWindowIndex());
                mModule.setPlayerPosition((int) mSimpleExoPlayer.getCurrentPosition());
                mModule.setLastViewed(System.currentTimeMillis());
                realm.copyToRealmOrUpdate(mModule);
            }
        });
    }

}
