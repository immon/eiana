package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class DomainVOWrapper extends ValueObject implements PaginatedEntity {

    public enum Status {
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

        public IDomainVO.Status toVoStatus() {
            IDomainVO.Status[] statuses = IDomainVO.Status.values();
            return statuses[this.ordinal()];
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

    public String getDescription(){
        return vo.getDescription();
    }

    public void setDescription(String description){
        vo.setDescription(description);
    }

    public boolean isSendEmail(){
        return vo.isEnableEmails();
    }

    public void setSendEmail(boolean value){
        vo.setEnableEmails(value);
    }

    public String getSpecialInstructions(){
        return vo.getSpecialInstructions();
    }

    public void setSpecialInstructions(String specialInstructions){
        vo.setSpecialInstructions(specialInstructions);
    }

    public String getRegistryUrl(){
        return vo.getRegistryUrl();
    }

    public void setRegistryUrl(String url){
        vo.setRegistryUrl(url);
    }

    public String getWhoisServer(){
       return vo.getWhoisServer();
    }

    public void setWhoisServer(String server){
        vo.setWhoisServer(server);
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
        return Arrays.asList(getAdminContact());
    }
    
    public ContactVOWrapper getAdminContact(){
        return new ContactVOWrapper(vo.getAdminContact(), SystemRoleVOWrapper.SystemType.AC);
    }

    public List<ContactVOWrapper> getTechnicalContacts() {
        return Arrays.asList(getTechnicalContact());
    }

    public ContactVOWrapper getTechnicalContact(){
        return new ContactVOWrapper(vo.getTechContact(), SystemRoleVOWrapper.SystemType.TC);
    }

    public List<NameServerVOWrapper> getNameServers() {
        List<HostVO> list = vo.getNameServers();
        List<NameServerVOWrapper> wrappers = new ArrayList<NameServerVOWrapper>();
        for (HostVO hostVO : list) {
            wrappers.add(new NameServerVOWrapper(hostVO));
        }

        return wrappers;
    }

    public String getStatusAsString(){
        return getStatus().getDisplayName();
    }

    public Status getStatus() {
        return Status.form(vo.getStatus());
    }

    public void setStatus(Status s){
        vo.setStatus(s.toVoStatus());
    }

    public String getIanaCode(){
        return vo.getIanaCode();
    }

    public void setIanaCode(String code){
        vo.setIanaCode(code);
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

    public boolean isNew(){
        return Status.form(vo.getStatus()).equals(Status.NEW);
    }



    public String getType(){
        return vo.getType();
    }

    public void setType(String type){
        vo.setType(type);
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
