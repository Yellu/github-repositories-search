package com.mapprr.gitsearch.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by appigizer on 18/1/18.
 */

public class RepoResultsEntity extends RealmObject {
    @PrimaryKey
    public int total_count;
    public boolean incomplete_results;
    public RealmList<RepositoryEntity> items = null;
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }
}
