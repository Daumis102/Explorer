package com.example.daumantas.explorer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Place> places;
    ImageLoader imageLoader = MySingleton.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Place> places) {
        this.activity = activity;
        this.places = places;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int location) {
        return places.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = MySingleton.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView goodFor = (TextView) convertView.findViewById(R.id.goodFor);

        // getting movie data for the row
        Place p = places.get(position);

        // thumbnail image
        thumbNail.setImageUrl(p.getThumbnailUrl(), imageLoader);

        // title
        title.setText(p.getTitle());

        // rating
        rating.setText("Rating: " + String.valueOf(p.getRating()));

        // goodFor
        String goodForStr = "";
        for (String str : p.getGoodFor()) {
            goodForStr += str + ", ";
        }
        goodForStr = goodForStr.length() > 0 ? goodForStr.substring(0,
                goodForStr.length() - 2) : goodForStr;
        goodFor.setText(goodForStr);

        return convertView;
    }

}
