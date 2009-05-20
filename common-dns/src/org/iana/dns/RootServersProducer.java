package org.iana.dns;

import org.iana.config.impl.ConfigException;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface RootServersProducer {

    static final String rootServerParamNames = "rootServerNames";
    
    List<DNSHost> getRootServers() throws ConfigException;
}
