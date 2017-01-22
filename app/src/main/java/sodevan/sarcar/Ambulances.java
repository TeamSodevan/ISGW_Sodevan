package sodevan.sarcar;

/**
 * Created by ravipiyush on 21/01/17.
 */

public class Ambulances {

    String ambid ;
    double lat ;
    double longi ;
    double prevlat ;
    double prevlong ;

    public Ambulances() {
    }

    public Ambulances(String ambid, double lat, double longi, double prevlat, double prevlong) {
        this.ambid = ambid;
        this.lat = lat;
        this.longi = longi;
        this.prevlat = prevlat;
        this.prevlong = prevlong;

    }


    public String getAmbid() {
        return ambid;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }

    public double getPrevlat() {
        return prevlat;
    }

    public double getPrevlong() {
        return prevlong;
    }
}
