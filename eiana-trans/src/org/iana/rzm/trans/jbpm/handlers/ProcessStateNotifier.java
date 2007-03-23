package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.*;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class ProcessStateNotifier implements ActionHandler {

    private static final String EMAIL_TEMPLATE_DATA = "EMAIL_TEMPLATE_DATA";
    private static final String TEMPLATE_TYPE       = "TEMPLATE_TYPE";
    private static final String EMAIL_ADDRESSES     = "EMAIL_ADDRESSES";

    String notification;
    
    public void execute(ExecutionContext executionContext) throws Exception {

        Object o = executionContext.getContextInstance().getVariable(EMAIL_TEMPLATE_DATA);
        String templateType = (String) executionContext.getContextInstance().getVariable(TEMPLATE_TYPE);
        Object addresses = executionContext.getContextInstance().getVariable(EMAIL_ADDRESSES);

        if ((o != null) && (addresses != null) && (templateType != null) && (templateType.trim().length() > 0)) {
            NotificationTemplateManager notifTemplateMgr = NotificationTemplateManager.getInstance();
            Notification notification = notifTemplateMgr.getNotificationTemplate(templateType).getNotificationInstance(o);
            NotificationSender notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");
            if (addresses instanceof EmailAddress)
                notificationSender.send((EmailAddress)addresses, notification.getContent());
            else {
                List<Addressee> addressesList = new ArrayList<Addressee>() ;
                for (Addressee address : ((EmailAddresses)addresses).getAddressees())
                    addressesList.add(address);
                notificationSender.send(addressesList, notification.getContent());
            }
        }
    }
}
