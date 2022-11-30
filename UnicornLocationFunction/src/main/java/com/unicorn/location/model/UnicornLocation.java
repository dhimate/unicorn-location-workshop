package com.unicorn.location.model;

public class UnicornLocation {
    private String unicornName;
    private String longitude;
    private String latitude;
    private String id;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUnicornName() {
        return unicornName;
    }
    public void setUnicornName(String unicornName) {
        this.unicornName = unicornName;
    }

    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}