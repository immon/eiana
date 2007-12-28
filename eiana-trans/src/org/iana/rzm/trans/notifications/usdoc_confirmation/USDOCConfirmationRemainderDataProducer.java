package org.iana.rzm.trans.notifications.usdoc_confirmation;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.default_producer.DefaultDataProducer;
import org.iana.rzm.trans.notifications.producer.DataProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class USDOCConfirmationRemainderDataProducer extends DefaultDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        values.put("domainName", td.getCurrentDomain().getName());
        values.put("transactionId", "" + dataSource.get("transactionId"));
        values.put("stateName", "" + dataSource.get("stateName"));
        values.put("period", "" + dataSource.get("period"));

        return values;
    }
}
