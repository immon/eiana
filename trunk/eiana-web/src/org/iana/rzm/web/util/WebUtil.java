package org.iana.rzm.web.util;

import org.iana.rzm.web.model.*;

import javax.servlet.http.*;
import java.util.*;


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

    public static List<NameServerValue> buildNameServerList(List<NameServerVOWrapper> originals, List<NameServerVOWrapper> current) {
        List<NameServerValue> all = new ArrayList<NameServerValue>();

        if(originals == null || originals.size() == 0){
            return all;
        }
        
        for (NameServerVOWrapper wrapper : originals) {
            NameServerVOWrapper currentVO = findNameServer(wrapper.getId(), current);
            NameServerValue nameServerValue = new NameServerValue(currentVO == null ? wrapper : currentVO);
            if (currentVO == null) {
                nameServerValue.setStatus(NameServerValue.DELETE);
            } else {
                String status = currentVO.equals(wrapper) ? NameServerValue.DEFAULT : NameServerValue.MODIFIED;
                nameServerValue.setStatus(status);
                current.remove(currentVO);
            }
            all.add(nameServerValue);
        }
        current.removeAll(originals);
        for (NameServerVOWrapper wrapper : current) {
            all.add(new NameServerValue(wrapper).setStatus(NameServerValue.NEW));
        }
        return all;
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

    private static NameServerVOWrapper findNameServer(final long id, List<NameServerVOWrapper> list) {
        return ListUtil.find(list, new ListUtil.Predicate<NameServerVOWrapper>() {
            public boolean evaluate(NameServerVOWrapper object) {
                return object.getId() == id;
            }
        });

    }


}
