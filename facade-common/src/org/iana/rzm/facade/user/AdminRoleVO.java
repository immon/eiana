package org.iana.rzm.facade.user;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AdminRoleVO extends RoleVO {

    public enum AdminType implements RoleVO.Type {
        IANA,
        GOV_OVERSIGHT,
        ZONE_PUBLISHER
    }

    public AdminType getType() {
        return (AdminType) super.getType();
    }

    public void setType(AdminType type) {
        super.setType(type);
    }
}
