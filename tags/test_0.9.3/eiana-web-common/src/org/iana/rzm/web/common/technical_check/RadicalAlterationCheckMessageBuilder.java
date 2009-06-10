package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;
import org.jdom.Element;

public class RadicalAlterationCheckMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder{

    public String build(Element elm, String domain) {
        return new MessageUtil().getRadicalAlterationCheckMessage(domain);
    }
}
