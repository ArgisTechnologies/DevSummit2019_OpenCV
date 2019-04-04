package com.logicpd.papapill.data;

import java.util.List;

public class DaySchedule {

    private String day;
    private String date;
    private List<ScheduleItem> scheduleItemList;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<ScheduleItem> getScheduleItemList() {
        return scheduleItemList;
    }

    public void setScheduleItemList(List<ScheduleItem> scheduleItemList) {
        this.scheduleItemList = scheduleItemList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
