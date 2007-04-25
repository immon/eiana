package org.iana.rzm.facade.system.domain;

import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.common.TrackDataVO;

import java.util.Set;
import java.sql.Timestamp;
import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HostVO implements Trackable, Serializable {

    private String name;
    private Set<IPAddressVO> addresses;
    private boolean shared;

    private Long objId;
    private TrackDataVO trackData = new TrackDataVO();
    
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

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public void setCreated(Timestamp created) {
        trackData.setCreated(created);
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public void setModified(Timestamp modified) {
        trackData.setModified(modified);
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public void setCreatedBy(String createdBy) {
        trackData.setCreatedBy(createdBy);
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    public void setModifiedBy(String modifiedBy) {
        trackData.setModifiedBy(modifiedBy);
    }
}