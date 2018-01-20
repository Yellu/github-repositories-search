package com.mapprr.gitsearch.database;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

/**
 * Created by appigizer on 18/1/18.
 */

public class DBManager {
    private static DBManager dbManager = null;
    private Realm realm;
    private DBManager(){
        realm = Realm.getDefaultInstance();
    }

    public static DBManager getInstance(){
        if (dbManager == null){
            dbManager = new DBManager();
        }
        return  dbManager;
    }

    public void createRepos(ResponseBody responseBody){
        try {
            String jsonStr = responseBody.string();
            realm.beginTransaction();
            RepoResultsEntity repoResultsEntity = realm.where(RepoResultsEntity.class).findFirst();

            if (repoResultsEntity != null){
                repoResultsEntity.deleteFromRealm();
            }
            realm.createOrUpdateObjectFromJson(RepoResultsEntity.class, jsonStr);
            realm.commitTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createContributorsFromJsonResponse(ResponseBody responseBody, int repoId){
        try {
            String jsonString = responseBody.string();
            JSONArray jsonArray = new JSONArray(jsonString);
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(ContributorEntity.class, jsonArray);

            for (ContributorEntity contributorEntity: realm.where(ContributorEntity.class).findAll()){
                contributorEntity.parentRepoId = repoId;
            }
            realm.commitTransaction();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createReposFromJsonResponse(ResponseBody responseBody){
        try {
            String jsonString = responseBody.string();
            JSONArray jsonArray = new JSONArray(jsonString);
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(RepositoryEntity.class, jsonArray);
            realm.commitTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
