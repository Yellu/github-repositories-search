package com.mapprr.gitsearch.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.mapprr.gitsearch.R;
import com.mapprr.gitsearch.Utilities;
import com.mapprr.gitsearch.database.DBManager;
import com.mapprr.gitsearch.database.RepoResultsEntity;
import com.mapprr.gitsearch.database.RepositoryEntity;
import com.mapprr.gitsearch.network.NetworkManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.greenrobot.eventbus.EventBus.TAG;

/**
 * Created by appigizer on 17/1/18.
 */

public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{
    @BindView(R.id.repoList)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.dataLoadProgress)
    ContentLoadingProgressBar dataLoadProgress;
    @BindView(R.id.tv_error_message)
    TextView tv_error_message;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;
    @BindView(R.id.star)
    RadioButton rbtnStar;
    @BindView(R.id.fork)
    RadioButton rbtnFork;
    @BindView(R.id.updated)
    RadioButton rbtnUpdated;
    @BindView(R.id.desc)
    RadioButton rbtnDesc;
    @BindView(R.id.asc)
    RadioButton rbtnAsc;
    @BindView(R.id.llFromDate)
    LinearLayout llFromDate;
    @BindView(R.id.llToDate)
    LinearLayout llToDate;
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;

    @BindView(R.id.tvEndDate)
    TextView tvEndDate;

    private SearchView searchView;
    private EditText editSearch;
    private MenuItem searchMenuItem;
    private RepoListAdapter repoListAdapter;
    private RealmResults<RepositoryEntity> repositoryEntities;
    private Realm realm;
    private RepoResultsEntity repoResultsEntity;
    private boolean expanded = false;
    private String sortBy;
    private String orderBy;

    private Calendar dateRangeCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
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
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

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

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
//                //if expanded hide the panel
//                if (expanded) {
//                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//                    expanded = false;
//                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
//                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    //set the expanded flag true to mark the expanded panel state.
//                    expanded = true;
//                }
            }
        });

        mLayout.setFadeOnClickListener(view -> mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));

        dateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
            dateRangeCalendar.set(Calendar.YEAR, year);
            dateRangeCalendar.set(Calendar.MONTH, monthOfYear);
            dateRangeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(isStartDate);
        };

        rbtnStar.setOnCheckedChangeListener(this);
        rbtnFork.setOnCheckedChangeListener(this);
        rbtnUpdated.setOnCheckedChangeListener(this);
        rbtnDesc.setOnCheckedChangeListener(this);
        rbtnAsc.setOnCheckedChangeListener(this);

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
        this.searchMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        if (searchView != null) {
            editSearch = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    hideSoftKeyboard(getActivity());
                    if (query.isEmpty() || query.length() < 2){
                        return true;
                    }
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
            case R.id.menu_filter:
                bottomSheet();
                if (mLayout != null) {
                    if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    } else {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void bottomSheet(){
//        DialogFragment newFragment = MyAlertDialogFragment.newInstance(
//                R.string.app_name);
//        newFragment.show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        editSearch.setTextColor(Color.WHITE);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_filter).setVisible(true);
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


    private Date startDate;
    private Date endDate;
    private boolean isStartDate;
    @OnClick(R.id.llFromDate)
    void pickStartDate(){
        isStartDate = true;
        datePicker();
    }

    @OnClick(R.id.llToDate)
    void pickEndDate(){
        isStartDate = false;
        datePicker();
    }


    private void datePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, dateRangeCalendar
                .get(Calendar.YEAR), dateRangeCalendar.get(Calendar.MONTH),
                dateRangeCalendar.get(Calendar.DAY_OF_MONTH));

        //date can't be later than today
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);

//        String minDateStr = "2017-10-16T12:27:18.284Z";
//        Date minDate = Utilities.stringToDate(minDateStr);
//        minDate.after(new Date());

//        Calendar minDateCalendar = Calendar.getInstance();
//        minDateCalendar.setTime(minDate);
//        minDateCalendar.set(Calendar.HOUR, 11);
//        minDateCalendar.set(Calendar.MINUTE, 59);

        DatePicker datePickerView = datePickerDialog.getDatePicker();
        datePickerView.setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateLabel(boolean isStartDate){
        String dateFormat = "dd-MMM-yyyy";
        String dateToString;

        if (isStartDate){
            startDate = dateRangeCalendar.getTime();
            dateToString = Utilities.dateToString(startDate, dateFormat);
            if (endDate == null || startDate.compareTo(endDate) < 0){
                tvStartDate.setText(dateToString);
            } else {
                if (this.isAdded() && this.getUserVisibleHint()) {
                    Toast.makeText(getActivity(), "Enter Valid date", Toast.LENGTH_SHORT).show();
                }
                tvStartDate.setText(dateFormat);
            }
        } else {
            endDate = dateRangeCalendar.getTime();
            dateToString = Utilities.dateToString(endDate, dateFormat);

            if (startDate == null || startDate.compareTo(endDate) < 0){
                tvEndDate.setText(dateToString);
            } else {
                if (this.isAdded() && this.getUserVisibleHint()) {
                    Toast.makeText(getActivity(), "Enter Valid date", Toast.LENGTH_SHORT).show();
                }
                tvEndDate.setText(dateFormat);
            }
        }
    }

    @OnClick(R.id.btn_save_filter)
    void saveFilter(){
        if (sortBy == null){
            //close filter
            return;
        }
        RealmQuery<RepositoryEntity> realmQuery = repositoryEntities.where();
//                .between("created_at", startDate, endDate);
        if (orderBy != null && orderBy.equalsIgnoreCase("desc")){
            realmQuery = realmQuery.sort(sortBy, Sort.DESCENDING);
        } else {
            realmQuery = realmQuery.sort(sortBy, Sort.ASCENDING);
        }
        repositoryEntities = realmQuery.findAll();
        repoListAdapter.updateAdapter(repositoryEntities);
        repoListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.star:
                sortBy = "stargazers_count";
                break;
            case R.id.fork:
                sortBy = "forks_count";
                break;
            case R.id.updated:
                sortBy = "updated_at";
                break;
            case R.id.desc:
                orderBy = "desc";
                break;
            case R.id.asc:
                orderBy = "asc";
                break;
        }
    }
}
