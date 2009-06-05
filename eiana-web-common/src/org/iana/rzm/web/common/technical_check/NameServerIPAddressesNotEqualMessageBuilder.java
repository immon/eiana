package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.jdom.Element;

import java.util.List;

public class NameServerIPAddressesNotEqualMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {

    public String build(Element elm, String domain) {

        ExpectedReceivedHostXmlParser parser = new ExpectedReceivedHostXmlParser(elm);
        String host = parser.getHost();
        List<String> recivedNameServers = parser.getReceived();
        List<String> expectedNameServers = parser.getExpected();

        if(host == null || recivedNameServers == null || recivedNameServers.size() == 0
                || expectedNameServers == null || expectedNameServers.size() == 0){
            return null;
        }
        
        return new MessageUtil().getNameServerIPAddressesNotEqualMessage(host, recivedNameServers, expectedNameServers);
    }


}
