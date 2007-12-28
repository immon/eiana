package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.TemplateProducer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class USDOCConfirmationTemplateProducer implements TemplateProducer {

    public List<String> produce(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        if (td.isNameServerChange())
            return Arrays.asList("usdoc-confirmation-nschange");
        else
            return Arrays.asList("usdoc-confirmation");
    }
}
