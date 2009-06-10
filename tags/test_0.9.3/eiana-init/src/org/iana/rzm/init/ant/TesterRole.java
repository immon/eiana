package org.iana.rzm.init.ant;

import org.jdom.*;

public class TesterRole {
    private String domain;
    private String type;
    private String email;

    public TesterRole(Element elm) {
        domain = elm.getChildText("domain");
        type= elm.getChildText("type");
        email = elm.getChildText("email");
    }

    public String getDomain(){
        return domain;
    }

    public String getType(){
        return type;
    }

    public String getEmail(){
        return email;
    }
}
