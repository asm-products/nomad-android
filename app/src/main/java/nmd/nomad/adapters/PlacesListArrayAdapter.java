package nmd.nomad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import nmd.nomad.R;
import nmd.nomad.models.Place;

/**
 * Created by nicolasiensen on 1/30/15.
 */
public class PlacesListArrayAdapter extends ArrayAdapter<Place> {
    private final Context context;
    private final List<Place> places;

    public PlacesListArrayAdapter(Context context, List<Place> places) {
        super(context, R.layout.places_list_row, places);
        this.context = context;
        this.places = places;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.places_list_row, parent, false);
        TextView placeNameTextView = (TextView) rowView.findViewById(R.id.place_name);
        TextView placeDistanceTextView = (TextView) rowView.findViewById(R.id.place_distance);
        Place place = this.places.get(position);

        placeNameTextView.setText(place.getName());
        placeDistanceTextView.setText(place.getDistance().intValue() + "m");

        return rowView;
    }
}
