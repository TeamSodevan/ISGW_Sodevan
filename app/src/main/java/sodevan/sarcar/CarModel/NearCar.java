package sodevan.sarcar.CarModel;

import android.location.Location;

/**
 * Created by ronaksakhuja on 21/01/17.
 */

public class NearCar {
    Location location;
    float distance;

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Location getLocation() {
        return location;
    }

    public float getDistance() {
        return distance;
    }

    public NearCar(Location location, float distance) {

        this.location = location;
        this.distance = distance;
    }
}
