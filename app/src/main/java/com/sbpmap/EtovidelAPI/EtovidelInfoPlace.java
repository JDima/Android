package com.sbpmap.EtovidelAPI;

import com.sbpmap.Map.Place;

/**
 * Created by JDima on 30/03/15.
 */
public class EtovidelInfoPlace extends Place{

    private String address;

    private String definition;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
