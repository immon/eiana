package org.iana.rzm.domain;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class NameServersDecorator {

    List<Host> hosts;

    public NameServersDecorator(List<Host> hosts) {
        this.hosts = hosts;
    }

    public List<Host> getNameServers() {
        return hosts;
    }

}