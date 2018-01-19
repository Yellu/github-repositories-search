package com.mapprr.gitsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.mapprr.gitsearch.database.DBManager;
import com.mapprr.gitsearch.event.RepoDetailsEvent;
import com.mapprr.gitsearch.network.NetworkManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
        fetchRepos();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.repo, new HomeFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void fetchRepos(){
        Call<ResponseBody> request = NetworkManager.getInstance().searchRequest(this);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    DBManager.getInstance().createRepos(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void launchRepoDetails(RepoDetailsEvent repoDetailsEvent){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.repo, new RepoDetailsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
