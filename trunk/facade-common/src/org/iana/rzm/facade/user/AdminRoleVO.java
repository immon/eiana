package org.iana.rzm.facade.user;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class AdminRoleVO extends RoleVO {

    public enum AdminType implements RoleVO.Type {
        ROOT,
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

    public boolean isRoot() {
        return AdminType.ROOT.equals(getType());
    }

    public boolean isIANAAdmin() {
        return AdminType.IANA.equals(getType());
    }

    public boolean isUSDoCAdmin() {
        return AdminType.GOV_OVERSIGHT.equals(getType());
    }

    public boolean isZonePublisherAdmin() {
        return AdminType.ZONE_PUBLISHER.equals(getType());
    }

    public boolean equals(Object object) {
        return object instanceof AdminRoleVO && super.equals(object);
    }
}
