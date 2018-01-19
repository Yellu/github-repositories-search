package com.mapprr.gitsearch;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mapprr.gitsearch.database.OwnerEntity;
import com.mapprr.gitsearch.database.RepositoryEntity;
import com.mapprr.gitsearch.event.RepoDetailsEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Created by appigizer on 18/1/18.
 */

public class RepoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   private RealmResults<RepositoryEntity> repositoryEntities;
   private Context context;
    public RepoListAdapter(RealmResults<RepositoryEntity> repositoryEntities, Activity activity){
        this.repositoryEntities = repositoryEntities;
        context = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.repo_list_item, parent, false);
        return new RepoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RepoViewHolder repoViewHolder = (RepoViewHolder) holder;
        RepositoryEntity repositoryEntity = repositoryEntities.get(position);

        String name = repositoryEntity.name;
        String fullName = repositoryEntity.full_name;
        int watcherCount = repositoryEntity.watchers_count;
        int commitCount = repositoryEntity.forks_count;
        OwnerEntity ownerEntity = repositoryEntity.owner;
        String url = ownerEntity.avatar_url;

        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.circleCropTransform()
                .placeholder(R.drawable.ic_watcher_count)
                .error(R.drawable.ic_watcher_count))
                        .into(repoViewHolder.imageView);

        repoViewHolder.tvName.setText(name);
        repoViewHolder.tvFullName.setText(fullName);
        repoViewHolder.tvWatcherCount.setText(String.valueOf(watcherCount));
        repoViewHolder.tvCommitCount.setText(String.valueOf(commitCount));

        repoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RepoDetailsEvent());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (repositoryEntities == null){
            return 0;
        }
        return repositoryEntities.size();
    }

    public static class RepoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvFullName) TextView tvFullName;
        @BindView(R.id.tvWatcherCount) TextView tvWatcherCount;
        @BindView(R.id.tvCommitCount) TextView tvCommitCount;
        @BindView(R.id.thumbnail)
        ImageView imageView;

        public RepoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
