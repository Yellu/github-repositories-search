package com.mapprr.gitsearch.repoDetails;

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
import com.mapprr.gitsearch.R;
import com.mapprr.gitsearch.SettingsManager;
import com.mapprr.gitsearch.database.ContributorEntity;
import com.mapprr.gitsearch.event.ContributorDetailsEvent;
import org.greenrobot.eventbus.EventBus;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Created by appigizer on 20/1/18.
 */

public class ContributorGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private RealmResults<ContributorEntity> contributorEntities;
    public ContributorGridAdapter(Activity activity){
        this.context = activity;
    }

    public void updateAdapter(RealmResults<ContributorEntity> contributorEntities){
        this.contributorEntities = contributorEntities;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new ContributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ContributorViewHolder viewHolder = (ContributorViewHolder) holder;
        final ContributorEntity contributorEntity = contributorEntities.get(position);
        String url = contributorEntity.avatar_url;
        String name = contributorEntity.login;
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder))
                .into(viewHolder.contributorAvatar);

        viewHolder.contributorName.setText(name);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsManager.getInstance().contributorEntity = contributorEntities.get(viewHolder.getAdapterPosition());
                EventBus.getDefault().post(new ContributorDetailsEvent());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contributorEntities == null){
            return 0;
        }
        return contributorEntities.size();
    }

    public static class ContributorViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img_contributor_avatar)
        ImageView contributorAvatar;
        @BindView(R.id.tv_contributor_name)
        TextView contributorName;

        public ContributorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
