package org.iana.rzm.trans.change;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.ObjectInstantiator;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.HostManager;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HostInstantiator implements ObjectInstantiator {

    private HostManager hostManager;

    public HostInstantiator(HostManager hostManager) {
        CheckTool.checkNull(hostManager, "host manager");
        this.hostManager = hostManager;
    }

    public Object instantiate(Change change) {
        ObjectChange objectChange = (ObjectChange)change;
        String hostName = objectChange.getId();
        Host host = hostManager.get(hostName);
        return host != null ? host : new Host(hostName);
    }
}
