package org.iana.rzm.facade.system.trans.vo;

import org.iana.rzm.facade.user.SystemRoleVO;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class ConfirmationVO implements Serializable {
    private SystemRoleVO.SystemType role;
    private boolean confirmed;
    private String contactName;
    private boolean newContact;

    public ConfirmationVO() {
    }

    public ConfirmationVO(SystemRoleVO.SystemType role, boolean confirmed, String contactName, boolean newContact) {
        this.role = role;
        this.confirmed = confirmed;
        this.contactName = contactName;
        this.newContact = newContact;
    }

    public SystemRoleVO.SystemType getRole() {
        return role;
    }

    public void setRole(SystemRoleVO.SystemType role) {
        this.role = role;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isNewContact() {
        return newContact;
    }

    public void setNewContact(boolean newContact) {
        this.newContact = newContact;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfirmationVO that = (ConfirmationVO) o;

        if (confirmed != that.confirmed) return false;
        if (newContact != that.newContact) return false;
        if (contactName != null ? !contactName.equals(that.contactName) : that.contactName != null) return false;
        if (role != that.role) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (role != null ? role.hashCode() : 0);
        result = 31 * result + (confirmed ? 1 : 0);
        result = 31 * result + (contactName != null ? contactName.hashCode() : 0);
        result = 31 * result + (newContact ? 1 : 0);
        return result;
    }
}
