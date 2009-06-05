package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;


public class MinimumNetworkDiversityMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {
    private static final String AS = "value";

    @SuppressWarnings("unchecked")
    public String build(Element elm, String domain) {
        Element as = XmlUtils.findFirstElementByName(elm, AS);
        if(as == null){
            return null;
        }

        return new MessageUtil().getAllNameServersFromSameASNumnerMessage(as.getValue());
    }
    
}
