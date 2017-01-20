package sodevan.sarcar.Interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sodevan.sarcar.MapModels.MapResponse;

/**
 * Created by ronaksakhuja on 20/01/17.
 */

public interface MapApiInterface {
    @GET("/maps/api/geocode/json?")
    Call<MapResponse> getstreetdata(@Query("latlng") String latlng, @Query("key") String key);

}
