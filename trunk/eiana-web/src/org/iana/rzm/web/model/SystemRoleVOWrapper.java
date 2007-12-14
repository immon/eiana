package org.iana.rzm.web.model;

import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.user.*;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: Apr 6, 2007
 * Time: 10:50:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SystemRoleVOWrapper extends RoleVOWrapper {

    public static final String ADMINISTRATIVE_CONTACT = "Administrative Contact";
    public static final String THECHNICAL_CONTACT = "Technical Contact";
    public static final String SUPPORTING_ORGANIZATION = "Sponsoring Organization";

    public enum SystemType implements Type {
        AC("Administrative", ADMINISTRATIVE_CONTACT), TC("Technical", THECHNICAL_CONTACT), SO(SUPPORTING_ORGANIZATION);

        private String displayName;
        private String serverName;

        SystemType(String displayName) {
            this(displayName, displayName);
        }

        SystemType(String serverName, String displayName) {
            this.displayName = displayName;
            this.serverName = serverName;
        }

        public String toString() {
            return displayName;
        }

        public String serverName() {
            return serverName;
        }

        public static SystemType fromString(String type) {
            CheckTool.checkNull(type, "Type");
            if (type.equals(AC.serverName)) {
                return AC;
            } else if (type.equals(TC.serverName)) {
                return TC;
            } else if (type.equals(SO.serverName)) {
                return SO;
            }

            throw new IllegalArgumentException(type);
        }

        public static SystemType fromType(String type) {
            CheckTool.checkNull(type, "Type");
            if (type.equals(AC.displayName)) {
                return AC;
            } else if (type.equals(TC.displayName)) {
                return TC;
            } else if (type.equals(SO.displayName)) {
                return SO;
            }

            throw new IllegalArgumentException(type);
        }


        public static SystemType fromVOType(SystemRoleVO.Type type) {
            CheckTool.checkNull(type, "Type");
            if (type.equals(SystemRoleVO.SystemType.AC)) {
                return AC;
            } else if (type.equals(SystemRoleVO.SystemType.TC)) {
                return TC;
            } else if (type.equals(SystemRoleVO.SystemType.SO)) {
                return SO;
            }

            throw new IllegalArgumentException(type.toString());
        }

        public SystemRoleVO.Type getServerType() {
            return SystemRoleVO.SystemType.values()[ordinal()];
        }
    }

    public SystemRoleVOWrapper(SystemRoleVO.Type type) {
        super(new SystemRoleVO());
        getVo().setType(type);
    }


    public SystemRoleVOWrapper(SystemRoleVO vo) {
        super(vo);
    }

    public void setMustAccept(boolean mustAccept) {
        getVo().setMustAccept(mustAccept);
    }

    public void setAcceptFrom(boolean acceptFrom) {
        getVo().setAcceptFrom(acceptFrom);
    }

    public void setNotify(boolean notify) {
        getVo().setNotify(notify);
    }

    public boolean isAcceptFrom() {
        return getVo().isAcceptFrom();
    }

    public boolean isNotify() {
        return getVo().isNotify();
    }

    public boolean isMustAccept() {
        return getVo().isMustAccept();
    }


    protected SystemRoleVO getVo() {
        return (SystemRoleVO) super.getVo();
    }

    public long getId() {
        return getVo().getObjId();
    }

    public void setId(long roleId) {
        if (roleId == 0) {
            getVo().setObjId(null);
        }
        getVo().setObjId(roleId);
    }

    public void setName(String domainName) {
        getVo().setName(domainName);
    }

    public String getDomainName() {
        return getVo().getName();
    }

    public SystemType getType() {
        return SystemType.fromVOType(getVo().getType());
    }

    public String getTypeAsString() {
        return getType().toString();
    }

    public void setDomainAccess(boolean value) {
        getVo().setAccessToDomain(value);
    }
}
