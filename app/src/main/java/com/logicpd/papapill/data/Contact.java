package com.logicpd.papapill.data;

import java.io.Serializable;

/**
 * Class for holding notification contacts
 *
 * @author alankilloren
 */
public class Contact implements Serializable {
    private int id;
    private int userid;
    private String name;
    private String textNumber;
    private String voiceNumber;
    private String email;
    private String category;
    private boolean isSelected;
    private boolean isTextNumberSelected;
    private boolean isVoiceNumberSelected;
    private boolean isEmailSelected;
    private int relationship;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextNumber() {
        return textNumber;
    }

    public void setTextNumber(String textNumber) {
        this.textNumber = textNumber;
    }

    public String getVoiceNumber() {
        return voiceNumber;
    }

    public void setVoiceNumber(String voiceNumber) {
        this.voiceNumber = voiceNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isTextNumberSelected() {
        return isTextNumberSelected;
    }

    public void setTextNumberSelected(boolean textNumberSelected) {
        isTextNumberSelected = textNumberSelected;
    }

    public boolean isVoiceNumberSelected() {
        return isVoiceNumberSelected;
    }

    public void setVoiceNumberSelected(boolean voiceNumberSelected) {
        isVoiceNumberSelected = voiceNumberSelected;
    }

    public boolean isEmailSelected() {
        return isEmailSelected;
    }

    public void setEmailSelected(boolean emailSelected) {
        isEmailSelected = emailSelected;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }
}
