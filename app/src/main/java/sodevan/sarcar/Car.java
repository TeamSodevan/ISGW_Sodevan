package sodevan.sarcar;

/**
 * Created by ravipiyush on 14/01/17.
 */

public class Car {

    private String latitude ;
    private String longitude;
    private String carid  ;
    private String speed ;
    private String prevlatitude ;
    private String prevlongitude  ;
    public Car() {
    }

    public Car(String latitude, String longitude, String carid, String speed, String prevlatitude, String prevlongitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.carid = carid;
        this.speed = speed;
        this.prevlatitude = prevlatitude;
        this.prevlongitude = prevlongitude;
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

    public String getPrevlatitude() {
        return prevlatitude;
    }

    public String getPrevlongitude() {
        return prevlongitude;
    }
}
