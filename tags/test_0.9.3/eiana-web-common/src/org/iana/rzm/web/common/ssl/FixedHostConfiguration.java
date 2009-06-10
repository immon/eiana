package org.iana.rzm.web.common.ssl;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpHost;

/**
 * @author Piotr Tkaczyk
 */
public class FixedHostConfiguration extends HostConfiguration {

    public FixedHostConfiguration(HttpHost host) {
        super();
        setHost(host);
    }
}
