package org.iana.rzm.init.ant;

import org.jdom.*;

import java.util.*;

public class IanaTester {

    private String userName;
    private String first;
    private String last;
    private String email;
    private List<TesterRole> roles;

    public IanaTester(Element value) {
        userName = value.getAttributeValue("id");
        first = value.getChildText("first");
        last  = value.getChildText("last");
        email = value.getChildText("email");
        roles = new ArrayList<TesterRole>();
        addRoles(value.getChildren("role"));

    }

    public List<TesterRole> getRoles(){
        return roles;
    }


    public String getUserName() {
        return userName;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getEmail() {
        return email;
    }

    private void addRoles(List roles) {
        for (Object obj : roles) {
            Element elm = (Element) obj;
            this.roles.add(new TesterRole(elm));
        }
    }
}
