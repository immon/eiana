package org.iana.dns.check;

import org.iana.config.Parameter;
import org.iana.config.impl.ConfigException;
import org.iana.dns.DNSHost;
import org.iana.dns.RootServersProducer;
import org.iana.dns.obj.DNSHostImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class MockRootServersProducer implements RootServersProducer {


    public List<DNSHost> getRootServers() throws ConfigException {
        Map<String, String> rootServers = new HashMap<String, String>();
        rootServers.put("a.root-servers.net", "198.41.0.4");
        rootServers.put("b.root-servers.net", "192.228.79.201");
        rootServers.put("c.root-servers.net", "192.33.4.12");
        rootServers.put("d.root-servers.net", "128.8.10.90");
        rootServers.put("e.root-servers.net", "192.203.230.10");
        rootServers.put("f.root-servers.net", "192.5.5.241");
        rootServers.put("g.root-servers.net", "192.112.36.4");
        rootServers.put("h.root-servers.net", "128.63.2.53");
        rootServers.put("i.root-servers.net", "192.36.148.17");
        rootServers.put("j.root-servers.net", "192.58.128.30");
        rootServers.put("k.root-servers.net", "193.0.14.129");
        rootServers.put("l.root-servers.net", "199.7.83.42");
        rootServers.put("m.root-servers.net", "202.12.27.33");

        List<DNSHost> rootServersAsDNSHost = new ArrayList<DNSHost>();

        for(String name : rootServers.keySet()) {
            DNSHostImpl dnsHostTemp = new DNSHostImpl(name);
            dnsHostTemp.addIPAddress(rootServers.get(name));
            rootServersAsDNSHost.add(dnsHostTemp);
        }

        return rootServersAsDNSHost;
    }


    public List<Parameter> getConfig(List<DNSHost> rootServers) throws ConfigException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean hasRootServers() throws ConfigException {
        return false;
    }

    public List<DNSHost> getDefaultServers() throws ConfigException {
        return null;
    }

    public List<Parameter> toConfig(List<DNSHost> rootServers) throws ConfigException {
        return null;
    }

    public void updateRootServers(List<DNSHost> rootServers) throws ConfigException {
    }
}
