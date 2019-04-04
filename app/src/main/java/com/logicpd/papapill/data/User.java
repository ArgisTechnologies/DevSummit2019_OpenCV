package com.logicpd.papapill.data;

import java.io.Serializable;
import java.util.List;

/**
 * Class for holding user account data
 *
 * @author alankilloren
 */
public class User implements Serializable {
    private int id;
    private String username;
    private String pin;
    private String patientname;
    private List<Contact> contactList;
    private int audioVolume;
    private int fontSize;
    private int screenBrightness;
    private int voice;
    private int theme;
    private int recoveryContactId;
    private String userNumber;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public int getAudioVolume() {
        return audioVolume;
    }

    public void setAudioVolume(int audioVolume) {
        this.audioVolume = audioVolume;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getScreenBrightness() {
        return screenBrightness;
    }

    public void setScreenBrightness(int screenBrightness) {
        this.screenBrightness = screenBrightness;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getRecoveryContactId() {
        return recoveryContactId;
    }

    public void setRecoveryContactId(int recoveryContactId) {
        this.recoveryContactId = recoveryContactId;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }
}
