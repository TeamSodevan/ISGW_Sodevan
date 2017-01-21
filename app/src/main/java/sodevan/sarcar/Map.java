package sodevan.sarcar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

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

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sodevan.sarcar.MapModels.GetStreetInfo;
import sodevan.sarcar.MapModels.MapResponse;
import sodevan.sarcar.PlacesModels.Places;
import sodevan.sarcar.PlacesModels.PlacesResponse;


public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        tv_low= (TextView) findViewById(R.id.loc_road);
        Myloc=new Location("");
        Prev_loc=new Location("");
        ambstatus = new HashMap<>() ;

        mContext = this;

        database = FirebaseDatabase.getInstance() ;






        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION},  permissionRequest);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


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
                Call<MapResponse> call=getStreetInfo.getkey();
                call.enqueue(new Callback<MapResponse>() {
                    @Override
                    public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                        Log.d("TAG", response.body().getResults().get(0).getAddressComponents().get(0).getLongName());
                        tv_low.setText(response.body().getResults().get(0).getAddressComponents().get(0).getLongName());
                     //   roadname = response.body().getResults().get(0).getAddressComponents().get(0).getLongName() ;

                        reference = database.getReference("Cars").child(roadname).child(carId) ;

                    }


                    @Override
                    public void onFailure(Call<MapResponse> call, Throwable t) {

                    }
                });

                Places places=new Places(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                Call<PlacesResponse> call1=places.getKey();
                call1.enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                       for(int i=0;i<=0;i++) {
                           Log.d("Tag_Places", response.body().getResults().get(i).getName());
                       }
                    }

                    @Override
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {

                    }
                });



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
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 21.0f));

                    flag=1 ;
                    }

                else if (marker!=null){
                    animateMarker(marker , loc , false);
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 21.0f));


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

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,  locationListener);



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

        {
            reference2 = database.getReference("Cars").child(roadname);

            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Location cur_loc=new Location("");
                        Location prev_loc=new Location("");
                        String current_lat = String.valueOf(dsp.child("latitude").getValue());
                        String current_long = String.valueOf(dsp.child("longitude").getValue());
                        String prev_lat=String.valueOf(dsp.child("prevlatitude").getValue());
                        String prev_long=String.valueOf(dsp.child("prevlongitude").getValue());
                        Log.d("Tags_prev",prev_lat);
                        Log.d("Tags_cur",prev_long);

                        cur_loc.setLatitude(Double.parseDouble(current_lat));
                        cur_loc.setLongitude(Double.parseDouble(current_long));
                        prev_loc.setLatitude(Double.parseDouble(prev_lat));
                        prev_loc.setLongitude(Double.parseDouble(prev_long));
                        cur_dist=cur_loc.distanceTo(Myloc);
                        prev_dist=prev_loc.distanceTo(Prev_loc);
                        //Toast.makeText(mContext, "Distance"+dist, Toast.LENGTH_SHORT).show();
                        Log.d("Dist",cur_dist+","+prev_dist);
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
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

                        MarkerOptions markerop=  new MarkerOptions().position(ns).icon(BitmapDescriptorFactory.fromBitmap(im)) ;






                        String m =  ambstatus.get(am.getAmbid())   ;

                        if (m==null){

                            Log.d("Ambulance"  , " Thats New") ;
                            ambulance = gmap.addMarker(markerop) ;
                             ambstatus.put(am.getAmbid() , "added" ) ;

                        }

                        else {

                            Log.d("Ambulance"  , " moving Marker") ;

                            animateMarker(ambulance , ns , false);

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


}
