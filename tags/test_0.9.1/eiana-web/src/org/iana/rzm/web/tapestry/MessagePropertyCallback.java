package org.iana.rzm.web.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.PageCallback;
import org.iana.rzm.web.pages.MessageProperty;

public class MessagePropertyCallback extends PageCallback {

    private String infoMessage;
    private String warningMsg;
    private String pageName;

    public MessagePropertyCallback(MessageProperty page) {
        super(page.getPageName());
        this.pageName = page.getPageName();
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String msg) {
        this.infoMessage = msg;
    }

    public String getWarningMessage() {
        return warningMsg;
    }

    public void setWarningMessage(String message) {
        warningMsg = message;
    }

    public void performCallback(IRequestCycle cycle) {
        MessageProperty page = (MessageProperty) cycle.getPage(pageName);
        if (infoMessage != null) {
            page.setInfoMessage(infoMessage);
        }

        if (warningMsg != null) {
            page.setWarningMessage(warningMsg);
        }
        cycle.activate(page);
    }
}
