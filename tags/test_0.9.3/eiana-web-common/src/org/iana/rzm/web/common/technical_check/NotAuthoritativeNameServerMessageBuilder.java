package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.jdom.Element;

public class NotAuthoritativeNameServerMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {

    public String build(Element elm, String domain) {
        String host = new XmlHostParser().parseHosts(elm);

        if(host == null){
            return null;
        }

        return new MessageUtil().getNotAuthoritativeNameServerMessage(host, domain);
    }
}
