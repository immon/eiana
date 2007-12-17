package org.iana.rzm.trans.notifications;

import org.iana.notifications.Content;
import org.iana.notifications.ContentConverter;
import org.iana.notifications.TemplateContentFactoryBean;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.TransactionStateLogEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class TransactionTemplateContentFactoryBean extends TemplateContentFactoryBean implements TransactionContentFactory {

    public TransactionTemplateContentFactoryBean(ContentConverter templateConverter) {
        super(templateConverter);
    }

    public Content createContent(String name, Map<String, String> values, TransactionData td) {
        Map<String, String> valuesToAdd = new HashMap<String, String>();
        valuesToAdd.put("ticket", td.getTicketID().toString());
        valuesToAdd.put("requestDate", String.valueOf(td.getCreated().getTime()));
        valuesToAdd.put("confirmationDate", getStateEndDate(TransactionState.Name.PENDING_CONTACT_CONFIRMATION, td));
        valuesToAdd.put("docVrsnDate", getStateStartDate(TransactionState.Name.PENDING_USDOC_APPROVAL, td));
        valuesToAdd.put("implementationDate", getStateEndDate(TransactionState.Name.PENDING_USDOC_APPROVAL, td));
        valuesToAdd.put("serialNumber", ""); //todo
        values.putAll(valuesToAdd);
        return createContent(name, values);
    }

    private String getStateStartDate(TransactionState.Name stateName, TransactionData td) {
        TransactionState state = getTransactionState(stateName, td);
        return (state == null) ? "" : String.valueOf(state.getStart().getTime());
    }

    private String getStateEndDate(TransactionState.Name stateName, TransactionData td) {
        TransactionState state = getTransactionState(stateName, td);
        return (state == null) ? "" : String.valueOf(state.getEnd().getTime());
    }

    private TransactionState getTransactionState(TransactionState.Name stateName, TransactionData td) {
        for (TransactionStateLogEntry logEntry : td.getStateLog()) {
            if (logEntry.getState().getName().equals(stateName))
                return logEntry.getState();
        }
        return null;
    }
}
