package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.Manifest;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsListFragment.OnArticleItemClickedListener, NewsListFragment.OnShareButtonClickedListener, NewsListFragment.OnFavoriteButtonClickedListener, ArticleDetailsFragment.OnUpdateList,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close;
    private FloatingActionButton fab,fab1,fab2,fab3;
//Geofence variables
protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    private boolean hasTwoPanes;
    private static String urlString;
    private NewsModel model = new NewsModel(this);
    private boolean isFavoritesDispayed = false;
    Menu mainmenu;
    android.widget.SearchView mysearch;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private boolean geofence_enable = false;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         fab = (FloatingActionButton) findViewById(R.id.fab);
         fab1 = (FloatingActionButton) findViewById(R.id.fab2);
         fab2 = (FloatingActionButton) findViewById(R.id.fab3);
         fab3 = (FloatingActionButton) findViewById(R.id.fab4);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
//
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                      //  .setAction("Action", null).show();
               animateFAB();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/edakent/"));
                startActivity(browserIntent);

            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/edakent/"));
                startActivity(browserIntent);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/groups/3683440/profile"));
                startActivity(browserIntent);
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        model = NewsModel.getInstance();

        if (findViewById(R.id.details_fragment) ==  null){

            hasTwoPanes = false;

        }else{
            hasTwoPanes = true;
        }


        mSharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Set<String> set = mSharedPreferences.getStringSet("eda_history", null);
        if (set!= null){
        ArrayList<String> tmp_search=new ArrayList<String>(set);

        model.setSearch_historyList(tmp_search);
        }


        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);


        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mSharedPreferences.getBoolean("firsRun",true)){
            final SharedPreferences.Editor editor = mSharedPreferences.edit();
        new AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setMessage("Do you want to enable the news notifications?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        editor.putBoolean("firsRun",false);
                        editor.putBoolean("notifications_new_message", true);
                        geofence_enable = true;
                        editor.commit();

                        addGeofencesButtonHandler();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        editor.putBoolean("firsRun",false);
                        editor.putBoolean("notifications_new_message", false);
                        geofence_enable = false;
                        editor.commit();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        }
        geofence_enable = mSharedPreferences.getBoolean("notifications_new_message",false);
        //load favorites to favorites array
        try {
            model.opendb_read();
            model.reload_favorites();

            model.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }



    }

    public void animateFAB(){

        if(isFabOpen){

            fab.setImageResource(R.drawable.social_media);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;


        } else {

            //fab.startAnimation(rotate_forward);
            fab.setImageResource(R.drawable.social_collapse);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;


        }
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mainmenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);

                fragment.SearchMode(false);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
                fragment.SearchSubmit(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
                fragment.SearchSubmit(s);

                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings= new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }else if (id == R.id.favorites) {
            //check if the favourites list is displayed in the recycler view
            if (isFavoritesDispayed == false) {
                item.setIcon(R.drawable.home_btn_toolbar);
                isFavoritesDispayed = true;

                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
                fragment.displayfavoriteList();
            }else{
                isFavoritesDispayed = false;
                item.setIcon(R.drawable.heart_full_toolbar);
                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
                fragment.displayOriginalList();

            }



        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            if (isFavoritesDispayed == true) {
                mainmenu.getItem(1).setIcon(R.drawable.heart_full_toolbar);

                Log.i("menu", "clicked");
                isFavoritesDispayed = false;

                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
                fragment.displayOriginalList();
            }
        } else if (id == R.id.menu_favorites) {
            if (isFavoritesDispayed == false) {
                mainmenu.getItem(1).setIcon(R.drawable.home_btn_toolbar);

                Log.i("menu", "clicked");
                isFavoritesDispayed = true;

                FragmentManager fragmentManager = getFragmentManager();
                NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
                fragment.displayfavoriteList();
            }

        } else if (id == R.id.map) {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        } else if (id == R.id.about) {
            Intent aboutIntent =  new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }else if (id == R.id.eda_search) {
            //Intent searchIntent = new Intent(this, SearchHistory.class);
            //startActivity(searchIntent);
            mainmenu.getItem(1).setIcon(R.drawable.heart_full_toolbar);
            isFavoritesDispayed = false;
            displaySearchDialog();

        } else if (id == R.id.telephones) {
            Intent telephonesIntent =  new Intent(this, TelephonesActivity.class);
            startActivity(telephonesIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySearchDialog() {

        FragmentManager fragmentManager = getFragmentManager();
        final NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
        //create an alert dialog to let the user type the keyword for the search in eda website
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.activity_search_history, null);
        alertDialog.setView(convertView);

        alertDialog.setTitle("Search for an article in Eda Website");
        final AlertDialog show = alertDialog.show();
        ListView mylist = (ListView)convertView.findViewById(R.id.SearchlistView);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tmp_query = model.getSearch_historyList().get(position);
                model.SearchData(tmp_query);

                fragment.progress.show();
                show.dismiss();

            }
        });
       mysearch = (android.widget.SearchView) convertView.findViewById(R.id.searchEdaView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                model.getSearch_historyList() );

        mylist.setAdapter(arrayAdapter);



        mysearch.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("Search", query);
                model.getSearch_historyList().add(query);
                arrayAdapter.notifyDataSetChanged();
                mysearch.clearFocus();
                //save to preferences the history

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                Set<String> set = new HashSet<String>();
                set.addAll(model.getSearch_historyList());
                editor.putStringSet("eda_history", set);
                editor.commit();
                fragment.progress.show();

                 model.SearchData(query);
                show.dismiss();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    @Override
    public void onArticleItemClicked(int position) {
        // if the item was selected check if the main content has two or one fragments.(smartphone/tablet version)
        if (hasTwoPanes){

            FragmentManager fragmentManager = getFragmentManager();
            ArticleDetailsFragment fragment = (ArticleDetailsFragment) fragmentManager.findFragmentById(R.id.details_fragment);
            fragment.updateDetails(position);

        }else{
            Intent intent = new Intent(this, ArticleDetailsActivity.class);
            intent.putExtra("ITEM_ID", position);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }






    @Override
    public void onShareButtonClickedListener(int position) {
        //share action
        Intent share = new Intent(Intent.ACTION_SEND);
        String shareBody = "http://www.eda.kent.ac.uk/school/news_article.aspx?aid="+Integer.toString(model.getNewsList().get(position).getRecord_id());
        share.setType("text/plain"); // might be text, sound, whatever
        share.putExtra(Intent.EXTRA_SUBJECT, "Subject here...");
        share.putExtra(Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(share, "share"));
    }

    @Override
    public void onFavoriteButtonClickedListener(int position, boolean isFavorite) {
        if (isFavorite == false) {
            NewsItem tmp = model.getNewsList().get(position);
            String tmp_url = "http://www.eda.kent.ac.uk/school/news_article.aspx?aid=" + Integer.toString(tmp.getRecord_id());
            model.getFavoritesList().add(tmp);
            try {
                model.open();
                model.addNews(true, tmp.getRecord_id(), tmp.getTitle(), tmp.getShort_description(),"", tmp.getDate(),tmp.getImage_url(), tmp_url);
                model.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // System.out.println(model.getFavoritesList());

        } else {


            model.delete_from_favorites(model.getNewsList().get(position).getRecord_id());
            model.getFavoritesList().remove(model.getNewsList().get(position));
            System.out.println("The size of favorites is : " + model.getFavoritesList().size());

            if (isFavoritesDispayed){
            FragmentManager fragmentManager = getFragmentManager();
            NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
            fragment.displayfavoriteList();}

        }
    }


    @Override
    public void onUpdateList(boolean update) {
        Log.i("OK", "attached");
        FragmentManager fragmentManager = getFragmentManager();
        NewsListFragment fragment = (NewsListFragment) fragmentManager.findFragmentById(R.id.list_fragment);
        fragment.UpdateList();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        //Give permission to user to add ACCESS_FINE_LOCATION
        int hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
            return;
        }
        boolean notifications = mSharedPreferences.getBoolean("notifications_new_message",false);

        if (geofence_enable != notifications){
            if (notifications == true){
                addGeofencesButtonHandler();
                geofence_enable = notifications;

            }else{
                removeGeofencesButtonHandler();
                geofence_enable = notifications;
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            //setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(geofence_enable ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    public void removeGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }
        System.out.println(mGeofenceList);
    }



}
