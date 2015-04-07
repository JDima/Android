package com.sbpmap.Ostrovok;

import com.sbpmap.Map.Place;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by JDima on 30/03/15.
 */
public class OstrovokInfoPlace extends Place{

    private String address;
    private String cost;
    private String definition;
    private LinkedHashSet<String> adds = new LinkedHashSet<>();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public LinkedHashSet<String> getAdds() {
        return adds;
    }

    public void setAdds(LinkedHashSet<String> adds) {
        this.adds = adds;
    }

    public void addAdds(LinkedHashSet<String> adds) {
        this.adds.addAll(adds);
    }
}
