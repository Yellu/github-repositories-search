package com.mapprr.gitsearch.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.mapprr.gitsearch.R;
import com.mapprr.gitsearch.database.DBManager;
import com.mapprr.gitsearch.database.RepoResultsEntity;
import com.mapprr.gitsearch.database.RepositoryEntity;
import com.mapprr.gitsearch.network.NetworkManager;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by appigizer on 17/1/18.
 */

public class HomeFragment extends Fragment {
    @BindView(R.id.repoList)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.dataLoadProgress)
    ContentLoadingProgressBar dataLoadProgress;
    @BindView(R.id.tv_error_message)
    TextView tv_error_message;

    private SearchView searchView;
    private EditText editSearch;
    private MenuItem searchMenuItem;
    private RepoListAdapter repoListAdapter;
    private RealmResults<RepositoryEntity> repositoryEntities;
    private Realm realm;
    private RepoResultsEntity repoResultsEntity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repo_list_fragment, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataLoadProgress.hide();
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(true);
            mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        }

        repoResultsEntity = realm.where(RepoResultsEntity.class).findFirst();
        if (repoResultsEntity != null){
            repositoryEntities = repoResultsEntity.items.where().findAll().sort("watchers_count", Sort.DESCENDING);
        }

        if (repositoryEntities == null || repositoryEntities.isEmpty()){
            tv_error_message.setVisibility(View.VISIBLE);
        }

        repoListAdapter = new RepoListAdapter(repositoryEntities, getActivity(), true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(repoListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        this.searchMenuItem = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();

        if (searchView != null) {
            editSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    hideSoftKeyboard(getActivity());
                    fetchRepos(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    private void reloadAdapter(){
        repoResultsEntity = realm.where(RepoResultsEntity.class).findFirst();
        if (repoResultsEntity != null){
            repositoryEntities = repoResultsEntity.items.where().findAll().sort("watchers_count", Sort.DESCENDING);
        }

        if (repositoryEntities == null || repositoryEntities.isEmpty()){
            tv_error_message.setVisibility(View.VISIBLE);
        } else {
            tv_error_message.setVisibility(View.GONE);
        }
        repoListAdapter.updateAdapter(repositoryEntities);
        repoListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSearch:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        editSearch.setTextColor(Color.WHITE);
        menu.findItem(R.id.menuSearch).setVisible(true);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void fetchRepos(String query){
        tv_error_message.setVisibility(View.GONE);
        dataLoadProgress.show();

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("q", query);
        queryMap.put("sort", "watcher_count");
        queryMap.put("order", "desc");
        queryMap.put("per_page", 10);

        Call<ResponseBody> request = NetworkManager.getInstance().searchRequest(getActivity(), queryMap);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    DBManager.getInstance().createRepos(response.body());
                    reloadAdapter();
                    dataLoadProgress.hide();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dataLoadProgress.hide();
            }
        });
    }

    @Override
    public void onStop() {
//close the search view here.
        //Collapse the search view
        searchView.onActionViewCollapsed();
        //Collapse the search widget
        searchMenuItem.collapseActionView();
        super.onStop();
    }

}
