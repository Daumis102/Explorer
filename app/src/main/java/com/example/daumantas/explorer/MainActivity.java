package com.example.daumantas.explorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean Auth;
    Welcome welcome;
    Fragment fragment;
    String name;
    FragmentManager fragmentManager;
    private static final String TAG_MY_FRAGMENT = "currentFragment";
    LatLng locationToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Realm.init(this); //initialize other plugins

        if (savedInstanceState != null){
            fragment = (Fragment) getSupportFragmentManager().findFragmentByTag(TAG_MY_FRAGMENT);
            name = savedInstanceState.getString("name");
            Auth = savedInstanceState.getBoolean("auth");
            Log.d("mytag","SavedInstance != null");
        }else{
            Log.d("mytag","savedInstance == null");
            fragment = null;
            Auth = false;
        }


        if (Auth == false) {
            Log.d("mytag", "Auth is false, call showLogin()");
            showlogin();
        }
        //Nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //login activity
        if (requestCode == 2) { //result->request
            if (data != null) {
                Log.d("mytag","OnActivityResult");
                name = data.getStringExtra("name");
                Auth = true;
                Log.d("mytag",Boolean.toString(Auth));
                Bundle bundle = new Bundle();
                bundle.putString("name", name);

                fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                welcome = new Welcome();
                welcome.setArguments(bundle);
                fragmentTransaction.add(R.id.fragment_container, welcome);
                fragmentTransaction.commit();


            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.d("mytag", "Saving...");
        outState.putString("name", name);
        outState.putBoolean("auth", Auth);
        super.onSaveInstanceState(outState);
    }

    void showlogin() {
        Log.d("mytag","showLogin()");
        Intent intent = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(Class fragClass)
    {

        try {
            fragment = (Fragment) fragClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, TAG_MY_FRAGMENT)
                .commit();
    }

    void handleMap(LatLng markerPos)
    {
        locationToAdd = markerPos;
        changeFragment(AddDescriptionFragment.class);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_addPlace) {
                changeFragment(AddPlacesFragment.class);

        } else if (id == R.id.nav_gallery) {
            //fragmentClass = Places.class;
            changeFragment(Places.class);
        }

        /*try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        */

        // Highlight the selected item has been done by NavigationView
        //item.setChecked(true);
        // Set action bar title
        //setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void showDefaultScreen(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, welcome);
        fragmentTransaction.commit();
    }
}
