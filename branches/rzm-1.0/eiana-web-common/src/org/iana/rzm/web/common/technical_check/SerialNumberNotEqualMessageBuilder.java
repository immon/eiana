package org.iana.rzm.web.common.technical_check;

import org.iana.commons.ListUtil;
import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.utils.XmlUtils;
import org.jdom.Element;

import java.util.List;


public class SerialNumberNotEqualMessageBuilder implements DNSTechnicalCheckErrorMessageBuilder{

    public String build(Element elm, String domain) {
        List<Element> list = XmlUtils.findElementByName(elm, "value");
        if(list == null || list.size() == 0){
            return null;
        }

        return new MessageUtil().getSerialNumberNotEqualMessage(
                ListUtil.convert(list, new ListUtil.ObjectConverter<String, Element>() {
                    public String convert(Element element) {
                        return element.getValue();
                    }
                }, false ));
        
    }
}
