package com.example.daumantas.explorer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daumantas on 2017-07-07.
 */

public class SearchPlacesFragment extends Fragment {


    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://explorer.we2host.lt/FetchPlaces.php";
    private  static final String uploadsUrl = "http://explorer.we2host.lt/uploads/";
    private ProgressDialog pDialog;
    private List<Place> placesList = new ArrayList<Place>();
    private ListView listView;
    private CustomListAdapter adapter;
    Activity mActivity;
    View globalView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();
        globalView = view;


        pDialog = new ProgressDialog(mActivity);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        //TEST req

        if (placesList.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("mytag", response);

                        //JSONObject jsonObject = new JSONObject(response);
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Place place = new Place();

                            //configure item
                            place.setTitle(obj.getString("title"));
                            place.setThumbnailUrl(uploadsUrl + obj.getString("id") + "/1.jpg");
                            place.setImageFolder(uploadsUrl + obj.getString("id"));
                            place.setDescription(obj.getString("description"));
                            place.setHint1(obj.getString("hint1"));
                            place.setHint2(obj.getString("hint2"));
                            place.setHint3(obj.getString("hint3"));
                            place.setId(obj.getString("id"));
                            place.setLat(obj.getString("lat"));
                            place.setLng(obj.getString("lng"));
                            place.setName(obj.getString("name"));
                            place.setRating(Double.valueOf(obj.getString("rating")));

                            // goodFor is json array
                            JSONArray goodForArray = new JSONArray(obj.getString("goodFor"));
                            ArrayList<String> goodFor = new ArrayList<String>();
                            for (int j = 0; j < goodForArray.length(); j++) {
                                goodFor.add((String) goodForArray.get(j));
                            }
                            place.setGoodFor(goodFor);

                            // adding movie to movies array
                            placesList.add(place);
                        }

                        adapter = new CustomListAdapter(mActivity, placesList);
                        listView = (ListView) globalView.findViewById(R.id.list);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        setItemListener(listView, placesList);
                        hidePDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(LoginActivity.this, "Error",Toast.LENGTH_LONG).show();
                    VolleyLog.e("Error: ", error.getMessage());
                    Log.d("mytag", "error");
                    error.printStackTrace();
                    hidePDialog();

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Communication Error!", Toast.LENGTH_SHORT).show();
                        Log.d("mytag", "communication error test");

                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                        Log.d("mytag", "authentification");
                    } else if (error instanceof ServerError) {
                        Log.d("mytag", "server error");
                        Toast.makeText(getActivity().getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Log.d("mytag", "networkerrpor");
                        Toast.makeText(getActivity().getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Log.d("mytag", "parse error");
                        Toast.makeText(getActivity().getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            MySingleton.getInstance(this.getActivity()).addToRequestque(stringRequest); // checks if there is a queue, if there is, puts request to it
        }else{
            //create instance of customAdapter and assign it to a list view
            adapter = new CustomListAdapter(mActivity, placesList);
            listView = (ListView) globalView.findViewById(R.id.list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setItemListener(listView, placesList);
            hidePDialog();
        }


    }


    public void setItemListener(final ListView listview, final List<Place> placesList){
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                placesList.get(position); // here you will get the clicked item from
                //your placeslist and you can check by getting a title  by using this

                Bundle bundle = new Bundle();
                bundle.putString("title", placesList.get(position).getTitle());
                bundle.putString("description", placesList.get(position).getDescription());
                bundle.putString("goodFor", placesList.get(position).getGoodForString());
                bundle.putString("hint1", placesList.get(position).getHint1());
                bundle.putString("hint2", placesList.get(position).getHint2());
                bundle.putString("hint3", placesList.get(position).getHint3());
                bundle.putString("id", placesList.get(position).getId());
                bundle.putString("lat", placesList.get(position).getLat());
                bundle.putString("lng",placesList.get(position).getLng());
                bundle.putString("rating",String.valueOf(placesList.get(position).getRating()));
                bundle.putString("name",placesList.get(position).getName());
                bundle.putString("imageFolder",placesList.get(position).getImageFolder());

                ((MainActivity) getActivity()).changeFragment(PlaceInfoFragment.class, bundle);

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_places, container, false);




        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}
