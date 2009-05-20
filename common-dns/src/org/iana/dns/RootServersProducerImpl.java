package org.iana.dns;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.dns.obj.DNSHostImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class RootServersProducerImpl implements RootServersProducer {

    Config config;

    List<DNSHost> rootServers;

    public RootServersProducerImpl(ParameterManager parameterManager) {
        if (parameterManager == null)
            throw new IllegalArgumentException("parameter manager is null");

        config = new OwnedConfig(parameterManager);
    }

    public List<DNSHost> getRootServers() throws ConfigException {

        Set<String> rootServerNames = config.getParameterSet(rootServerParamNames);

        if (rootServerNames == null)
            return rootServers;

        List<DNSHost> dnsHostsList = new ArrayList<DNSHost>(rootServerNames.size());

        for (String rootServer : rootServerNames) {
            Set<String> serverAddresses = config.getParameterSet(rootServer);
            DNSHostImpl dnsHost = new DNSHostImpl(rootServer);
            dnsHost.setIPAddressesAsStrings(serverAddresses);

            dnsHostsList.add(dnsHost);
        }

        return dnsHostsList;
    }

    public void setRootServers(List<DNSHost> rootServers) {
        this.rootServers = rootServers;
    }
}
