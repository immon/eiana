package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 13, 2009
 * Time: 10:43:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyIPAddressListMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder {
    private static final String HOST = "value";

    public String build(Element elm, String domain) {
        Element host = XmlUtils.findFirstElementByName(elm, HOST);

        return new MessageUtil().getEmptyIpAddressListMessage(host.getValue());
    }
}
