package com.mapprr.gitsearch;

import com.mapprr.gitsearch.database.ContributorEntity;
import com.mapprr.gitsearch.database.RepositoryEntity;

/**
 * Created by appigizer on 20/1/18.
 */

public class SettingsManager {
    private static SettingsManager instance = null;
    public RepositoryEntity repositoryEntity;
    public ContributorEntity contributorEntity;

    private SettingsManager(){

    }

    public static SettingsManager getInstance(){
        if (instance == null){
            instance = new SettingsManager();
        }
        return instance;
    }
}
