package com.mapprr.gitsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapprr.gitsearch.database.ContributorEntity;
import com.mapprr.gitsearch.database.DBManager;
import com.mapprr.gitsearch.network.NetworkManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by appigizer on 17/1/18.
 */

public class ContributorDetailsFragment extends Fragment {
    private ContributorEntity contributorEntity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contributorEntity = SettingsManager.getInstance().contributorEntity;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contributorDetailsfetch(contributorEntity.repos_url);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void contributorDetailsfetch(String url){
        Call<ResponseBody> request = NetworkManager.getInstance().getOwnerReposRequest(getActivity(), url);

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

}
