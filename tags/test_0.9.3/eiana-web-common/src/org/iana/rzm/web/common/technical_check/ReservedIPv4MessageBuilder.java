package org.iana.rzm.web.common.technical_check;

import org.iana.commons.ListUtil;
import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

import java.util.List;

public class ReservedIPv4MessageBuilder implements DNSTechnicalCheckErrorMessageBuilder{

    public String build(Element elm, String domain) {
        Element host = XmlUtils.findFirstElementByName(elm, "host");
        List<Element> list = XmlUtils.findElementByName(elm, "value");
        List<String> ips = ListUtil.convert(list, new ListUtil.ObjectConverter<String, Element>() {
            public String convert(Element element) {
                return element.getValue();

            }
        }, false);

        return new MessageUtil().getReservedIPv4Message(host.getValue(), ips);       
    }
}
