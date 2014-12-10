package nmd.nomad;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;
import se.walkercrou.places.Prediction;

public class GooglePlacesApiActivity extends ActionBarActivity {

    private GooglePlaces googlePlacesClient;
    private Subscription automaticSearchingSubscription;
    private PublishSubject<Observable<String>> searchTextEmitterSubject;

    @InjectView(R.id.list)
    RecyclerView list;

    @InjectView(R.id.progress_layout)
    View progressLayout;

    @InjectView(R.id.places_search)
    EditText placesSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_places_api);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO inject with Dagger
        googlePlacesClient = new GooglePlaces(getString(R.string.google_places_api_key));

        setupList();
        setupAutomaticSearching();

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            startSearchByLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSearchByLocation() {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        locationProvider.getLastKnownLocation()
                .subscribe(location -> findNearbyPlaces(location),
                        throwable -> Toast.makeText(GooglePlacesApiActivity.this,
                                "No location: " + throwable, Toast.LENGTH_SHORT).show());
    }

    private void setupAutomaticSearching() {
        // Use RXJava debounce to avoid calling API until user stops typing
        searchTextEmitterSubject = PublishSubject.create();
        automaticSearchingSubscription = AndroidObservable.bindActivity(GooglePlacesApiActivity.this,
                Observable.switchOnNext(searchTextEmitterSubject))
                .debounce(400, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> getPlacePredictions(s));

        placesSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchTextEmitterSubject.onNext(getASearchObservableFor(s.toString()));
            }
        });
    }

    private void setupList() {
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getPlacePredictions(String searchQuery) {
        showProgress();
        Observable<List<Prediction>> observable = Observable.create((Observable
                .OnSubscribe<List<Prediction>>) subscriber -> {
            List<Prediction> predictions = googlePlacesClient.getPlacePredictions(searchQuery);
            if (predictions != null & predictions.size() != 0) {
                subscriber.onNext(predictions);
            } else {
                subscriber.onError(new Throwable());
            }
            subscriber.onCompleted();
        });
        AndroidObservable.bindActivity(this, observable)
                .subscribeOn(Schedulers.io())
                .flatMap(s -> Observable.from(s))
                .map(prediction -> prediction.getDescription())
                .toList()
                .subscribe(names -> showNamesInList(names),
                        throwable -> {
                            ArrayList<String> noResultsList = new ArrayList<>();
                            noResultsList.add("No results");
                            showNamesInList(noResultsList);
                            hideProgress();
                        });
    }

    private void findNearbyPlaces(Location location) {
        Observable<List<Place>> observable = Observable.create((Observable
                .OnSubscribe<List<Place>>) subscriber -> {
            List<Place> places = googlePlacesClient.getNearbyPlaces(location.getLatitude(),
                    location.getLongitude(), GooglePlaces.MAXIMUM_RESULTS);
            if (places != null & places.size() != 0) {
                subscriber.onNext(places);
            } else {
                subscriber.onError(new Throwable());
            }
            subscriber.onCompleted();
        });
        AndroidObservable.bindActivity(this, observable)
                .subscribeOn(Schedulers.io())
                .flatMap(s -> Observable.from(s))
                .map(place -> place.getName())
                .toList()
                .subscribe(names -> {
                            names.add(0, "Nearest places:");
                            showNamesInList(names);
                        },
                        throwable -> {
                            ArrayList<String> noResultsList = new ArrayList<>();
                            noResultsList.add("No results");
                            showNamesInList(noResultsList);
                        });
    }

    private void showProgress() {
        progressLayout.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressLayout.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    private void showNamesInList(List<String> names) {
        list.setAdapter(new NamesAdapter(names));
        hideProgress();
    }

    public static class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.ViewHolder> {

        private List<String> names;

        public NamesAdapter(List<String> names) {
            this.names = names;
        }

        @Override
        public NamesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_place, parent, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.title.setText(names.get(i));
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            TextView title;

            public ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (automaticSearchingSubscription != null) {
            automaticSearchingSubscription.unsubscribe();
        }
    }

    /**
     * @param searchText search text entered onTextChange
     * @return a new observable which searches for text searchText,
     * explicitly say you want subscription to be done on a a non-UI thread,
     * otherwise it'll default to the main thread.
     */
    private Observable<String> getASearchObservableFor(final String searchText) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {

                Log.d("RX", "----------- inside the search observable");
                subscriber.onNext(searchText);
                // subscriber.onCompleted(); This seems to have no effect.
            }
        }).subscribeOn(Schedulers.io());
    }
}
