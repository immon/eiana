package org.iana.notifications.template.def.xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 *
 * Class used only to parse template object from xml
 */
public class AddresseeHelper {

    Set<String> addressees = new HashSet<String>();

    public void setAddressee(List<String> addressees) {
        this.addressees = new HashSet<String>(addressees);
    }

    public Set<String> getAddreessees() {
        return addressees;   
    }

}
