package org.iana.rzm.facade.system;

import org.iana.rzm.common.Name;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HostVO {

    private String name;
    private Set<IPAddressVO> addresses;
    private boolean shared;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<IPAddressVO> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<IPAddressVO> addresses) {
        this.addresses = addresses;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
