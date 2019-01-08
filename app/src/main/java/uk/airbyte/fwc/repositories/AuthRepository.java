package uk.airbyte.fwc.repositories;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.model.User;

public class AuthRepository {

    private static final String TAG = AuthRepository.class.getSimpleName();

    private final Realm realmInstance;
    private final RealmResults<User> userRealm;


    public AuthRepository() {
        realmInstance = Realm.getDefaultInstance();
        userRealm = realmInstance.where(User.class).findAll();
    }

    public void registerUserRealm(final User body) {

        realmInstance.executeTransactionAsync(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                User user = realm.createObject(User.class, body.getId());
                user.setFirstName(body.getFirstName());
                user.setLastName(body.getLastName());
                user.setEmailAddress(body.getEmailAddress());
                user.setAccessToken(body.getAccessToken());
            }
        });
    }

}
