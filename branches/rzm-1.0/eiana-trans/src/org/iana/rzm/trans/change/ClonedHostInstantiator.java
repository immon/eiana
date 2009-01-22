package org.iana.rzm.trans.change;

import org.iana.rzm.domain.HostManager;
import org.iana.rzm.domain.Host;
import org.iana.objectdiff.Change;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ClonedHostInstantiator extends HostInstantiator {

    public ClonedHostInstantiator(HostManager hostManager) {
        super(hostManager);
    }

    public Host instantiate(Change change) {
        return cloneHost(super.instantiate(change));
    }

    public Host get(String id) {
        return cloneHost(super.get(id));
    }

    private Host cloneHost(Host host) {
        return host == null ? null : host.clone();
    }
}
