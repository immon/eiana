package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.jdom.Element;


public class NameServerUnreachableMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {

    public String build(Element elm, String domain) {
        String host = new XmlHostParser().parseHosts(elm);

        if(host == null){
            return null;
        }

        return getErrorMessage(host);

    }

    protected String getErrorMessage(String host) {
        return new MessageUtil().getNameServerUnreachableMessage(host);
    }
}
