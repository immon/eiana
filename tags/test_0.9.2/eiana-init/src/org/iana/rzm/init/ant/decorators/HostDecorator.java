package org.iana.rzm.init.ant.decorators;

import org.iana.rzm.domain.*;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class HostDecorator {
    private Host host = new Host();

    public Host getHost() {
        return host;
    }

    public void setName(String name) {
        host.setName(name);
    }
    
    public void setIpaddress(String value) {
        host.addIPAddress(value);
    }

    public void setIpaddress(List<String> values) {
        for (String value : values)
            if(value != null && value.trim().length() > 0)
                host.addIPAddress(value);
    }
}
