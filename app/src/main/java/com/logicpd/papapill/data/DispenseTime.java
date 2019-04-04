package com.logicpd.papapill.data;

import java.io.Serializable;

public class DispenseTime implements Serializable {
    private int id;
    private int userId;
    private String dispenseName;
    private String dispenseTime;
    private int dispenseAmount;
    private boolean isActive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDispenseName() {
        return dispenseName;
    }

    public void setDispenseName(String dispenseName) {
        this.dispenseName = dispenseName;
    }

    public String getDispenseTime() {
        return dispenseTime;
    }

    public void setDispenseTime(String dispenseTime) {
        this.dispenseTime = dispenseTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getDispenseAmount() {
        return dispenseAmount;
    }

    public void setDispenseAmount(int dispenseAmount) {
        this.dispenseAmount = dispenseAmount;
    }
}
