package org.iana.rzm.web.util;

import org.iana.rzm.web.model.IPAddressVOWrapper;
import org.iana.rzm.web.model.NameServerVOWrapper;
import org.iana.rzm.web.model.NameServerValue;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class WebUtil {
    private static final String IE = "IE";
    private static final String NS6 = "NS6";
    private static final String NS4 = "NS4";
    private static final String FIREFOX = "FIREFOX";
    private static final String CAMINO = "CAMINO";
    private static final String SAFARI = "SAFARI";

    public static List<IPAddressVOWrapper> toVos(String ips) {
        if (ips == null || ips.length() == 0) {
            return new ArrayList<IPAddressVOWrapper>();
        }

        String[] ipParts = ips.trim().split(" ");
        List<IPAddressVOWrapper> ipList = new ArrayList<IPAddressVOWrapper>();
        for (String ip : ipParts) {
            IPAddressVOWrapper ipAddress = new IPAddressVOWrapper(ip, getType(ip));
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
        List<NameServerValue> result = new ArrayList<NameServerValue>();
        for (NameServerVOWrapper nameServer : nameServers) {
            result.add(new NameServerValue(nameServer));
        }

        return result;
    }

    public static String getBrowserName(HttpServletRequest request) {

        String useragent = request.getHeader("User-Agent");
        String user = useragent.toLowerCase();
        if (user.indexOf("msie") != -1) {
            return IE;
        } else if (user.indexOf("netscape6") != -1) {
            return NS6;
        } else if ((user.indexOf("firefox")) != -1) {
            return FIREFOX;
        } else if ((user.indexOf("camino")) != -1) {
            return CAMINO;
        } else if ((user.indexOf("safari")) != -1) {
            return SAFARI;
        } else if (user.indexOf("mozilla") != -1) {
            return NS4;
        }

        return "";
    }

    public static boolean isNavBrowser(HttpServletRequest request) {
        String broeser = getBrowserName(request);
        return !broeser.equals(IE);
    }

    public static boolean isIEBrowser(HttpServletRequest request) {
        String broeser = getBrowserName(request);
        return broeser.equals(IE);
    }


}
