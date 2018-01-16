package com.tp.locator;

/**
 * Created by user on 5/23/2015.
 */
public class MyAddress {
    public String address;

    @Override
    public String toString() {
        return knownName+"\n"+address+"\n"+city+"\n"+state+"\n"+country+"\n"+postalCode;
    }

    public double latitude;
    public double longitude;
    public String city;
    public String state;
    public String country;
    public String postalCode;
    public String knownName;
}
