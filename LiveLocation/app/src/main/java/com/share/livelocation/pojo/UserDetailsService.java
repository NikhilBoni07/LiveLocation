package com.share.livelocation.pojo;

public class UserDetailsService {

    public UserDetailsService() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIssharing() {
        return issharing;
    }

    public void setIssharing(String issharing) {
        this.issharing = issharing;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Long getCirclecode() {
        return circlecode;
    }

    public void setCirclecode(Long circlecode) {
        this.circlecode = circlecode;
    }

    public String getCirclememberid() {
        return circlememberid;
    }

    public void setCirclememberid(String circlememberid) {
        this.circlememberid = circlememberid;
    }


    private String email;
    private String issharing;
    private double lat;
    private double lng;
    private String name;
    private String userId;
    private Long circlecode;
    private String circlememberid;


}
