package com.mapprr.gitsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.mapprr.gitsearch.event.RepoDetailsEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by appigizer on 17/1/18.
 */

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.repo, new HomeFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void launchRepoDetails(RepoDetailsEvent repoDetailsEvent){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.repo, new RepoDetailsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Subscribe
    public void launchContributorDetails(RepoDetailsEvent repoDetailsEvent){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.repo, new ContributorDetailsFragment())
                .addToBackStack(null)
                .commit();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
