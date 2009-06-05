package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;


public class NotEnoughNameServersMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder{

    public String build(Element elm, String domain) {

        Element received = XmlUtils.findFirstElementByName(elm, "received");
        Element actualNameServers = XmlUtils.findFirstElementByName(received, "value");
        Element expected = XmlUtils.findFirstElementByName(elm, "expected");
        Element expectedNameServers = XmlUtils.findFirstElementByName(expected, "value");
        return new MessageUtil().getNotEnoughNameServersMessage(
                expectedNameServers.getValue(),
                actualNameServers.getValue());
    }
}
