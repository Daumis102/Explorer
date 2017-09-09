package com.example.daumantas.explorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean Auth;
    Welcome welcome;
    Fragment fragment;
    String name = "";
    FragmentManager fragmentManager;
    private static final String TAG_MY_FRAGMENT = "currentFragment";
    LatLng locationToAdd;

    private String UPLOAD_URL ="http://explorer.we2host.lt/MultipartUpload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_MY_FRAGMENT);
            name = savedInstanceState.getString("name");
            Auth = savedInstanceState.getBoolean("auth");
            Log.d("mytag","SavedInstance != null");
            locationToAdd = (new LatLng(savedInstanceState.getDouble("lat"),savedInstanceState.getDouble("lng")));
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
            }else{
                finish();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.d("mytag", "Saving...");
        outState.putString("name", name);
        outState.putBoolean("auth", Auth);
        if(locationToAdd!=null){
            outState.putDouble("lat", locationToAdd.latitude);
            outState.putDouble("lng", locationToAdd.longitude);
        }
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

            int count = getSupportFragmentManager().getBackStackEntryCount();
            if(count==0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Leave?");
                builder.setMessage("Would you like to end your adventure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }else{
                super.onBackPressed();
            }

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
                .addToBackStack(null)
                .commit();
    }

    public void changeFragment(Class fragClass, Bundle bundle)
    {

        try {
            fragment = (Fragment) fragClass.newInstance();
            fragment.setArguments(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, TAG_MY_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    void handleMap(LatLng markerPos)
    {
        locationToAdd = markerPos;
        changeFragment(AddDescriptionFragment.class);
    }
    void handleDescription(final String title, final String description, final String goodFor,
                           final String hint1, final String hint2, final String hint3,
                           final Drawable image1, final Drawable image2, final Drawable image3){



        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        loading.setCanceledOnTouchOutside(false);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    loading.dismiss();
                    String resultResponse = new String(response.data);
                    Log.d("mytag",resultResponse);
                    try {
                        JSONObject result = new JSONObject(resultResponse);

                        String status = result.getString("code");

                        if (status.equals("success")) {
                            Toast.makeText(MainActivity.this, "Spot has been sent for managers to review, thanks!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error has occured...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Unknown error";
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    } else {
                        String result = new String(networkResponse.data);
                        try {

                            JSONObject response = new JSONObject(result);
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Log.e("Error Status", status);
                            Log.e("Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message+" Please login again";
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message+ " Check your inputs";
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message+" Something is getting wrong";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("Error", errorMessage);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", title);
                    params.put("description", description);
                    params.put("goodFor", goodFor);

                    if(!name.equals("")){
                        params.put("name", name);
                    }


                    if(!hint1.equals("")){
                        params.put("hint1",hint1);
                    }
                    if(!hint2.equals("")){
                        params.put("hint2",hint2);
                    }
                    if(!hint3.equals("")){
                        params.put("hint3",hint3);
                    }
                    params.put("lat",Double.valueOf(locationToAdd.latitude).toString());
                    params.put("lng",Double.valueOf(locationToAdd.longitude).toString());

                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    params.put("image1", new DataPart("image1.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), image1), "image/jpeg"));
                    if(image2!=null){
                        params.put("image2", new DataPart("image2.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), image2), "image/jpeg"));
                    }
                    if(image3!=null){
                        params.put("image3", new DataPart("image3.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), image3), "image/jpeg"));
                    }
                    return params;
                }
            };

        MySingleton.getInstance(getBaseContext()).addToRequestque(multipartRequest);


        }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("stringImage",encodedImage);
        return encodedImage;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_addPlace) {
                changeFragment(AddPlacesFragment.class);

        } else if (id == R.id.nav_searchPlaces) {
            //fragmentClass = Places.class;
            changeFragment(SearchPlacesFragment.class);
        }

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
