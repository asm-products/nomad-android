package nmd.nomad.api;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by nicolasiensen on 1/28/15.
 */
public class ServiceGenerator {
    private static Object service;

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        if(service == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(baseUrl)
                    .setClient(new OkClient(new OkHttpClient()));
            RestAdapter adapter = builder.build();
            service = adapter.create(serviceClass);
        }

        return (S) service;
    }
}
