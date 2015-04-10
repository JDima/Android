package com.sbpmap.Utils;

/**
 * Created by JDima on 10/04/15.
 */
public class LatLngBounds {
    private double southlongitude;
    private double southlatitude;
    private double northlongitude;
    private double northlatitude;

    public LatLngBounds(double slat, double slng, double nlat, double nlng){
        this.southlatitude = slat;
        this.northlatitude = nlat;
        this.southlongitude = slng;
        this.northlongitude = nlng;
    }

    public double getSouthlongitude() {
        return southlongitude;
    }

    public void setSouthlongitude(double southlongitude) {
        this.southlongitude = southlongitude;
    }

    public double getSouthlatitude() {
        return southlatitude;
    }

    public void setSouthlatitude(double southlatitude) {
        this.southlatitude = southlatitude;
    }

    public double getNorthlongitude() {
        return northlongitude;
    }

    public void setNorthlongitude(double northlongitude) {
        this.northlongitude = northlongitude;
    }

    public double getNorthlatitude() {
        return northlatitude;
    }

    public void setNorthlatitude(double northlatitude) {
        this.northlatitude = northlatitude;
    }
}
