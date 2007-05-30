package org.iana.rzm.web.util;

import org.iana.rzm.web.model.IPAddressVOWrapper;
import org.iana.rzm.web.model.NameServerVOWrapper;
import org.iana.rzm.web.model.NameServerValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class WebUtil {
    public static List<IPAddressVOWrapper> toVos(String ips) {
        if (ips == null || ips.length() == 0) {
            return new ArrayList<IPAddressVOWrapper>();
        }

        String[]ipParts = ips.trim().split(" ");
        List<IPAddressVOWrapper> ipList = new ArrayList<IPAddressVOWrapper>();
        for (String ip : ipParts) {
            IPAddressVOWrapper ipAddress = new IPAddressVOWrapper(ip,getType(ip));
            ipList.add(ipAddress);
        }

        return ipList;
    }

    private static IPAddressVOWrapper.Type getType(String ip) {
        return ip.indexOf(":") > -1 ? IPAddressVOWrapper.Type.IPv6 : IPAddressVOWrapper.Type.IPv4;
    }

    public static String buildIpListAsString(List<IPAddressVOWrapper> ips) {
         StringBuilder builder = new StringBuilder();

        Iterator<IPAddressVOWrapper> iterator = ips.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            builder.append(iterator.next().getAddress());
            if (i < ips.size() - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public static List<NameServerValue> convert(List<NameServerVOWrapper> nameServers) {
        List<NameServerValue>result = new ArrayList<NameServerValue>();
        for (NameServerVOWrapper nameServer : nameServers) {
            result.add(new NameServerValue(nameServer));
        }
        
        return result;
    }
}
