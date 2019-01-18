package uk.airbyte.fwc.repositories;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import uk.airbyte.fwc.model.Module;

public class ModuleRepository {

    private final Realm realmInstance;
    private final RealmResults<Module> moduleRealm;
    private List<Module> listOfModules = new ArrayList<Module>();
    private Module favModule;
    private Boolean mIsFavourite;
    private Boolean vidIsFavourite;
    private String mDelRecentID;
    private String mDelFavID;

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

    public boolean getFavouriteStatus(String moduleID){
        Module module = moduleRealm.where().equalTo("id", moduleID).findFirst();
        if(module.getFavourited() == null) {
            return false;
        } else {
            return module.getFavourited();
        }
    }

    public void setRealmFavouriteFromID(Boolean isFavourite, String moduleID){
        final Module module = moduleRealm.where().equalTo("id", moduleID).findFirst();
        vidIsFavourite = isFavourite;
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                module.setFavourited(vidIsFavourite);
                realm.copyToRealmOrUpdate(module);
            }
        });
    }

    public void setRealmFavourite(Boolean isFavourite, Module module){
        mIsFavourite = isFavourite;
        favModule = module;
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                favModule.setFavourited(mIsFavourite);
                realm.copyToRealmOrUpdate(favModule);
            }
        });
    }

    public RealmResults<Module> getModulesForCategory(String topicID, String categoryName){
        RealmResults<Module> realmCategoryModules = moduleRealm.where()
                .equalTo("topic.id", topicID)
                .equalTo("category.name", categoryName)
                .findAll();
        realmCategoryModules.sort("displayOrder");
        return realmCategoryModules;
    }
    public RealmResults<Module> getModulesForTopic(String topicID){
        RealmResults<Module> realmTopicModules = moduleRealm.where().equalTo("topic.id", topicID).findAll();
        realmTopicModules.sort("displayOrder");
        return realmTopicModules;
    }

    public Module getModuleFromID(String moduleID){
        return moduleRealm.where().equalTo("id", moduleID).findFirst();
    }

    public RealmResults<Module> getModulesToDisplay(){
        moduleRealm.sort("displayOrder");
        return moduleRealm;
    }

    public void copyTopicModulesToRealm(List<Module> listOfModulesForTopic){
        //TODO: is this bit wrong?
        if(listOfModulesForTopic != null) {
            listOfModules.addAll(listOfModulesForTopic);
            realmInstance.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(listOfModules);
                }
            });
        }
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

    public void deleteRealmRecent(String moduleID){
        mDelRecentID = moduleID;
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", mDelRecentID)
                        .findFirst();
                unfavouritedModule.setLastViewed(0);
            }
        });
    }

    public void deleteRealmFavourite(String moduleID) {
        mDelFavID = moduleID;
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Module unfavouritedModule = realm.where(Module.class)
                        .equalTo("id", mDelFavID)
                        .findFirst();
                unfavouritedModule.setFavourited(false);
            }
        });
    }

    public void onDestroy(){
        realmInstance.close();
    }
}
