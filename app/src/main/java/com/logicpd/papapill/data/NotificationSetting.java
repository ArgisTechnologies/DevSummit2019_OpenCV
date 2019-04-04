package com.logicpd.papapill.data;

public class NotificationSetting {

    private int id;
    private int userId;
    private int contactId;
    private String name;
    private boolean isTextSelected;
    private boolean isVoiceSelected;
    private boolean isEmailSelected;

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

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTextSelected() {
        return isTextSelected;
    }

    public void setTextSelected(boolean textSelected) {
        isTextSelected = textSelected;
    }

    public boolean isVoiceSelected() {
        return isVoiceSelected;
    }

    public void setVoiceSelected(boolean voiceSelected) {
        isVoiceSelected = voiceSelected;
    }

    public boolean isEmailSelected() {
        return isEmailSelected;
    }

    public void setEmailSelected(boolean emailSelected) {
        isEmailSelected = emailSelected;
    }
}
