package sodevan.sarcar.Interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sodevan.sarcar.PlacesModels.PlacesResponse;

/**
 * Created by kartiksharma on 21/01/17.
 */

public interface PlacesAPIInterface {
    @GET("/maps/api/place/nearbysearch/json?")
    Call<PlacesResponse> nearbypetrolpumps(@Query("location") String location,@Query("radius") String radius,@Query("type") String type, @Query("key") String key);
}
