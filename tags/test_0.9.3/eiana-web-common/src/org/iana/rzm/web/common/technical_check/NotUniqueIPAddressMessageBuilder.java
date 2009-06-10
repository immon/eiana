package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;


public class NotUniqueIPAddressMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {

    public String build(Element elm, String domain) {
        List<Element> list = XmlUtils.findElementByName(elm, "value");
        List<String>hosts = new ArrayList<String>(list.size());
        for (Element element : list) {
            hosts.add(element.getValue());
        }
        return new MessageUtil().NotUniqueIPAddressMessage(hosts);
    }
}
