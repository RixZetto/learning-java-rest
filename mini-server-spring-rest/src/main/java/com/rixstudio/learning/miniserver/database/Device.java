package com.rixstudio.learning.miniserver.database;

import java.io.Serializable;

public class Device implements Serializable {

    private String token;
    private DeviceType type;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

}
