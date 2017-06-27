package com.example.daumantas.explorer;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.content.ContentValues.TAG;

/**
 * Created by Daumantas on 2017-05-25.
 */
@RuntimePermissions
public class AddPlacesFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mLastKnownLocation = null;
    private CameraPosition mCameraPosition = null;
    private LatLng mDefaultLocation = new LatLng(40.76793169992044,
            -73.98180484771729);
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private float DEFAULT_ZOOM = 10;
    private Marker newPlace;
    private LatLng previousMarkerLocation;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mGoogleApiClient == null)
        {
            buildGoogleApi();
        }

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        return inflater.inflate(R.layout.add_places_fragment, container, false);
    }

    void buildGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),
                        this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mContext = getContext();
        Button nextBtn = (Button) getActivity().findViewById(R.id.mapNext);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPlace!=null){
                    ((MainActivity) getActivity()).handleMap(newPlace.getPosition());
                }else{
                    Toast.makeText(mContext, "Tap on map or drag marker to select location", Toast.LENGTH_SHORT).show();
                }

            }
        });



        android.support.v4.app.FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mf = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mf == null) {
            mf = new SupportMapFragment();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.map, mf);
            ft.commit();
            fm.executePendingTransactions();
        }
        mf.getMapAsync(this);
        if(savedInstanceState!=null){
            previousMarkerLocation = new LatLng(savedInstanceState.getDouble("markerLat"),savedInstanceState.getDouble("markerLng"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    public void updateLocation() {
        AddPlacesFragmentPermissionsDispatcher.getDeviceLocationWithCheck(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            if(newPlace!=null){
                outState.putDouble("markerLng",newPlace.getPosition().longitude);
                outState.putDouble("markerLat",newPlace.getPosition().latitude);
            }
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {


        mMap = map;
        AddPlacesFragmentPermissionsDispatcher.getDeviceLocationWithCheck(this);
        // Do other setup activities here too, as described elsewhere in this tutorial.

        //AddPlacesFragmentPermissionsDispatcher.updateLocationUIWithCheck(this); //UpdateLocationUI

        if(previousMarkerLocation!=null){
            addMarker(previousMarkerLocation);
        }
        else if (mLastKnownLocation != null) {
            addMarker(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
            }
        });

    }

    public void addMarker(LatLng position) {
        mMap.clear();
        newPlace = mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(true)
                .title("Spot location"));
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("CONNECTION CALLBACK", "failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("CONNECTION CALLBACK", "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("CONNECTION CALLBACK", "suspended");
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void updateLocationUI() {

        if (mMap == null) {
            Log.d("LOCATION", "MAPS ARE NULL");
        } else {
            Log.d("LOCATION", "updateLocationUI");
            //noinspection MissingPermission

            mMap.setMyLocationEnabled(true);
            mMap.setPadding(0, 150, 0, 0);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddPlacesFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void updateLocationUIOnShowRationale(final PermissionRequest request) {
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void updateLocationUIOnPermissionDenied() {

        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void updateLocationUIOnNeverAskAgain() {
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getDeviceLocation() {

        mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}