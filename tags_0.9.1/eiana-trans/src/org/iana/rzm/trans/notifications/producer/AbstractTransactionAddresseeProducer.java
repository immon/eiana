package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public abstract class AbstractTransactionAddresseeProducer implements AddresseeProducer {

    public Set<Addressee> produce(Map dataSource) {
        Set<Addressee> addressees = new HashSet<Addressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        String submitterEmail = td.getSubmitterEmail();
        if (CheckTool.isCorrectEmali(submitterEmail))
            addressees.add(new EmailAddressee(submitterEmail, submitterEmail));

        addressees.addAll(produceAddressee(dataSource));

        return addressees;
    }

    abstract protected Set<Addressee> produceAddressee(Map dataSource);
}
