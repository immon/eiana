package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

class XmlHostParser {

    private static final String HOST_ELEMENT = "host";

    public String parseHosts(Element elm) {
        Element host = XmlUtils.findFirstElementByName(elm, HOST_ELEMENT);
        return host == null ? null : host.getValue();
    }
}
