package sodevan.sarcar;

/**
 * Created by ravipiyush on 14/01/17.
 */

public class Car {

    private String latitude ;
    private String longitude;
    private String carid  ;
    private String speed ;

    public Car() {
    }

    public Car(String latitude, String longitude, String carid, String speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.carid = carid;
        this.speed = speed;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCarid() {
        return carid;
    }

    public String getSpeed() {
        return speed;
    }


}
