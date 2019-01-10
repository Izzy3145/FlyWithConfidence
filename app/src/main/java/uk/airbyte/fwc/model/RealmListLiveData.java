package uk.airbyte.fwc.model;

import android.arch.lifecycle.LiveData;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class RealmListLiveData<Module> extends LiveData<RealmResults<Module>> {

    private RealmResults<Module> results;

    private final RealmChangeListener<RealmResults<Module>> listener = new RealmChangeListener<RealmResults<Module>>() {
        @Override
        public void onChange(RealmResults<Module> results) {
            setValue(results);
        }
    };

    public RealmListLiveData(RealmResults<Module> realmResults) {
        results = realmResults;
    }

    @Override
    protected void onActive() {
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        results.removeChangeListener(listener);
    }
}
