package org.iana.rzm.web.model;

import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.user.*;

public class AdminRoleVOWrapper extends RoleVOWrapper {

    public enum AdminType implements Type {
        IANA("IANA", "IANA"),
        DoC( "GOV_OVERSIGHT", "DoC"),
        ZONE_PUBLISHER("ZONE_PUBLISHER", "Zone Publisher");

        private String displayName;
        private String serverName;

        AdminType(String displayName) {
            this(displayName, displayName);
        }

        AdminType(String serverName, String displayName) {
            this.displayName = displayName;
            this.serverName = serverName;
        }

        public String toString() {
            return displayName;
        }

        public String serverName(){
            return serverName;
        }

        public static AdminType fromString(String type) {
            CheckTool.checkNull(type, "Type");
            if (type.equals(IANA.serverName)) {
                return IANA;
            } else if (type.equals(DoC.serverName)) {
                return DoC;
            }else if(type.equals(ZONE_PUBLISHER.serverName)){
                return ZONE_PUBLISHER;
            }

            throw new IllegalArgumentException(type);
        }

        public static AdminType fromType(String type) {
            CheckTool.checkNull(type, "Type");
            if (type.equals(IANA.displayName)) {
                return IANA;
            } else if (type.equals(DoC.displayName)) {
                return DoC;
            }else if(type.equals(ZONE_PUBLISHER.displayName)){
                return ZONE_PUBLISHER;
            }

            throw new IllegalArgumentException(type);
        }


        public static AdminType fromVOType(AdminRoleVO.Type type) {
            CheckTool.checkNull(type, "Type");

            if (type.equals(AdminRoleVO.AdminType.IANA)) {
                return IANA;
            } else if (type.equals(AdminRoleVO.AdminType.GOV_OVERSIGHT)) {
                return DoC;
            } else if (type.equals(AdminRoleVO.AdminType.ZONE_PUBLISHER)) {
                return ZONE_PUBLISHER;
            }
            throw new IllegalArgumentException(type.getClass().getName());
        }

        public AdminRoleVO.Type getServerType(){
            return AdminRoleVO.AdminType.values()[ordinal()];
        }
    }



    public AdminRoleVOWrapper(AdminRoleVOWrapper.AdminType type) {
        super(new AdminRoleVO());
        getVo().setType(type.getServerType());
    }

    public AdminRoleVOWrapper(AdminRoleVO vo) {
        super(vo);
    }

    public Type getType() {
        return AdminType.fromVOType(getVo().getType());
    }
}
