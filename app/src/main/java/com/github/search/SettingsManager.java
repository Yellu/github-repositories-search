package com.github.search;

import com.github.search.database.ContributorEntity;
import com.github.search.database.RepositoryEntity;

/**
 * Created by yellappa on 20/1/18.
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
