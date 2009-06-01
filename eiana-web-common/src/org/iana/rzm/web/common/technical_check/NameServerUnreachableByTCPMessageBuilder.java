package org.iana.rzm.web.common.technical_check;

import org.iana.rzm.web.common.MessageUtil;

public class NameServerUnreachableByTCPMessageBuilder extends NameServerUnreachableMessageBuilder{

    protected String getErrorMessage(String host) {
        return new MessageUtil().getNameServerUnreachableByTCPMessage(host);
    }
}
