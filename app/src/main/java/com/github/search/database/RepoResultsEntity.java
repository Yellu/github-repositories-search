package com.github.search.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yellappa on 18/1/18.
 */

public class RepoResultsEntity extends RealmObject {
    @PrimaryKey
    public int total_count;
    public boolean incomplete_results;
    public RealmList<RepositoryEntity> items = null;
}
