package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.facade.system.domain.IPAddressVO;
import org.iana.rzm.web.util.WebUtil;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;


public class NameServerValue implements Serializable {

    private long id;
    private String hostName;
    private List<IPAddressVOWrapper> ips = new ArrayList<IPAddressVOWrapper>();
    private String status;
    private boolean shared;
    private Timestamp created;
    private Timestamp modified;

    public static final String DELETE = "delete";
    public static final String NEW = "new";
    public static final String MODIFIED = "modified";
    public static final String DEFAULT = "default";


    public NameServerValue(String hostname, String ips) {
        this.hostName = hostname;
        this.ips.addAll(WebUtil.toVos(ips));
        shared = false;
        status = NEW;
    }

    public NameServerValue(NameServerVOWrapper nameServer) {
        this.id = nameServer.getId();
        this.hostName = nameServer.getName();
        Set<IPAddressVOWrapper> ips = nameServer.getIps();
        addAll(ips);
        this.shared = nameServer.isShared();
        created = nameServer.getCreated();
        modified = nameServer.getModified();
        status = DEFAULT;
    }


    public NameServerValue setStatus(String status) {
        this.status = status;
        return this;
    }

    public boolean isNewOrModified() {
        return status.equals(NEW) || status.equals(MODIFIED);
    }

    public boolean isDelete() {
        return status.equals(DELETE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIps() {
        return WebUtil.buildIpListAsString(ips);
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public List<IPAddressVOWrapper> getIpsAsList() {
        return ips;
    }

    public Date getLastUpdated() {
        return modified == null ? created : modified;
    }

    public NameServerVOWrapper asNameServer() {
        NameServerVOWrapper result = new NameServerVOWrapper(new HostVO());
        result
            .setId(getId())
            .setName(hostName)
            .setShared(shared)
            .setCreated(created)
            .setModified(modified);

        for (IPAddressVOWrapper ip : ips) {
            result.addIPAddress(ip);
        }

        return result;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NameServerValue that = (NameServerValue) o;

        if (id != that.id) return false;
        if (hostName != null ? !hostName.equals(that.hostName) : that.hostName != null) return false;
        if (ips != null ? !ips.equals(that.ips) : that.ips != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (int) (id ^ (id >>> 32));
        result = 29 * result + (hostName != null ? hostName.hashCode() : 0);
        result = 29 * result + (ips != null ? ips.hashCode() : 0);
        return result;
    }


    public void setIps(String ips) {
        List<IPAddressVOWrapper> modifiedProposals = WebUtil.toVos(ips);

        for (int i = 0; i < modifiedProposals.size() && i < this.ips.size(); i++) {
            IPAddressVOWrapper ipvo = this.ips.get(i);
            IPAddressVOWrapper mipvo = modifiedProposals.get(i);
            ipvo.setAddress(mipvo.getAddress());
            ipvo.setType(mipvo.getType());
        }

        if (modifiedProposals.size() > this.ips.size()) {
            for (int i = this.ips.size(); i < modifiedProposals.size(); i++) {
                IPAddressVOWrapper wrapper = modifiedProposals.get(i);
                this.ips.add(wrapper);
            }
        }

        if (modifiedProposals.size() < this.ips.size()) {
            Iterator<IPAddressVOWrapper> it = this.ips.iterator();
            while (it.hasNext()) {
                IPAddressVOWrapper ip = it.next();
                if (exitsInList(ip, modifiedProposals)) {
                    removeFromList(ip, modifiedProposals);
                } else {
                    it.remove();
                }
            }
        }

    }

    private void removeFromList(IPAddressVOWrapper ip, List<IPAddressVOWrapper> list) {
        Iterator<IPAddressVOWrapper> it = list.iterator();
        while (it.hasNext()) {
            IPAddressVOWrapper proposal = it.next();
            if (proposal.getAddress().equals(ip.getAddress()) && proposal.getType().equals(ip.getType())) {
                it.remove();
            }
        }
    }

    private boolean exitsInList(IPAddressVOWrapper ip, List<IPAddressVOWrapper> list) {
        for (IPAddressVOWrapper proposal : list) {
            if (ip.getAddress().equals(proposal.getAddress())
                    && ip.getType() == proposal.getType()) {
                return true;
            }
        }
        return false;
    }


    private void addAll(Set<IPAddressVOWrapper> ips) {
        for (IPAddressVOWrapper voWrapper : ips) {
            try {
                IPAddressVO vo = new IPAddressVO();
                vo.setAddress(voWrapper.getAddress());
                this.ips.add(new IPAddressVOWrapper(vo));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}

