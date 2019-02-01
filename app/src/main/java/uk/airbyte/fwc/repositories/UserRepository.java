package uk.airbyte.fwc.repositories;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.airbyte.fwc.model.User;

public class UserRepository {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final Realm realmInstance;
    private final RealmResults<User> userRealm;
    private User user;
    private String mAccessToken;
    private User foundUser;

    public UserRepository() {
        realmInstance = Realm.getDefaultInstance();
        userRealm = realmInstance.where(User.class).findAll();
    }

    public void updateUserDetailsRealm(User body) {
        user = body;
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(user);
            }
        });
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

    public User getUserDetailsRealm(String accessToken){
        mAccessToken = accessToken;
        realmInstance.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("accessToken", mAccessToken).findFirst();
                if(user != null){
                    foundUser = user;
                }
            }
        });
        return foundUser;
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

    public void onDestroy(){
        realmInstance.close();
    }
}
