package com.logicpd.papapill.data;

import java.io.Serializable;

public class ScheduleItem implements Serializable {

    private int id;
    private int userId;
    private int medicationId;
    private int dispenseTimeId;
    private int dispenseAmount;
    private int scheduleType;//1=daily, 2=weekly, 3=monthly
    private String scheduleDate;
    private String scheduleDay;
    private String dispenseTime;
    private String dispenseName;

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

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public int getDispenseTimeId() {
        return dispenseTimeId;
    }

    public void setDispenseTimeId(int dispenseTimeId) {
        this.dispenseTimeId = dispenseTimeId;
    }

    public int getDispenseAmount() {
        return dispenseAmount;
    }

    public void setDispenseAmount(int dispenseAmount) {
        this.dispenseAmount = dispenseAmount;
    }

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleDay() {
        return scheduleDay;
    }

    public void setScheduleDay(String scheduleDay) {
        this.scheduleDay = scheduleDay;
    }

    public String getDispenseTime() {
        return dispenseTime;
    }

    public void setDispenseTime(String dispenseTime) {
        this.dispenseTime = dispenseTime;
    }

    public String getDispenseName() {
        return dispenseName;
    }

    public void setDispenseName(String dispenseName) {
        this.dispenseName = dispenseName;
    }
}
