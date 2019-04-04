package com.logicpd.papapill.data;

import java.io.Serializable;

/**
 * Class for holding data for medications
 *
 * @author alankilloren
 */
public class Medication implements Serializable {

    private int id;
    private String name;
    private String nickname;
    private int userId;
    private int strength_value;
    private String strength_measurement;
    private String dosage_instructions;
    private int time_between_doses;
    private int max_units_per_day;
    private int max_number_per_dose;
    private int medication_quantity;
    private String use_by_date;
    private int medication_location;
    private String medication_location_name;
    private String patient_name;
    private boolean paused;
    private String fill_date;
    private int medication_schedule_type;// 0=as-needed or scheduled, 1=as-needed, 2=scheduled

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStrength_value() {
        return strength_value;
    }

    public void setStrength_value(int strength_value) {
        this.strength_value = strength_value;
    }

    public String getStrength_measurement() {
        return strength_measurement;
    }

    public void setStrength_measurement(String strength_measurement) {
        this.strength_measurement = strength_measurement;
    }

    public int getTime_between_doses() {
        return time_between_doses;
    }

    public void setTime_between_doses(int time_between_doses) {
        this.time_between_doses = time_between_doses;
    }

    public int getMax_units_per_day() {
        return max_units_per_day;
    }

    public void setMax_units_per_day(int max_units_per_day) {
        this.max_units_per_day = max_units_per_day;
    }

    public int getMax_number_per_dose() {
        return max_number_per_dose;
    }

    public void setMax_number_per_dose(int max_number_per_dose) {
        this.max_number_per_dose = max_number_per_dose;
    }

    public int getMedication_quantity() {
        return medication_quantity;
    }

    public void setMedication_quantity(int medication_quantity) {
        this.medication_quantity = medication_quantity;
    }

    public String getUse_by_date() {
        return use_by_date;
    }

    public void setUse_by_date(String use_by_date) {
        this.use_by_date = use_by_date;
    }

    public int getMedication_location() {
        return medication_location;
    }

    public void setMedication_location(int medication_location) {
        this.medication_location = medication_location;
    }

    public String getMedication_location_name() {
        return medication_location_name;
    }

    public void setMedication_location_name(String medication_location_name) {
        this.medication_location_name = medication_location_name;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public String getFill_date() {
        return fill_date;
    }

    public void setFill_date(String fill_date) {
        this.fill_date = fill_date;
    }

    public int getMedication_schedule_type() {
        return medication_schedule_type;
    }

    public void setMedication_schedule_type(int medication_schedule_type) {
        this.medication_schedule_type = medication_schedule_type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDosage_instructions() {
        return dosage_instructions;
    }

    public void setDosage_instructions(String dosage_instructions) {
        this.dosage_instructions = dosage_instructions;
    }
}
