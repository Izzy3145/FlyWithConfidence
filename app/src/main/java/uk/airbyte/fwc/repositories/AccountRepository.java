package uk.airbyte.fwc.repositories;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.model.User;

public class AccountRepository {

    private static final String TAG = AccountRepository.class.getSimpleName();

    private final Realm realmInstance;
    private final RealmResults<User> userRealm;


    public AccountRepository() {
        realmInstance = Realm.getDefaultInstance();
        userRealm = realmInstance.where(User.class).findAll();
    }

    public void updateUserDetailsRealm(final User body) {
        final String userID = body.getId();
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("id", userID).findFirst();
                if (user != null) {
                    user.setFirstName(body.getFirstName());
                    user.setLastName(body.getLastName());
                    user.setEmailAddress(body.getEmailAddress());
                    Log.d(TAG, "Realm user first name: " + user.getFirstName());
                    Log.d(TAG, "Realm user last name: " + user.getLastName());
                    Log.d(TAG, "Realm user id: " + user.getId());
                    Log.d(TAG, "Realm user accessToken: " + user.getAccessToken());
                }
            }
        });
    }
}
