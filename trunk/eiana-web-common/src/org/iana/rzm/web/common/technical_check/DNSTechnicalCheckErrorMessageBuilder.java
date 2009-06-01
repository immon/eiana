package org.iana.rzm.web.common.technical_check;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: May 11, 2009
* Time: 9:31:22 PM
* To change this template use File | Settings | File Templates.
*/
public interface DNSTechnicalCheckErrorMessageBuilder {
    public String build(Element elm, String domain);
}
