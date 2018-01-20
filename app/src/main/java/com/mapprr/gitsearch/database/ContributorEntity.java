package com.mapprr.gitsearch.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by appigizer on 20/1/18.
 */

public class ContributorEntity extends RealmObject {
    @PrimaryKey
    public Integer id;
    public String login;
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
    public Integer contributions;
    public int parentRepoId;
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//
//    @JsonAnyGetter
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    @JsonAnySetter
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }

}
