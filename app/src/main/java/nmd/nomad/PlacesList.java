package nmd.nomad;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import nmd.nomad.R;
import nmd.nomad.api.GomadClient;
import nmd.nomad.api.ServiceGenerator;
import nmd.nomad.models.Place;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlacesList extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        // Set the listView and the adapter
        ListView listView = (ListView) findViewById(R.id.placesListView);
        final ArrayList<Place> placeList = new ArrayList<Place>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, placeList);
        listView.setAdapter(arrayAdapter);

        // Set a callback to fill the listView
        final Callback callback = new Callback() {
            @Override
            public void success(Object object, Response response) {
                arrayAdapter.clear();
                arrayAdapter.addAll((ArrayList<Place>) object);
                for (Place place : placeList) {
                    System.out.println(place.toString());
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                System.out.println(retrofitError.getBody());
            }
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                System.out.println(location.getLatitude() + ", " + location.getLongitude());
                String API_URL = "https://asm-nomad-staging.herokuapp.com/api/v1/";
                GomadClient client = ServiceGenerator.createService(GomadClient.class, API_URL);
                client.searchPlaces(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), callback);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places_list, menu);
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
}
