package org.iana.rzm.trans.change;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.ObjectInstantiator;
import org.iana.rzm.domain.IPAddress;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IPAddressInstantiator implements ObjectInstantiator {

    public Object instantiate(Change change) {
        ObjectChange objectChange = (ObjectChange) change;
        System.out.println("ip address id " + objectChange.getId());
        return IPAddress.createIPAddress(objectChange.getId());
    }
}
