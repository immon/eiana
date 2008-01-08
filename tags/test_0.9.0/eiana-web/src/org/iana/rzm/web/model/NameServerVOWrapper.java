package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IPAddressVO;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class NameServerVOWrapper extends ValueObject {

    private HostVO vo;

    public NameServerVOWrapper(HostVO vo) {
        this.vo = vo;
    }

    public long getId() {
        return vo.getObjId();
    }

    public NameServerVOWrapper setId(long id) {
        vo.setObjId(id);
        return this;
    }

    public String getName() {
        return vo.getName();
    }

    public NameServerVOWrapper setName(String name) {
        vo.setName(name);
        return this;
    }

    public boolean isShared() {
        return vo.isShared();
    }

    public NameServerVOWrapper setShared(boolean shared) {
        vo.setShared(shared);
        return this;
    }

    public Timestamp getCreated() {
        return vo.getCreated();
    }

    public NameServerVOWrapper setCreated(Timestamp created) {
        vo.setCreated(created);
        return this;
    }

    public Timestamp getModified() {
        return vo.getModified();
    }

    public NameServerVOWrapper setModified(Timestamp modified) {
        vo.setModified(modified);
        return this;
    }

    public Set<IPAddressVOWrapper> getIps() {
        Set<IPAddressVO> addresses = vo.getAddresses();
        Set<IPAddressVOWrapper> result = new HashSet<IPAddressVOWrapper>();
        for (IPAddressVO address : addresses) {
            result.add(new IPAddressVOWrapper(address));
        }
        return result;
    }

    public void addIPAddress(IPAddressVOWrapper ip) {
        Set<IPAddressVO> addresses = vo.getAddresses();
        if(addresses == null){
            addresses = new HashSet<IPAddressVO>();
        }

        IPAddressVO o = new IPAddressVO();
        o.setAddress(ip.getAddress());
        o.setType(ip.getType().asVOType());
        addresses.add(o);
        vo.setAddresses(addresses);
    }

    public HostVO getVO() {
        return vo;
    }
}
