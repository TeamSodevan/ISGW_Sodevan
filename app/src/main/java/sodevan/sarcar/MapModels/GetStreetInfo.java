package sodevan.sarcar.MapModels;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sodevan.sarcar.Interfaces.MapApiInterface;

/**
 * Created by ronaksakhuja on 20/01/17.
 */

public class GetStreetInfo {
    String lat,longi;
    public GetStreetInfo(String lat, String longi){
        this.lat=lat;
        this.longi=longi;

    }


   public Call<MapResponse> getkey() {
        String API_KEY = "AIzaSyBK7099rWFmhOjfF42wJ9GDxNa0AMdkIdQ";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapApiInterface api = retrofit.create(MapApiInterface.class);

        Call<MapResponse> call = api.getstreetdata(lat+","+longi, API_KEY);
        return call;
    }


}
