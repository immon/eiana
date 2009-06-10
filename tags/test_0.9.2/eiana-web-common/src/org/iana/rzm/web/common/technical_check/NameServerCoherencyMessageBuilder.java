package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;


public class NameServerCoherencyMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {
    private static final String HOST_ELEMENT = "host";
    private static final String RECEIVED_ELEMENT = "received";
    private static final String EXPECTED_ELEMENT = "expected";
    private static final String VALUE_ELEMENT = "value";

    public String build(Element elm, String domain) {
        Element host = elm.getChild(HOST_ELEMENT);
        if(host == null){
            return null;
        }

        Element received = elm.getChild(RECEIVED_ELEMENT);
        if(received == null){
            return null;
        }

        Element expected = elm.getChild(EXPECTED_ELEMENT);
        if(expected == null){
            return null;
        }

        List<String> recivedNameServers = parseNameServers(received);
        List<String> expectedNameServers = parseNameServers(expected);

        return new MessageUtil().getNameServerCoherencyMessage(recivedNameServers, expectedNameServers, host.getValue());


    }

    private List<String> parseNameServers(Element element) {
        List<Element> list = XmlUtils.findElementByName(element, VALUE_ELEMENT);
        List<String>nameservers = new ArrayList<String>();
        for (Element nameserver : list) {
            nameservers.add(nameserver.getValue());
        }

        return nameservers;
    }
}
