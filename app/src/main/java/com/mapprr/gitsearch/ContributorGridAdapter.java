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
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by appigizer on 20/1/18.
 */

public class ContributorGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<String> stringList = new ArrayList<>();
    public ContributorGridAdapter(Activity activity){
        this.context = activity;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stringList.add("1");
        stringList.add("2");stringList.add("3");stringList.add("4");stringList.add("5");stringList.add("6");stringList.add("7");stringList.add("8");stringList.add("9");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new ContributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContributorViewHolder viewHolder = (ContributorViewHolder) holder;
        Glide.with(context)
                .asBitmap()
                .load("")
                .apply(RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder))
                .into(viewHolder.contributorAvatar);

        viewHolder.contributorName.setText(R.string.app_name);
    }

    @Override
    public int getItemCount() {
        return 20;
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
