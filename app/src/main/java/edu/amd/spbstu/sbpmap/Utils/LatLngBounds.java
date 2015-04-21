package edu.amd.spbstu.sbpmap.Utils;

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

    public static double getAlpha(double lat, double lng, double cenlat, double cenlng, LatLngBounds latLngBounds) {
        double d1 = getDistance(lat, lng, cenlat, cenlng);
        double d2 = getDistance(cenlat, cenlng, latLngBounds.getSouthlatitude(), latLngBounds.getSouthlongitude());
        double alpha = d1 / d2;
        double opacity = 1 - alpha;
        return opacity;
    }

    private static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


}