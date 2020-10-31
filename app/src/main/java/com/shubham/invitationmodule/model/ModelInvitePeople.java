package com.shubham.invitationmodule.model;

import java.io.Serializable;

public class ModelInvitePeople implements Serializable {
    private boolean isChecked = false;
    String name, email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
