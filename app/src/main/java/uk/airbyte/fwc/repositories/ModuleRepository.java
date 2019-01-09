package uk.airbyte.fwc.repositories;

import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import uk.airbyte.fwc.model.Module;

public class ModuleRepository {

    private final Realm realmInstance;
    private final RealmResults<Module> moduleRealm;

    public ModuleRepository() {
        realmInstance = Realm.getDefaultInstance();
        moduleRealm = realmInstance.where(Module.class).findAll();
    }

    public void addChangeListener(OrderedRealmCollectionChangeListener<RealmResults<Module>> listener) {
        moduleRealm.addChangeListener(listener);
    }

    public void clearListeners() {
        moduleRealm.removeAllChangeListeners();
    }

    public RealmResults<Module> getRealmFavourites() {
        RealmResults<Module> realmFavourites = realmInstance.where(Module.class)
                .equalTo("favourited", true)
                .findAll();
        realmFavourites.sort("lastViewed", Sort.DESCENDING);
        return realmFavourites;
    }

    public RealmResults<Module> getRealmRecents() {
        RealmResults<Module> realmRecents = realmInstance.where(Module.class)
                .notEqualTo("lastViewed", 0)
                .findAll();
        realmRecents.sort("lastViewed", Sort.DESCENDING);
        return realmRecents;
    }

    public void deleteRealmRecent(final String moduleID){
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", moduleID)
                        .findFirst();
                unfavouritedModule.setLastViewed(0);
            }
        });
    }

    public void deleteRealmFavourite(final String moduleID) {
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", moduleID)
                        .findFirst();
                unfavouritedModule.setFavourited(false);
            }
        });
    }
}
