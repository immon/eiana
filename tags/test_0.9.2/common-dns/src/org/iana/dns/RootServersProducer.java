package org.iana.dns;

import org.iana.config.impl.ConfigException;
import org.iana.config.Parameter;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface RootServersProducer {

    static final String rootServerParamNames = "rootServerNames";
    
    List<DNSHost> getRootServers() throws ConfigException;

    boolean hasRootServers() throws ConfigException;

    List<DNSHost> getDefaultServers() throws ConfigException;

    List<Parameter> toConfig(List<DNSHost> rootServers) throws ConfigException;

}
