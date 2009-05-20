package org.iana.dns;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.Parameter;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.config.impl.SetParameter;
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
        Config rootServerConfig = config.getSubConfig(rootServerParamNames);
        if (rootServerConfig == null)
            throw new IllegalArgumentException("root servers subconfig is null");

        List<DNSHost> dnsHostsList = new ArrayList<DNSHost>();
        for (String rootServer : rootServerConfig.getParameterNames()) {
            Set<String> serverAddresses = config.getParameterSet(rootServer);
            DNSHostImpl dnsHost = new DNSHostImpl(rootServer);
            dnsHost.setIPAddressesAsStrings(serverAddresses);
            dnsHostsList.add(dnsHost);
        }
        return dnsHostsList;
    }

    public List<Parameter> getConfig(List<DNSHost> rootServers) throws ConfigException {
        List<Parameter> ret = new ArrayList<Parameter>();
        for (DNSHost host : rootServers) {
            Parameter param = new SetParameter(rootServerParamNames + "." + host.getName(), host.getIPAddressesAsStrings());
            ret.add(param);
        }
        return ret;
    }

}
