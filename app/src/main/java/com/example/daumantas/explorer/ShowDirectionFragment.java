package com.example.daumantas.explorer;


import android.Manifest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.content.Context.SENSOR_SERVICE;

@RuntimePermissions
public class ShowDirectionFragment extends Fragment implements SensorEventListener {

    double  spotLat, spotLng;
    Float azimut;
    String hint1, hint2, hint3;
    Float degree, prevDirection = 0f;
    boolean degreeAvailable = false, allAnimationsEnded = true, hint1Open, hint2Open, hint3Open;
    ImageView arrow;
    TextView tv_distance, tv_hint1, tv_hint2, tv_hint3;
    Button btn_hint1, btn_hint2, btn_hint3;
    long lastUpdate = 0;
    public static  final float TWENTY_FIVE_DEGREE_IN_RADIAN = 0.436332313f;
    public static final float ONE_FIFTY_FIVE_DEGRE_IN_RADIAN = 2.7052603f;
    public static final int animTime = 800;
    Location spotLocation;

    public ShowDirectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle args = getArguments();
            spotLat = args.getDouble("lat");
            spotLng = args.getDouble("lng");
            hint1 = args.getString("hint1");
            hint2 = args.getString("hint2");
            hint3 = args.getString("hint3");
            hint1Open = args.getBoolean("hint1Open");
            hint2Open = args.getBoolean("hint2Open");
            hint3Open = args.getBoolean("hint3Open");
            //Create Location object from received spot coordinates
            spotLocation = new Location(LocationManager.NETWORK_PROVIDER);
            spotLocation.setLatitude(spotLat);
            spotLocation.setLongitude(spotLng);
        }

        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_direction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findLayoutItems();
        startLocationUpdates();
        checkHints();
        setButtonListeners();

    }

    void findLayoutItems(){
        arrow = (ImageView)getActivity().findViewById(R.id.arrow);
        tv_distance = (TextView)getActivity().findViewById(R.id.distance);
        tv_hint1 = (TextView)getActivity().findViewById(R.id.hint1);
        tv_hint2 = (TextView)getActivity().findViewById(R.id.hint2);
        tv_hint3 = (TextView)getActivity().findViewById(R.id.hint3);
        btn_hint1 = (Button)getActivity().findViewById(R.id.btn_hint1);
        btn_hint2 = (Button)getActivity().findViewById(R.id.btn_hint2);
        btn_hint3 = (Button)getActivity().findViewById(R.id.btn_hint3);
    }

    void checkHints(){
        //if there is no hint or the hint is unlocked - delete the button
        if(hint1.length() == 0 || hint1Open){
            btn_hint1.setVisibility(View.GONE);
        }
        if(hint2.length() == 0 || hint2Open){
            btn_hint2.setVisibility(View.GONE);
        }
        if(hint3.length() == 0 || hint3Open){
            btn_hint3.setVisibility(View.GONE);
        }
        //if the hint is unlocked - show it if not - hide textView
        if(hint1Open){
            tv_hint1.setText(hint1);
        }else{
            tv_hint1.setVisibility(View.GONE);
        }
        if(hint2Open){
            tv_hint2.setText(hint2);
        }else{
            tv_hint2.setVisibility(View.GONE);
        }
        if(hint3Open){
            tv_hint3.setText(hint3);
        }else{
            tv_hint3.setVisibility(View.GONE);
        }
    }

    void setButtonListeners(){
        //set hint buttons listeners so they disappear when clicked and reveal hint
        btn_hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hint1Open) {
                    fadeText(btn_hint1, hint1, 1000);
                    hint1Open = true;
                }

            }
        });

        btn_hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hint2Open) {
                    fadeText(btn_hint2, hint2, 1000);
                    hint2Open = true;
                }

            }
        });

        btn_hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hint3Open) {
                    fadeText(btn_hint3, hint3, 1000);
                    hint3Open = true;
                }
            }
        });
    }

    @Override
    public void onStop() {
        SmartLocation.with(getActivity()).location().stop();
        super.onStop();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void startLocationUpdates(){


        long mLocTrackingInterval = 1000 * 1; // 1 sec
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(getActivity())
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {

                        if(degreeAvailable){
                            float bearing = getBearing(location); // direcion from current location to destination
                            float direction = getTrueDirection(fixDirection(bearing)); // direction to turn arrow
                            float angle360 = fixDirection(direction); // true direction in 0-360

                            if(allAnimationsEnded){
                                rotateImage(angle360);
                            }


                        }

                    }
                });
    }

    void AnimateRotation2(float oldDir, float newDir,int animTime){
        Log.d("mytag", "from "+String.valueOf(oldDir)+" to " + String.valueOf(newDir));
        final RotateAnimation rotateAnim = new RotateAnimation(oldDir, newDir, arrow.getDrawable().getBounds().width()/2, arrow.getDrawable().getBounds().height()/2);
        rotateAnim.setDuration(animTime);
        rotateAnim.setFillAfter(true);
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                allAnimationsEnded = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                allAnimationsEnded = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrow.startAnimation(rotateAnim);
    }
     void RotateClockwise(float oldDir, float newDir, int animTime){

         final RotateAnimation secondRot = new RotateAnimation(0, newDir, arrow.getDrawable().getBounds().width()/2, arrow.getDrawable().getBounds().height()/2);
         secondRot.setDuration(animTime);
         secondRot.setFillAfter(true);
         secondRot.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 allAnimationsEnded  = true;
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });

         final RotateAnimation firstRot = new RotateAnimation(oldDir, 359, arrow.getDrawable().getBounds().width()/2, arrow.getDrawable().getBounds().height()/2);
         firstRot.setDuration(animTime);
         firstRot.setFillAfter(true);
         firstRot.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {
                allAnimationsEnded = false;
             }

             @Override
             public void onAnimationEnd(Animation animation) {
                arrow.setAnimation(secondRot);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
         arrow.startAnimation(firstRot);
     }

    void RotateAntiClockwise(float oldDir, float newDir, int animTime){

        final RotateAnimation secondRot = new RotateAnimation(oldDir, 0, arrow.getDrawable().getBounds().width()/2, arrow.getDrawable().getBounds().height()/2);
        secondRot.setDuration(animTime);
        secondRot.setFillAfter(true);
        secondRot.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                allAnimationsEnded = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final RotateAnimation firstRot = new RotateAnimation(359, newDir, arrow.getDrawable().getBounds().width()/2, arrow.getDrawable().getBounds().height()/2);
        firstRot.setDuration(animTime);
        firstRot.setFillAfter(true);
        firstRot.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                allAnimationsEnded = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                arrow.setAnimation(secondRot);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrow.startAnimation(firstRot);
    }

    boolean goesThroughClockwise(float direction){
        return (360 - prevDirection + direction < 180);
    }

    boolean goesThroughAntiClockwise(float direction){
        return (360 - direction + prevDirection < 180);
    }

    void rotateImage(float direction){

        if(Math.abs(direction - prevDirection) >= 180 && direction < prevDirection && goesThroughClockwise(direction)) // clockwise through 360/0
        {
            RotateClockwise(prevDirection, direction, animTime);

        }else if(Math.abs(direction - prevDirection) >= 180 && direction > prevDirection && goesThroughAntiClockwise(direction)){ //anticlockwise through 360/0
            RotateAntiClockwise(prevDirection, direction, animTime);
        }else{
            AnimateRotation2(prevDirection, direction,animTime);
            Log.d("mytag", "other");
        }
        prevDirection = direction;
    }

    float fixDirection(float angle){

        //Log.d("mytag", "angle before: " + String.valueOf(angle));
        return (angle %= 360) >= 0 ? angle : (angle + 360);
    }


    float getTrueDirection(float bearing){
        return bearing - degree;

    }


    float getBearing(Location location){
        //Get bearing to chosen location//
        return location.bearingTo(spotLocation);
    }


    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;


    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        startLocationUpdates();
    }

    public void onPause() {
        super.onPause();
        //degreeAvailable = false;
        mSensorManager.unregisterListener(this);
    }

    void fadeText(final Button button, final String newText, int animTime){
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(animTime);

        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(animTime);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button.setText(newText);
                button.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                button.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        button.startAnimation(out);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

        float[] mGravity;
        float[] mGeomagnetic;

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float rotationMatrix[] = new float[9];
                float I[] = new float[9];
                float[] remapedCoords = new float[16];
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];

                    //to make compass more accurate we need to check if the device is flat
                    float inclination = (float)Math.acos(rotationMatrix[8]);
                    if(inclination < TWENTY_FIVE_DEGREE_IN_RADIAN||inclination > ONE_FIFTY_FIVE_DEGRE_IN_RADIAN){
                        //device is flat
                        SensorManager.getOrientation(rotationMatrix, orientation); // computes orientation from rotationMatrix
                    }else{
                        // call remap
                        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, remapedCoords);
                        SensorManager.getOrientation(remapedCoords, orientation); // computes orientation from remapedCoords
                    }

                    SensorManager.getOrientation(rotationMatrix, orientation);
                    azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                    degree = (float)(Math.toDegrees(azimut)+360)%360;
                    degreeAvailable = true;

                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastUpdate) > 500) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;
                    }


                }
            }
        }

}

