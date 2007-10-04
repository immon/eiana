package org.iana.rzm.facade.system.domain.vo;

import org.iana.rzm.common.Name;
import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.auth.AccessDeniedException;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface IDomainVO extends Trackable {

    public static enum Breakpoint {
        SO_CHANGE_EXT_REVIEW,
        AC_CHANGE_EXT_REVIEW,
        TC_CHANGE_EXT_REVIEW,
        NS_CHANGE_EXT_REVIEW,
        ANY_CHANGE_EXT_REVIEW
    }

    public static enum Status {
        NEW,
        ACTIVE,
        CLOSED
    }

    public static enum State {
        NO_ACTIVITY,
        OPERATIONS_PENDING,
        THIRD_PARTY_PENDING
    }

    public Long getObjId();

    public void setObjId(Long id);
    
    ContactVO getSupportingOrg();

    void setSupportingOrg(ContactVO supportingOrg);

    List<ContactVO> getAdminContacts();

    ContactVO getAdminContact();

    void setAdminContact(ContactVO adminContacts);

    List<ContactVO> getTechContacts();

    ContactVO getTechContact();

    void setTechContact(ContactVO techContacts);

    List<HostVO> getNameServers();

    void setNameServers(List<HostVO> nameServers);

    String getRegistryUrl();

    void setRegistryUrl(String registryUrl);

    Name getWhoisServer();

    void setWhoisServer(Name whoisServer);

    Set<Breakpoint> getBreakpoints() throws AccessDeniedException;

    void setBreakpoints(Set<Breakpoint> breakpoints) throws AccessDeniedException;

    String getSpecialInstructions() throws AccessDeniedException;

    void setSpecialInstructions(String specialInstructions) throws AccessDeniedException;

    Status getStatus();

    void setStatus(DomainVO.Status status);

    State getState();

    void setState(DomainVO.State state);

    String getName();

    Set<RoleVO.Type> getRoles();

    boolean isEnableEmails();

    void setEnableEmails(boolean enableEmails);

    String getDescription();

    void setDescription(String description);

    String getType();

    void setType(String type);
}
