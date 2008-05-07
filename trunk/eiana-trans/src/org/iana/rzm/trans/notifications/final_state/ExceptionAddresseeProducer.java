package org.iana.rzm.trans.notifications.final_state;

import org.apache.commons.lang.*;
import org.iana.notifications.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.notifications.default_producer.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class ExceptionAddresseeProducer extends DefaultTransactionAddresseeProducer {
    List<String> additionalEmails = new ArrayList<String>();

    public void setAdditionalEmails(List<String> additionalEmails) {
        this.additionalEmails = additionalEmails;
    }

    public Set<PAddressee> produceAddressee(Map dataSource) {
        Set<PAddressee> retAddressees = super.produceAddressee(dataSource);
        for (String email : additionalEmails)
            retAddressees.add(new PAddressee(email, email));

        retAddressees.addAll(getAddressees(AdminRole.AdminType.IANA));

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        String submitter = td.getSubmitterEmail();

        if(StringUtils.isNotBlank(submitter)){
            retAddressees.add(new PAddressee(submitter, submitter));
        }

        return retAddressees;
    }
}
