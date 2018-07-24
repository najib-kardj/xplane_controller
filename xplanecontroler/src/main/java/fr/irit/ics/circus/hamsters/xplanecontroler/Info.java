/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.circus.hamsters.xplanecontroler;

/**
 *
 * @author SYSTEM
 */
class Info {

    private final float stormheight;
    private final double lat;
    private final double lon;
    private float centermapx;
    private float centermapy;
    private double phiplane;
    private double lambdaplane;
    private double sinPhiplane;
    private double cosPhiplane;
    private double phi;
    private double lambda;
    private double sinPhiPoint;
    private double cos_phi;
    private float x;
    private float y;

    Info(double lat, double lon, float stormheight) {
        this.lat = lat;
        this.lon = lon;
        this.stormheight = stormheight;
    }

    float getStormHeight() {
        return stormheight;
    }

    /*  float getDistance(PlanePosition planeposition) {
        double R = 6371000;
        double theta1 = Math.toRadians((double) planeposition.getLat());
        double theta2 = Math.toRadians((double) lat);
        double detlatetha = Math.toRadians((double) lat - planeposition.getLat());
        double deltalambda = Math.toRadians((double) lon - planeposition.getLon());

        double a = Math.sin(detlatetha / 2) * Math.sin(detlatetha / 2)
                + Math.cos(theta1) * Math.cos(theta2)
                * Math.sin(deltalambda / 2) * Math.sin(deltalambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return (float) (distance / 1000);
    }

    float getBearing(PlanePosition planeposition) {
        double R = 6371000;
        double theta1 = Math.toRadians((double) planeposition.getLat());
        double theta2 = Math.toRadians((double) lat);
        double detlatetha = Math.toRadians((double) lat - planeposition.getLat());
        double deltalambda = Math.toRadians((double) lon - planeposition.getLon());

        double a = Math.sin(detlatetha / 2) * Math.sin(detlatetha / 2)
                + Math.cos(theta1) * Math.cos(theta2)
                * Math.sin(deltalambda / 2) * Math.sin(deltalambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //  double distance = R * c;
        // System.err.println("Plane" + planeposition.getLat() + "," + planeposition.getLon() + ",  " + lat + "," + lon);
        // System.err.println("distance" + distance / 1000);
        double y = Math.sin((double) lon - planeposition.getLon()) * Math.cos(theta2);
        double x = Math.cos(theta1) * Math.sin(theta2)
                - Math.sin(theta1) * Math.cos(theta2) * Math.cos((double) lon - planeposition.getLon());
        return (float) Math.toDegrees(Math.atan2(y, x));
      
    }*/
    void setPlanePos(PlanePosition planepos) {
        phiplane = Math.toRadians(planepos.getLat());
        lambdaplane = Math.toRadians(planepos.getLon());
        sinPhiplane = Math.sin(phiplane);
        cosPhiplane = Math.cos(phiplane);
    }

    void compute() {
        phi = Math.toRadians(lat);
        lambda = Math.toRadians(lon);
        sinPhiPoint = Math.sin(phi);
        cos_phi = Math.cos(phi);
        double d_lambda = lambda - lambdaplane;
        double sin_d_lambda = Math.sin(d_lambda);
        double cos_d_lambda = Math.cos(d_lambda);
        // cos_rho = sin_phi1 * sin_phi + cos_phi1 * cos_phi * cos_d_lambda;
        // tan_theta = cos_phi * sin_d_lambda / ( cos_phi1 * sin_phi - sin_phi1 * cos_phi * cos_d_lambda );
        double rho = Math.acos(sinPhiplane * sinPhiPoint + cosPhiplane * cos_phi * cos_d_lambda);
        double theta = Math.atan2(cosPhiplane * sinPhiPoint - sinPhiplane * cos_phi * cos_d_lambda, cos_phi * sin_d_lambda);
        x = (float) (rho * Math.sin(theta));
        y = -(float) (rho * Math.cos(theta));
    }

    void setMapCenter(float xc, float yy) {
        this.centermapx = xc;
        this.centermapy = yy;
    }
    public static float pixels_per_nm = 3;

    float getX() {
        return Math.round(this.centermapx - y * 180.0f / (float) Math.PI * 60.0f * this.pixels_per_nm);
    }

    float getY() {
        return Math.round(this.centermapy - x * 180.0f / (float) Math.PI * 60.0f * this.pixels_per_nm);
    }

}
