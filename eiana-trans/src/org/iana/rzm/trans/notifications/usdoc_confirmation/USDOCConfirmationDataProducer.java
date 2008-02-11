package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.notifications.producers.DataProducer;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainChangePrinter;
import org.iana.rzm.trans.notifications.default_producer.DefaultTransactionDataProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class USDOCConfirmationDataProducer extends DefaultTransactionDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        values.put("domainName", td.getCurrentDomain().getName());
        values.put("transactionId", "" + dataSource.get("transactionId"));
        values.put("stateName", "" + dataSource.get("stateName"));
        values.put("receipt", "" + dataSource.get("receipt"));
        values.put("ticket", "" + td.getTicketID());
        values.put("notes", td.getUsdocNotes());
        values.put("eppid", "" + dataSource.get("eppID"));
        values.put("change", DomainChangePrinter.print(td.getDomainChange()));
        values.put("retry", "" + td.getEPPRetries());

        return values;
    }
}
