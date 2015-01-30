package nmd.nomad.models;

/**
 * Created by nicolasiensen on 1/28/15.
 */
public class Place {
    Integer id;
    Double lat;
    Double lng;
    String name;
    Double distance;

    public String toString(){
        return name;
    }

    public String getName() {
        return name;
    }

    public Double getDistance() {
        return distance;
    }
}