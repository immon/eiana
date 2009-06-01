package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

public class MaximumPayloadSizeMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {

    public String build(Element elm, String domain) {
        Element received = XmlUtils.findFirstElementByName(elm, "received");
        if(received == null){
            return null;
        }

        String receivedPayload = received.getChild("value").getValue();
        Element expected = XmlUtils.findFirstElementByName(elm, "expected");
        if(expected == null){
            return null;
        }

        String expectedPayload = expected.getChild("value").getValue();
        return new MessageUtil().getMaximumPayloadSizeMessage(receivedPayload, expectedPayload);

    }
}
