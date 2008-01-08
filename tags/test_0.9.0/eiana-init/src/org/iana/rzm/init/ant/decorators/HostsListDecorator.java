package org.iana.rzm.init.ant.decorators;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */
public class HostsListDecorator {

    List<HostDecorator> hosts = new ArrayList<HostDecorator>();


    public List<HostDecorator> getHosts() {
        return hosts;
    }

    public void setNameserver(List<HostDecorator> hosts) {
        this.hosts = hosts;
    }

    public void setNameserver(HostDecorator host) {
        this.hosts.add(host);
    }
}
