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
class PlanePosition {

    private double lat;
    private double lon;
    private final double elevation;
    private final double elevationterrain;
    private boolean valid;
    private double headingpsi = 0d;

    PlanePosition(double lat, double lon, double elevation, double elevationterrain) {
        this.lat = lat;
        this.lon = lon;
        this.elevation = elevation;
        this.elevationterrain = elevationterrain;
    }

    void setValid(boolean b) {
        this.valid = b;
    }

    public boolean isValid() {
        return valid;
    }

    double getLat() {
        return this.lat;
    }

    double getLon() {
        return this.lon;
    }

    void setLat(double lat) {
        this.lat = lat;
    }

    void setLon(double lon) {
        this.lon = lon;
    }

    double getHeading() {
        return this.headingpsi;
    }

    void setHeading(float heading) {
        this.headingpsi = heading;
    }

}
