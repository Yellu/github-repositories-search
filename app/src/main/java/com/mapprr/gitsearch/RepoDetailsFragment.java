package com.mapprr.gitsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapprr.gitsearch.database.ContributorEntity;
import com.mapprr.gitsearch.database.DBManager;
import com.mapprr.gitsearch.database.RepositoryEntity;
import com.mapprr.gitsearch.network.NetworkManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by appigizer on 17/1/18.
 */

public class RepoDetailsFragment extends Fragment {
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_project_link)
    TextView tvProjectLink;
    @BindView(R.id.tv_description)
    TextView tvDescription;

    private RepositoryEntity repositoryEntity;
    private ContributorGridAdapter contributorGridAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repo_details_fragment, container, false);
        ButterKnife.bind(this, view);
        repositoryEntity = SettingsManager.getInstance().repositoryEntity;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        String projectLink = repositoryEntity.html_url;
        String description = repositoryEntity.description;

        if (projectLink == null){

        } else {
            tvProjectLink.setText(projectLink);
            tvProjectLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("hjg", "j");
                }
            });
        }

        if (description == null){

        } else {
            tvDescription.setText(description);
        }
//
//        String linkedText = "<b>text3:</b>  Text with a " +
//                String.format("<a href=\"%s\">link</a> ", projectLink) +
//                "created in the Java source code using HTML.";
//
//        tvProjectLink.setText(Html.fromHtml(linkedText));
//        tvProjectLink.setMovementMethod(LinkMovementMethod.getInstance());

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        contributorGridAdapter = new ContributorGridAdapter(getActivity());
        recyclerView.setAdapter(contributorGridAdapter);

        getContributors(repositoryEntity.contributors_url);
    }

    private void updateAdapter(){
        RealmResults<ContributorEntity> contributorEntities = Realm.getDefaultInstance().where(ContributorEntity.class).findAll();
        contributorGridAdapter.updateAdapter(contributorEntities);
        contributorGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getContributors(String url){
        Call<ResponseBody> request = NetworkManager.getInstance().contributorsRequest(getActivity(), url);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    DBManager.getInstance().createContributorsFromJsonResponse(response.body());
                    updateAdapter();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
