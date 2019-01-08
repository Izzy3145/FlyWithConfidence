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
                }
            }
        });
    }

    public void deleteRealmContents(){
        realmInstance.close();
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }
}
