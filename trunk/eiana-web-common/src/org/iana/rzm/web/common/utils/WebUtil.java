package org.iana.rzm.web.common.utils;

import org.iana.commons.ListUtil;
import org.iana.rzm.web.common.model.IPAddressVOWrapper;
import org.iana.rzm.web.common.model.NameServerVOWrapper;
import org.iana.rzm.web.common.model.NameServerValue;

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

    public static List<NameServerVOWrapper> convertToVos(List<NameServerValue> list) {

        List<NameServerVOWrapper> result = new ArrayList<NameServerVOWrapper>(list.size());

        for (NameServerValue value : list) {
            result.add(value.asNameServer());
        }

        return result;
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

        if (originals == null) {
            originals = new ArrayList<NameServerVOWrapper>();
        }

        for (NameServerVOWrapper wrapper : originals) {
            NameServerVOWrapper currentVO = findNameServer(wrapper, current);
            NameServerValue nameServerValue = new NameServerValue(currentVO == null ? wrapper : currentVO);
            if (currentVO == null) {
                nameServerValue.setStatus(NameServerValue.DELETE);
            } else {
                NameServerValue temp = new NameServerValue(wrapper);
                String status = temp.equals(nameServerValue) ? NameServerValue.DEFAULT : NameServerValue.MODIFIED;
                nameServerValue.setStatus(status);
                if (status.equals(NameServerValue.DEFAULT)) {
                    nameServerValue.setShared(temp.isShared());
                }
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

    public static boolean isModefied(List<NameServerVOWrapper> oldList, List<NameServerVOWrapper> currentList) {

        if (oldList == null && currentList == null) {
            return false;
        }

        if (oldList == null || currentList == null) {
            return true;
        }

        if (oldList.size() != currentList.size()) {
            return true;
        }

        List<NameServerValue> newList = WebUtil.buildNameServerList(oldList, currentList);
        for (NameServerValue nameServerValue : newList) {
            if (nameServerValue.isNewOrModified() || nameServerValue.isDelete()) {
                return true;
            }
        }

        return false;
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


    public static int getServerPort(int defaultPort) {

        if (Boolean.getBoolean("org.iana.web.debug-enabled")) {
            return defaultPort;
        }

        if (Boolean.getBoolean("org.iana.web.use.default-port")) {
            return defaultPort;
        }

        return 80;
    }

    static NameServerVOWrapper findNameServer(final NameServerVOWrapper wrapper, List<NameServerVOWrapper> list) {
        NameServerVOWrapper nameServer = ListUtil.find(list, new ListUtil.Predicate<NameServerVOWrapper>() {
            public boolean evaluate(NameServerVOWrapper object) {
                return object.getId() == wrapper.getId();
            }
        });

        if (nameServer == null) {
            return ListUtil.find(list, new ListUtil.Predicate<NameServerVOWrapper>() {
                public boolean evaluate(NameServerVOWrapper object) {
                    return object.getName().equals(wrapper.getName()) && object.getIps().equals(wrapper.getIps());
                }
            });
        }
        return nameServer;

    }


    static IPAddressVOWrapper.Type getType(String ip) {
        return ip.indexOf(":") > -1 ? IPAddressVOWrapper.Type.IPv6 : IPAddressVOWrapper.Type.IPv4;
    }


}
