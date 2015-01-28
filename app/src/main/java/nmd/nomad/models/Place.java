package nmd.nomad.models;

/**
 * Created by nicolasiensen on 1/28/15.
 */
public class Place {
    Integer id;
    Double lat;
    Double lng;
    String name;

    public String toString(){
        return name;
    }
}