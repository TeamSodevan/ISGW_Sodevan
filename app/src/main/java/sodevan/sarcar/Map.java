package sodevan.sarcar;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sodevan.sarcar.MapModels.GetStreetInfo;
import sodevan.sarcar.MapModels.MapResponse;
import sodevan.sarcar.PlacesModels.Places;
import sodevan.sarcar.PlacesModels.PlacesResponse;
import sodevan.sarcar.PlacesModels.Result;
import zemin.notification.NotificationBuilder;
import zemin.notification.NotificationDelegater;
import zemin.notification.NotificationLocal;
import zemin.notification.NotificationView;


public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    TextToSpeech tts1;
    private GoogleMap gmap;
    private GoogleApiClient googleApiClient;
    Marker ambulance  ;
    private static int request = 9000;
    private static String TAG = "Map Activity";
    Context mContext;
    TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    LocationManager locationManager;
    Marker marker;
    private static int permissionRequest = 120 ;
    private int flag = 0 , flag2 =0 ;
    private String carId = "car-9990401860" ;
    float cur_dist=0,prev_dist=0;
    HashMap<String,Location> NearbyVehichles;
    HashMap<String , Marker> markersred ;
    FirebaseDatabase database ;
    DatabaseReference reference , reference2  , reference3;
    TextView tv_low;
    String roadname = "MangalPandayRoad" ;

    HashMap< String , String >  ambstatus ;
    HashMap< String , String> sad ;
    Location Myloc,Prev_loc;
    String privlat;
    String privlong ;
    double LAT,LONG,prev_lat,prev_long;
    String prevroad =null;
    Drawable sa ;
    SharedPreferences pref;
    int r1 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        tv_low= (TextView) findViewById(R.id.loc_road);
        Myloc=new Location("");
        Prev_loc=new Location("");
        ambstatus = new HashMap<>() ;
        markersred = new HashMap<>() ;
        NearbyVehichles=new HashMap<>();
        mContext = this;

        database = FirebaseDatabase.getInstance() ;

       sa = getResources().getDrawable(R.drawable.alert)  ;


        NotificationDelegater.initialize(this, NotificationDelegater.LOCAL);

        NotificationLocal local = NotificationDelegater.getInstance().local();
        NotificationView view = (NotificationView) findViewById(R.id.nv);
        local.setView(view);


        pref=getSharedPreferences("Preferences",Context.MODE_PRIVATE);
        carId="car-"+pref.getString("phone",null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION},  permissionRequest);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        tts1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i !=TextToSpeech.ERROR){
                    tts1.setLanguage(Locale.US);
                    tts1.setSpeechRate((float) 0.9);
                }
            }
        });




        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                prev_lat=LAT;
                prev_long=LONG;
                setpreviouslocation(prev_lat,prev_long);
                LAT=location.getLatitude();
                LONG=location.getLongitude();
                setcurrentlocation(LAT,LONG);

                //Log.d(TAG , "Lat :"+location.getLatitude() + "  , Long : "+location.getLongitude()) ;





                GetStreetInfo getStreetInfo=new GetStreetInfo(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                final Call<MapResponse> call=getStreetInfo.getkey();
                call.enqueue(new Callback<MapResponse>() {
                    @Override
                    public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                        prevroad = roadname;

                        Log.d("TAG", response.body().getResults().get(0).getAddressComponents().get(0).getLongName());
                        tv_low.setText(response.body().getResults().get(0).getAddressComponents().get(0).getLongName());
                       roadname = response.body().getResults().get(0).getAddressComponents().get(0).getLongName() ;

                        reference = database.getReference("Cars").child(roadname).child(carId) ;

                    }


                    @Override
                    public void onFailure(Call<MapResponse> call, Throwable t) {

                    }
                });
               /* Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {



                        //Your code goes heretry {
                        try {
                           List<sodevan.sarcar.MapModels.Result> listaaa= call.execute().body().getResults();

                           if(listaaa.size()!=0) roadname = call.execute().body().getResults().get(0).getAddressComponents().get(0).getLongName();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });

                thread.start();*/
                if (prevroad!=roadname&&prevroad!=null){
                    DatabaseReference ref=database.getReference("Cars").child(prevroad).child(carId);
                    ref.removeValue();
                }

                tv_low.setText(roadname);
                Log.d("roadname", roadname + "");
                if (roadname != null) {
                    reference = database.getReference("Cars").child(roadname).child(carId);
                    Log.d("firebase", reference.getKey());
                }

                Places places=new Places(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                final Call<PlacesResponse> call1=places.getKey();
                 call1.enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                        Log.d("TAG",response.toString());
                        if(response.body().getResults().size()!=0){
                       for(int i=0;i<=1;i++) {
                           Log.d("Tag_Places", response.body().getResults().get(i).getName());
                           Log.d("Tag_LAT", response.body().getResults().get(i).getGeometry().getLocation().getLat().toString());
                           Log.d("Tag_LONG", response.body().getResults().get(i).getGeometry().getLocation().getLng().toString());

                           Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                           Double Long = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                           putpetrolpump(new LatLng(lat, Long));
                       }


                       }
                    }

                    @Override
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {

                    }
                });

                /*
                Thread thread2 = new Thread(new Runnable() {
                    List<sodevan.sarcar.PlacesModels.Result> lista = null;

                    @Override
                    public void run() {
                        try {
                            //Your code goes here

                            try {
                                lista = call1.execute().body().getResults();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i <= 1; i++) {
                                if (lista.size()!=0) {
                                    Double lat = lista.get(i).getGeometry().getLocation().getLat();
                                    Double Long = lista.get(i).getGeometry().getLocation().getLng();
                                    putpetrolpump(new LatLng(lat, Long));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread2.start();


*/

                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

                if (reference!=null) {

                    if (flag2==0) {
                        reference.setValue(new Car(location.getLatitude() + "", location.getLongitude() + "", carId, location.getSpeed() + "" , location.getLatitude() +"", location.getLongitude()+""));
                        flag2++;

                    }

                    else {
                        reference.setValue(new Car(location.getLatitude() + "", location.getLongitude() + "", carId, location.getSpeed() + "" ,privlat, privlong )) ;
                    }

                }
                checkcollision();
                checkambulance();

                Bitmap bm = BitmapFactory.decodeResource(getResources() , R.drawable.car) ;
                Bitmap im = Bitmap.createScaledBitmap(bm , 80 , 179 , false) ;
                MarkerOptions markerop=  new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(im)) ;


                if (flag==0 && gmap!=null) {
                    marker = gmap.addMarker(markerop) ;
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 19.0f));

                    flag=1 ;
                }

                else if (marker!=null){
                    animateMarker(marker , loc , false);
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 19.0f));


                }

                privlat = location.getLatitude() +"" ;
                privlong = location.getLongitude() +"";


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        } ;

        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0,  locationListener);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, 0, locationListener);

    }



    private  void  putpetrolpump(LatLng loca) {

        Bitmap bm = BitmapFactory.decodeResource(getResources() , R.drawable.gs) ;
        Bitmap im = Bitmap.createScaledBitmap(bm , 100 , 100 , false) ;

        MarkerOptions markerop = new MarkerOptions().position(loca).icon(BitmapDescriptorFactory.fromBitmap(im)) ;
        Marker marker = gmap.addMarker(markerop) ;



    }

    private void setpreviouslocation(double prev_lat, double prev_long) {
        Prev_loc.setLatitude(prev_lat);
        Prev_loc.setLongitude(prev_long);
        Log.d("TAG_Previous",prev_lat+","+prev_long);
    }

    private void setcurrentlocation(double lat, double aLong) {
        Myloc.setLatitude(lat);
        Myloc.setLongitude(aLong);
        Log.d("TAG_current",lat+","+aLong);
    }

    private void checkcollision() {
        if (roadname!=null)

        {   r1=0 ;
            reference2 = database.getReference("Cars").child(roadname);

            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    NearbyVehichles = new HashMap<String, Location>() ;


                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (!String.valueOf(dsp.child("carid").getValue()).equals(carId)) {
                            Location cur_loc = new Location("");
                            Location prev_loc = new Location("");
                            String current_lat = String.valueOf(dsp.child("latitude").getValue());
                            String current_long = String.valueOf(dsp.child("longitude").getValue());
                            String prev_lat = String.valueOf(dsp.child("prevlatitude").getValue());
                            String prev_long = String.valueOf(dsp.child("prevlongitude").getValue());
                            Log.d("Tags_prev", prev_lat);
                            Log.d("Tags_cur", prev_long);

                            cur_loc.setLatitude(Double.parseDouble(current_lat));
                            cur_loc.setLongitude(Double.parseDouble(current_long));
                            prev_loc.setLatitude(Double.parseDouble(prev_lat));
                            prev_loc.setLongitude(Double.parseDouble(prev_long));
                            cur_dist = cur_loc.distanceTo(Myloc);
                            prev_dist = prev_loc.distanceTo(Prev_loc);
                            //Toast.makeText(mContext, "Distance"+dist, Toast.LENGTH_SHORT).show();


                            if (cur_dist < prev_dist && cur_dist <= 100) {



                                NearbyVehichles.put(String.valueOf(dsp.child("carid").getValue()), cur_loc);
                                r1=r1+1 ;



                            }


                            Log.d("Dist", cur_dist + "," + prev_dist);
                            }
                        }

                   /* if (r1!=0){
                        NotificationBuilder.V1 builder = NotificationBuilder.local()
                                .setIconDrawable(sa)
                                .setTitle("Collision Prediction")
                                .setText("Please be Careful with nearby Red Car shown in our Map")
                                .setLayoutId(zemin.notification.R.layout.notification_full);

                        NotificationDelegater delegater = NotificationDelegater.getInstance();
                        delegater.send(builder.getNotification());
                        tts1.speak("Please be Careful with nearby Red Car shown in our Map",TextToSpeech.QUEUE_ADD,null);

                    }*/

                    if (NearbyVehichles!=null){
                        MapNearbyVehichles();
                    }


                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void MapNearbyVehichles() {
        String toSpeak="Vehicle nearby be careful";
        Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_LONG).show();
        HashMap<String , Marker> tempred =   new HashMap<>();
        Set<String> keys = NearbyVehichles.keySet() ;

        for(String id : keys ){

            Log.d("red" , id) ;

            Location ln = NearbyVehichles.get(id)  ;

            LatLng ns= new LatLng(ln.getLatitude() , ln.getLongitude()) ;

            Marker m    = markersred.get(id) ;

            if (m==null) {

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.red);
                Bitmap im = Bitmap.createScaledBitmap(bm, 80, 179, false);
                MarkerOptions markerop = new MarkerOptions().position(ns).icon(BitmapDescriptorFactory.fromBitmap(im));
                Marker m1 = gmap.addMarker(markerop) ;
                tempred.put(id , m1) ;


            }


            else  {
                animateMarker(m ,   ns, false );
                tempred.put(id,m) ;
                markersred.remove(id) ;
            }
        }


        if (markersred!=null) {
            Set<String> keys2 = markersred.keySet();

            for (String id : keys2) {

                Marker m = markersred.get(id);
                removemarker(m);
            }



        }

        markersred = tempred ;




    }


    private void checkambulance() {

        if (roadname!=null){

            reference3  = database.getReference("Ambulances").child(roadname) ;

            reference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ipo : dataSnapshot.getChildren()){

                        Ambulances am = ipo.getValue(Ambulances.class) ;

                        Bitmap bm = BitmapFactory.decodeResource(getResources() , R.drawable.ambulance) ;
                        Bitmap im = Bitmap.createScaledBitmap(bm , 80 , 179 , false) ;

                        LatLng ns = new LatLng(am.getLat() , am.getLongi() )  ;

                        Log.d("Ambulance" ,ns+"" ) ;
                        String toSpeak="Ambulance nearby kindly move your vehicle in the left lane";
                        Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_LONG).show();
                       // tts1.speak(toSpeak,TextToSpeech.QUEUE_ADD,null);
                        MarkerOptions markerop =  new MarkerOptions().position(ns).icon(BitmapDescriptorFactory.fromBitmap(im)) ;



                        int r=0 ;


                        String m =  ambstatus.get(am.getAmbid())   ;

                        if (m==null){

                            Log.d("Ambulance"  , " Thats New") ;
                            ambulance = gmap.addMarker(markerop) ;
                            ambstatus.put(am.getAmbid() , "added" ) ;

                            r=r+1 ;



                        }

                        else {

                            Log.d("Ambulance"  , " moving Marker") ;

                            animateMarker(ambulance , ns , false);
                            r=r+1 ;

                        }


                        if (r!=0)
                        {
                           /* NotificationBuilder.V1 builder = NotificationBuilder.local()
                                    .setIconDrawable(sa)
                                    .setTitle("Collision Prediction")
                                    .setText("There is An Ambulance on your Route . Kindly switch to left lane")
                                    .setLayoutId(zemin.notification.R.layout.notification_full);

                            NotificationDelegater delegater = NotificationDelegater.getInstance();
                            delegater.send(builder.getNotification());*/

                          //
                            //  tts1.speak("There is An Ambulance on your Route . Kindly switch to left lane",TextToSpeech.QUEUE_ADD,null);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }) ;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        Log.d(TAG,"MAP READY") ;
        gmap = googleMap;

    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void removemarker ( Marker marker) {
        marker.remove();
    }


    public void animateMarker ( final Marker marker,  final LatLng topostion , final boolean hideMarker) {
        final Handler handler = new Handler() ;

        final long start = SystemClock.uptimeMillis() ;
        Projection projection = gmap.getProjection() ;

        // Getting current Marker Location
        Point startpoint = projection.toScreenLocation(marker.getPosition()) ;
        final LatLng startLatlong = projection.fromScreenLocation(startpoint) ;
        final long duration = 500 ;

        final LinearInterpolator interpolator = new LinearInterpolator() ;

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis()-start ;

                float t = interpolator.getInterpolation((float)elapsed/duration) ;
                double lng = t * topostion.longitude + (1 - t)
                        * startLatlong.longitude;
                double lat = t * topostion.latitude + (1 - t)
                        * startLatlong.latitude;

                marker.setPosition(new LatLng(lat , lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }

                else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }

                }



            }
        });

    }
    /*











        XML

    */


}
