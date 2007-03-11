package org.iana.rzm.facade.user;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class AdminRoleVO extends RoleVO {

    public enum AdminType implements RoleVO.Type {
        IANA,
        GOV_OVERSIGHT,
        ZONE_PUBLISHER
    }


    public AdminRoleVO() {
    }

    public AdminRoleVO(AdminType type) {
        super(type);
    }

    public AdminType getType() {
        return (AdminType) super.getType();
    }

    public void setType(AdminType type) {
        super.setType(type);
    }

    public boolean isAdmin() {
        return true;
    }
}
