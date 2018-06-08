package com.github.search;

import com.github.search.database.ContributorEntity;
import com.github.search.database.RepositoryEntity;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

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

    static long hashString64Bit(CharSequence str) {
        long result = 0xcbf29ce484222325L;
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            result ^= str.charAt(i); result *= 0x100000001b3L;
        }
        return result;
    }
}
