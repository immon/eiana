package org.iana.dns;

import org.iana.config.Config;
import org.iana.config.Parameter;
import org.iana.config.ParameterManager;
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
    ParameterManager parameterManager;

    List<DNSHost> rootServers;

    public RootServersProducerImpl(ParameterManager parameterManager, List<DNSHost> rootServers) {
        if (parameterManager == null)
            throw new IllegalArgumentException("parameter manager is null");
        config = new OwnedConfig(parameterManager);
        this.parameterManager = parameterManager;
        this.rootServers = rootServers;
    }

    public List<DNSHost> getRootServers() throws ConfigException {
        Config rootServerConfig = config.getSubConfig(rootServerParamNames);

        if (rootServerConfig == null)
            return rootServers;

        Set<String> configRootServers = rootServerConfig.getParameterNames();

        if (configRootServers == null || configRootServers.isEmpty())
            return rootServers;

        return toDNSHostList(rootServerConfig);
    }

    public boolean hasRootServers() throws ConfigException {
        List<DNSHost> retRootServers = getRootServers();
        return (retRootServers != null) && (retRootServers.size() > 0);
    }

    public List<DNSHost> getDefaultServers() throws ConfigException {
        return rootServers;
    }

    public List<Parameter> toConfig(List<DNSHost> rootServers) throws ConfigException {
        List<Parameter> ret = new ArrayList<Parameter>();
        if (rootServers != null) {
            for (DNSHost host : rootServers) {
                Parameter param = new SetParameter(rootServerParamNames + "." + host.getName(), host.getIPAddressesAsStrings());
                ret.add(param);
            }
        }
        return ret;
    }

    private List<DNSHost> toDNSHostList(Config rootServerConfig) throws ConfigException {
        List<DNSHost> dnsHostsList = new ArrayList<DNSHost>();
        for (String rootServer : rootServerConfig.getParameterNames()) {
            Set<String> serverAddresses = rootServerConfig.getParameterSet(rootServer);
            DNSHostImpl dnsHost = new DNSHostImpl(rootServer);
            dnsHost.setIPAddressesAsStrings(serverAddresses);
            dnsHostsList.add(dnsHost);
        }

        return dnsHostsList;
    }

    public void updateRootServers(List<DNSHost> rootServers) throws ConfigException {
        if (rootServers != null && !rootServers.isEmpty()) {
            List<Parameter> toUpdate = toConfig(rootServers);
            for (Parameter param : toUpdate) {
                parameterManager.updateParameter(Config.DEFAULT_OWNER, param);    
            }
        }
    }
}
