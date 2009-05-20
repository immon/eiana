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

    List<Parameter> getConfig(List<DNSHost> rootServers) throws ConfigException;

}
