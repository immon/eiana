package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.web.util.DateUtil;
import org.iana.rzm.web.util.ListUtil;

import java.util.*;

public abstract class DomainVOWrapper extends ValueObject implements PaginatedEntity {

    private enum Status {
        NEW("New"), ACTIVE("Active"), CLOSE("Close");

        private String displayName;

        private Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Status form(IDomainVO.Status status) {
            if (status.equals(IDomainVO.Status.ACTIVE)) {
                return ACTIVE;
            } else if (status.equals(IDomainVO.Status.CLOSED)) {
                return CLOSE;
            } else if (status.equals(IDomainVO.Status.NEW)) {
                return NEW;
            }

            throw new IllegalArgumentException("Unknown status " + status);
        }

    }

    private IDomainVO vo;

    public DomainVOWrapper(IDomainVO vo) {
        this.vo = vo;
    }

    public IDomainVO getDomainVO() {
        return vo;
    }

    public long getId() {
        return vo.getObjId();
    }

    public String getName() {
        return vo.getName();
    }

    public String getNameWithDot(){
        return "." + vo.getName();   
    }

    public String getModified() {
        Date d = vo.getModified();
        return d == null ?
                DateUtil.formatDate(vo.getCreated()) :
                DateUtil.formatDate(d);
    }

    public ContactVOWrapper getSupportingOrganization() {
        return new ContactVOWrapper(vo.getSupportingOrg(), SystemRoleVOWrapper.SystemType.SO);
    }

    public List<ContactVOWrapper> getAdminContacts() {
        ContactVOWrapper wrapper = new ContactVOWrapper(vo.getAdminContact(), SystemRoleVOWrapper.SystemType.AC);
        return Arrays.asList(wrapper);
    }

    public List<ContactVOWrapper> getTechnicalContacts() {
        ContactVOWrapper wrapper = new ContactVOWrapper(vo.getTechContact(), SystemRoleVOWrapper.SystemType.TC);
        return Arrays.asList(wrapper);
    }

    public List<NameServerVOWrapper> getNameServers() {
        List<HostVO> list = vo.getNameServers();
        List<NameServerVOWrapper> wrappers = new ArrayList<NameServerVOWrapper>();
        for (HostVO hostVO : list) {
            wrappers.add(new NameServerVOWrapper(hostVO));
        }

        return wrappers;
    }

    public String getStatus() {
        return Status.form(vo.getStatus()).getDisplayName();
    }

    public abstract List<? extends RoleVOWrapper> getRoles();

    public boolean isOperationPending() {
        return vo.getState().equals(IDomainVO.State.OPERATIONS_PENDING) ||
                vo.getState().equals(IDomainVO.State.THIRD_PARTY_PENDING);
    }

    public void updateContactAttributes(Map<String, String> attributes, String type) {
        ContactVOWrapper contact = findContactByTypeAndId(Long.parseLong(attributes.get(ContactVOWrapper.ID)), type);
        contact.save(attributes);
    }

    public void updateNameServers(List<NameServerVOWrapper> nameServers) {
        List<HostVO> hosts = new ArrayList<HostVO>();
        for (NameServerVOWrapper nameServer : nameServers) {
            hosts.add(nameServer.getVO());
        }

        vo.setNameServers(hosts);
    }

    public ContactVOWrapper getContact(long contactId, String type) {
        return findContactByTypeAndId(contactId, type);
    }

    private ContactVOWrapper findContactByTypeAndId(final Long id, String type) {
        final SystemRoleVOWrapper.SystemType contactType = SystemRoleVOWrapper.SystemType.fromString(type);

        ListUtil.Predicate<ContactVOWrapper> predicate = new ListUtil.Predicate<ContactVOWrapper>() {
            public boolean evaluate(ContactVOWrapper object) {
                return object.getId() == id && contactType.equals(object.getType());
            }
        };

        ContactVOWrapper so = getSupportingOrganization();
        if (contactType.equals(so.getType()) && so.getId() == id) {
            return so;
        } else if (contactType.equals(SystemRoleVOWrapper.SystemType.AC)) {
            return ListUtil.find(getAdminContacts(), predicate);
        } else if (contactType.equals(SystemRoleVOWrapper.SystemType.TC)) {
            return ListUtil.find(getTechnicalContacts(), predicate);
        }
        throw new IllegalArgumentException("Can not find contact with type " + type + " and id = " + id);
    }

}
