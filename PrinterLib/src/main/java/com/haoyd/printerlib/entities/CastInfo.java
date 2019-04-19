package com.haoyd.printerlib.entities;

import java.io.Serializable;

public class CastInfo implements Serializable {

    public String action;
    public Object obj;

    public CastInfo(String action) {
        this.action = action;
    }
}
