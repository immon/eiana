package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.IPAddressVO;

public class IPAddressVOWrapper extends ValueObject {

    public static enum Type {

        IPv4(IPAddressVO.Type.IPv4), IPv6(IPAddressVO.Type.IPv6);

        private IPAddressVO.Type type;

        Type(IPAddressVO.Type type) {
            this.type = type;
        }

        public IPAddressVO.Type asVOType() {
            return type;
        }

        public static Type from(IPAddressVO.Type type) {
            if (type.equals(IPAddressVO.Type.IPv4)) {
                return IPv4;
            }

            return IPv6;
        }
    }

    private IPAddressVO vo;

    public IPAddressVOWrapper(IPAddressVO vo) {
        this.vo = vo;
    }

    public IPAddressVOWrapper(String address, Type type) {
        this(new IPAddressVO());
        vo.setAddress(address);
        vo.setType(type.asVOType());
    }

    public String getAddress() {
        return vo.getAddress();
    }

    public Type getType() {
        return Type.from(vo.getType());
    }

    public void setAddress(String address) {
        vo.setAddress(address);
    }

    public void setType(Type type) {
        vo.setType(type.asVOType());
    }
}
