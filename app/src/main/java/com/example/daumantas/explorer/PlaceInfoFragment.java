package com.example.daumantas.explorer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PlaceInfoFragment extends Fragment {

    String title, description, goodFor, hint1, hint2, hint3, lat, lng, rating, uploadedBy, itemid,
    imageFolder;

    Button btn_hint1, btn_hint2, btn_hint3, btn_go;
    TextView tv_hint1, tv_hint2, tv_hint3, tv_description, tv_title, tv_goodFor, tv_rating, tv_uploader;
    boolean hint1Open = false, hint2Open = false, hint3Open = false;
    ImageView view_image1, view_image2, view_image3;


    public PlaceInfoFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            title = args.getString("title");
            description = args.getString("description");
            goodFor = args.getString("goodFor");
            hint1 = args.getString("hint1");
            hint2 = args.getString("hint2");
            hint3 = args.getString("hint3");
            lat = args.getString("lat");
            lng = args.getString("lng");
            rating = args.getString("rating");
            uploadedBy = args.getString("name");
            itemid = args.getString("id");
            imageFolder = args.getString("imageFolder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_info, container, false);
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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();
        setTexts();
        showImages();
        assignHintButtonListeners();
        checkHints();

    }

    void checkHints(){
        if(hint1.trim().length() == 0){ btn_hint1.setVisibility(View.GONE);}
        if(hint2.trim().length() == 0){ btn_hint2.setVisibility(View.GONE);}
        if(hint3.trim().length() == 0){ btn_hint3.setVisibility(View.GONE);}

    }

    void assignHintButtonListeners(){
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lng",Double.valueOf(lng));
                bundle.putDouble("lat",Double.valueOf(lat));
                bundle.putString("hint1", hint1);
                bundle.putString("hint2", hint2);
                bundle.putString("hint3", hint3);
                bundle.putBoolean("hint1Open", hint1Open);
                bundle.putBoolean("hint2Open", hint2Open);
                bundle.putBoolean("hint3Open", hint3Open);
                ((MainActivity) getActivity()).changeFragment(ShowDirectionFragment.class, bundle);
            }
        });

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
                if(!hint1Open) {
                    fadeText(btn_hint3, hint3, 1000);
                    hint3Open = true;
                }
            }
        });
    }

    void setTexts(){
        tv_goodFor.setText(goodFor);
        tv_uploader.setText(getString(R.string.uploadedBy, uploadedBy));
        tv_rating.setText(rating);
        tv_title.setText(title);
        tv_description.setText(description);
    }

    void findViews(){
        btn_hint1 = (Button)getActivity().findViewById(R.id.btn_hint1);
        btn_hint2 = (Button)getActivity().findViewById(R.id.btn_hint2);
        btn_hint3 = (Button)getActivity().findViewById(R.id.btn_hint3);
        btn_go = (Button)getActivity().findViewById(R.id.btn_go);

        tv_hint1 = (TextView)getActivity().findViewById(R.id.hint1);
        tv_hint2 = (TextView)getActivity().findViewById(R.id.hint2);
        tv_hint3 = (TextView)getActivity().findViewById(R.id.hint3);

        tv_goodFor = (TextView)getActivity().findViewById(R.id.goodFor);
        tv_uploader = (TextView)getActivity().findViewById(R.id.uploader);
        tv_rating = (TextView)getActivity().findViewById(R.id.rating);
        tv_title = (TextView)getActivity().findViewById(R.id.title);
        tv_description = (TextView)getActivity().findViewById(R.id.description);

        view_image1 = (ImageView)getActivity().findViewById(R.id.image1);
        view_image2 = (ImageView)getActivity().findViewById(R.id.image2);
        view_image3 = (ImageView)getActivity().findViewById(R.id.image3);

    }

    void showImages(){

        Picasso.with(getActivity())
                .load(imageFolder + "/1.jpg")
                .into(view_image1, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {
                        view_image1.setVisibility(View.GONE);
                    }
                });

        Picasso.with(getActivity())
                .load(imageFolder + "/2.jpg")
                .into(view_image2, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {
                        view_image2.setVisibility(View.GONE);
                    }
                });

        Picasso.with(getActivity())
                .load(imageFolder + "/3.jpg")
                .into(view_image3, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {
                        view_image3.setVisibility(View.GONE);
                    }
                });

    }
}
