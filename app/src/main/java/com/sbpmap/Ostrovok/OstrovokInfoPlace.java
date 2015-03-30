package com.sbpmap.Ostrovok;

import com.sbpmap.Map.Place;

/**
 * Created by JDima on 30/03/15.
 */
public class OstrovokInfoPlace extends Place{

    private String address;
    private double cost;
    private String definition;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
