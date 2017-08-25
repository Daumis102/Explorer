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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("mytag", response);


                    JSONArray array = new JSONArray(response);
                    for(int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        Place place = new Place();
                        place.setTitle(obj.getString("title"));
                        place.setThumbnailUrl("http://explorer.we2host.lt/uploads/" + obj.getString("id") + "/1.jpg");
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

                    //create instance of customAdapter and assign it to a list view
                    adapter = new CustomListAdapter(mActivity, placesList);
                    listView = (ListView) globalView.findViewById(R.id.list);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    hidePDialog();

                } catch (JSONException e){
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
                    Log.d("mytag","communication error test");

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getActivity().getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                    Log.d("mytag","authentification");
                } else if (error instanceof ServerError) {
                    Log.d("mytag","server error");
                    Toast.makeText(getActivity().getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Log.d("mytag","networkerrpor");
                    Toast.makeText(getActivity().getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Log.d("mytag","parse error");
                    Toast.makeText(getActivity().getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MySingleton.getInstance(this.getActivity()).addToRequestque(stringRequest); // checks if there is a queue, if there is, puts request to it

                //////
/*

                // Creating volley request obj
                JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Place place = new Place();
                                Log.d(TAG, obj.getString("title"));
                                place.setTitle(obj.getString("title"));
                                place.setThumbnailUrl("http://explorer.we2host.lt/uploads/" + obj.getString("id") + "/1.jpg");
                                place.setRating(Double.parseDouble(obj.getString("rating")));

                                // goodFor is json array
                                JSONArray goodForArray = obj.getJSONArray("goodFor");
                                ArrayList<String> goodFor = new ArrayList<String>();
                                for (int j = 0; j < goodForArray.length(); j++) {
                                    goodFor.add((String) goodForArray.get(j));
                                }
                                place.setGoodFor(goodFor);

                                // adding movie to movies array
                                placesList.add(place);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        MySingleton.getInstance().addToRequestque(movieReq);

*/
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
