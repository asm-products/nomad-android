package nmd.nomad.api;

import java.util.List;

import nmd.nomad.models.Place;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by nicolasiensen on 1/28/15.
 */
public interface GomadClient {
    @GET("/places/search/1000/{lat}/{lng}.json")
    void searchPlaces(
            @Path("lat") String lat,
            @Path("lng") String lng,
            Callback<List<Place>> cb
    );
}
