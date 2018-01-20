package com.mapprr.gitsearch.repoDetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mapprr.gitsearch.R;
import com.mapprr.gitsearch.SettingsManager;
import com.mapprr.gitsearch.database.ContributorEntity;
import com.mapprr.gitsearch.database.DBManager;
import com.mapprr.gitsearch.database.OwnerEntity;
import com.mapprr.gitsearch.database.RepositoryEntity;
import com.mapprr.gitsearch.event.ProjectLinkEvent;
import com.mapprr.gitsearch.network.NetworkManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.dataLoadProgress)
    ContentLoadingProgressBar dataLoadProgress;
    @BindView(R.id.img_contributor_avatar)
    ImageView contributorAvatar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    private Realm realm;
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
        realm = Realm.getDefaultInstance();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataLoadProgress.hide();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        String projectLink = repositoryEntity.html_url;
        String description = repositoryEntity.description;
        OwnerEntity ownerEntity = repositoryEntity.owner;
        String url = ownerEntity.avatar_url;
        String name = repositoryEntity.name;
        collapsingToolbar.setTitle(name);

        Glide.with(getActivity())
                .asBitmap()
                .load(url)
                .apply(RequestOptions.circleCropTransform()
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder))
                .into(contributorAvatar);

        if (projectLink == null){

        } else {
            String text = "<a href='http://www.google.com'>" + projectLink + "</a>";
            tvProjectLink.setText(Html.fromHtml(text));
        }

        if (description == null){

        } else {
            tvDescription.setText(description);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        contributorGridAdapter = new ContributorGridAdapter(getActivity());
        recyclerView.setAdapter(contributorGridAdapter);

        getContributors(repositoryEntity.contributors_url);
    }

    private void updateAdapter(){
        RealmResults<ContributorEntity> contributorEntities = realm.where(ContributorEntity.class).equalTo("parentRepoId", repositoryEntity.id).findAll();
        contributorGridAdapter.updateAdapter(contributorEntities);
        contributorGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.llProjectLink)
    public void openLink(){
        String projectLink = repositoryEntity.html_url;
        EventBus.getDefault().post(new ProjectLinkEvent(projectLink));
    }

    private void getContributors(String url){
        dataLoadProgress.show();
        Call<ResponseBody> request = NetworkManager.getInstance().contributorsRequest(getActivity(), url);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    DBManager.getInstance().createContributorsFromJsonResponse(response.body(), repositoryEntity.id);
                    updateAdapter();
                }
                dataLoadProgress.hide();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dataLoadProgress.hide();
            }
        });
    }
}
