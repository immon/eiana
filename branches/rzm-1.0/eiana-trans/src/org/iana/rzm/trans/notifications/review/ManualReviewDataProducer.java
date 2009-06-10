package org.iana.rzm.trans.notifications.review;

import org.iana.notifications.producers.DataProducer;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainChangePrinter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ManualReviewDataProducer implements DataProducer {

    public Map<String, String> getValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        Domain currentDomain = td.getCurrentDomain();

        values.put("domainName", "." + currentDomain.getName());

        values.put("changes", DomainChangePrinter.print(td.getDomainChange()));

        values.put("ticket", "" + td.getTicketID());

        values.put("submitter", td.getSubmitterEmail() == null ? td.getTrackData().getCreatedBy() : td.getSubmitterEmail());

        values.put("specialReviewFlag", currentDomain.getSpecialReview()? "on" : "off");

        String specialInstructions = currentDomain.getSpecialInstructions();
        if (specialInstructions == null || specialInstructions.trim().length() == 0)
            specialInstructions = "None";

        values.put("specialInstructions", specialInstructions);

        return values;
    }
}
