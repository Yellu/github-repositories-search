package com.github.search.fastadapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.github.search.R;
import com.github.search.database.RepoResultsEntity;
import com.github.search.database.RepositoryEntity;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaInAnimator;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialize.MaterializeBuilder;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

public class RealmActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter<RepositoryEntity> mFastItemAdapter;
    //save our Realm instance to close it later
    private Realm mRealm;
    private RealmResults<RepositoryEntity> repositoryEntities;
    private RepoResultsEntity repoResultsEntity;
    private ItemAdapter footerAdapter;


    //endless scroll
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_fast_adapter);

        // Handle Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_realm_list);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter which will manage everything
        mFastItemAdapter = new FastItemAdapter<>();

//        //create our FooterAdapter which will manage the progress items
        footerAdapter = items();
        mFastItemAdapter.addAdapter(1, footerAdapter);

        //configure our fastAdapter
        mFastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
            Toast.makeText(v.getContext(), item.getName(), Toast.LENGTH_SHORT).show();
            return false;
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new AlphaInAnimator());
        rv.setAdapter(mFastItemAdapter);

        //Get a realm instance for this activity
        mRealm = Realm.getDefaultInstance();
        //Add a realm on change listener (don´t forget to close this realm instance before adding this listener again)
        mRealm.where(RepositoryEntity.class).findAllAsync().addChangeListener(userItems -> {
            //This will call twice
            //1.) from findAllAsync()
            //2.) from createData()
            mFastItemAdapter.setNewList(userItems);
        });



        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(final int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                //simulate networking (2 seconds)
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    footerAdapter.clear();
                    for (int i = 1; i < 16; i++) {
                        int finalI = i;
                        mRealm.where(RepositoryEntity.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<RepositoryEntity>>() {
                            @Override
                            public void onChange(RealmResults<RepositoryEntity> userItems) {
                                //Remove the change listener
                                userItems.removeChangeListener(this);
                                //Store the primary key to get access from a other thread
                                final long newPrimaryKey = userItems.last().getIdentifier() + finalI;
                                mRealm.executeTransactionAsync(realm -> {
                                    RepositoryEntity newUser = realm.createObject(RepositoryEntity.class, newPrimaryKey);
                                    newUser.withName("Sample Realm Element " + newPrimaryKey);
                                });
                            }
                        });
                    }
                }, 2000);
            }
        };
        rv.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill with some sample data
        createData();

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        //restore selections (this has to be done after the items were added
        mFastItemAdapter.withSavedInstanceState(savedInstanceState);
    }

    private void createData() {
        //Execute transaction
        mRealm.executeTransactionAsync(realm -> {
//            List<RealmSampleUserItem> users = new LinkedList<>();
            repoResultsEntity = realm.where(RepoResultsEntity.class).findFirst();
            if (repoResultsEntity != null){
                repositoryEntities = repoResultsEntity.items.where().findAll().sort("watchers_count", Sort.DESCENDING);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        menu.findItem(R.id.item_add).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_plus_square).color(Color.BLACK).actionBar());
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.item_add:
                mRealm.where(RepositoryEntity.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<RepositoryEntity>>() {
                    @Override
                    public void onChange(RealmResults<RepositoryEntity> userItems) {
                        //Remove the change listener
                        userItems.removeChangeListener(this);
                        //Store the primary key to get access from a other thread
                        final long newPrimaryKey = userItems.last().getIdentifier() + 1;
                        mRealm.executeTransactionAsync(realm -> {
                            RepositoryEntity newUser = realm.createObject(RepositoryEntity.class, newPrimaryKey);
                            newUser.withName("Sample Realm Element " + newPrimaryKey);
                        });
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Prevent the realm instance from leaking
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeRealm();
    }

    private void closeRealm() {
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
