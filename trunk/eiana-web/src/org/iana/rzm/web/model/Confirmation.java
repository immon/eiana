package org.iana.rzm.web.model;

public class Confirmation extends ValueObject {

    private SystemRoleVOWrapper role;
    private boolean confirmed;
    private String name;


    public Confirmation(String name, boolean confirmed, SystemRoleVOWrapper role) {
        this.role = role;
        this.confirmed = confirmed;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return role.getTypeAsString();
    }

    public boolean isConfirmed(){
        return confirmed;
    }

    public String getContact(){
        return getType() + " - " + getName();
    }
}
