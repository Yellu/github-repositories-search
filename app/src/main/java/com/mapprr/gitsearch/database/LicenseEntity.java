package com.mapprr.gitsearch.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by appigizer on 18/1/18.
 */

public class LicenseEntity extends RealmObject {
    @PrimaryKey
    public String key;
    public String name;
    public String spdx_id;
    public String url;
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
