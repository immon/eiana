package org.iana.rzm.web.common.technical_check;

import org.iana.commons.ListUtil;
import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 27, 2009
 * Time: 4:47:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhoIsIOExceptionMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder{

    public String build(Element elm, String domain) {
        Element host = XmlUtils.findFirstElementByName(elm, "host");
        List<Element> list = XmlUtils.findElementByName(elm, "value");

        return new MessageUtil().getWhoIsIOExceptionMessage(
                host.getValue(),
                ListUtil.convert(list, new ListUtil.ObjectConverter<String, Element>() {
                    public String convert(Element element) {
                        return element.getValue();
                    }
                }, false)
        );
    }
}
