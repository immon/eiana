package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.notifications.producers.TemplateNameProducer;
import org.iana.rzm.trans.TransactionData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ChangeTypeBasedTemplateNameProducer implements TemplateNameProducer {

    private String nsChangeTemplateName;

    private String databaseChangeTemplateName;

    public ChangeTypeBasedTemplateNameProducer(String nsChangeTemplateName, String databaseChangeTemplateName) {
        this.nsChangeTemplateName = nsChangeTemplateName;
        this.databaseChangeTemplateName = databaseChangeTemplateName;
    }

    public List<String> produce(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        if (td.isNameServerChange())
            return Arrays.asList(nsChangeTemplateName, databaseChangeTemplateName);
        else
            return Arrays.asList(databaseChangeTemplateName);
    }
}
