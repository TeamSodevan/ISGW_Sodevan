package sodevan.sarcar.PlacesModels;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sodevan.sarcar.Interfaces.PlacesAPIInterface;

/**
 * Created by kartiksharma on 21/01/17.
 */

public class Places {
    String lat,longit;

    public Places(String lat, String longit){

        this.lat=lat;
        this.longit=longit;


    }


    public Call<PlacesResponse> getKey(){
        String API_KEY ="AIzaSyBDxQTlfBu83UepeJyZLgPTkZeJwlg-Q7w";
        String type="gas_station";


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesAPIInterface api= retrofit.create(PlacesAPIInterface.class);
        Call<PlacesResponse> call=api.nearbypetrolpumps(lat+","+longit,"1000",type,API_KEY);
        return call;

    }
}
