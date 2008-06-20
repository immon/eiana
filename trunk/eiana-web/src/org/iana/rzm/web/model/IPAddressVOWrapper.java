package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.vo.*;

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

            if(type == null){
                return IPv4;
            }
            
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
        return normelize(vo.getAddress());
    }

    private String normelize(String address) {
        if (getType().equals(Type.IPv4)) {
            return address;
        }

        String tempAddres = address.toUpperCase();
        return isCompressed(tempAddres) ? uncompress(tempAddres) : tempAddres;
    }

    private static boolean isCompressed(String addr) {
        return addr.indexOf("::") >= 0;
    }

    private static String uncompress(String addr) {
        boolean start = addr.startsWith(":");
        boolean end = addr.endsWith(":");
        int missing = 8 - colons(addr);
        if (start || end) {
            ++missing;
        }
        StringBuffer zeros = new StringBuffer(":");
        while (missing-- > 0) {
            zeros.append("0:");
        }
        if (start) {
            zeros.deleteCharAt(0);
        }
        if (end) {
            zeros.deleteCharAt(zeros.length() - 1);
        }
        return addr.replaceFirst("::", zeros.toString());
    }

    private static int colons(String addr) {
          int ret = 0;
          for (char c : addr.toCharArray()) {
              if (c == ':') ++ret;
          }
          return ret;
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
