package com.github.search.database;

import java.util.HashMap;
import java.util.Map;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yellappa on 18/1/18.
 */

public class OwnerEntity extends RealmObject {
    public String login;
    @PrimaryKey
    public int id;
    public String avatar_url;
    public String gravatar_id;
    public String url;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gists_url;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String repos_url;
    public String events_url;
    public String received_events_url;
    public String type;
    public Boolean site_admin;

//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }

}
