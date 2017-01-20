package sodevan.sarcar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sodevan.sarcar.MapModels.GetStreetInfo;
import sodevan.sarcar.MapModels.MapResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent ch = new Intent(this, Map.class) ;
        startActivity(ch);
       /* GetStreetInfo getStreetInfo=new GetStreetInfo("28.609","77.0375");
        Call<MapResponse> call=getStreetInfo.getkey();
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                Log.d("TAG", response.body().getResults().get(0).getAddressComponents().get(0).getLongName());
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });*/
    }
}
