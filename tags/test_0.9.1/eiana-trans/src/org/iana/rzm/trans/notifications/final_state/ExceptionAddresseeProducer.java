package org.iana.rzm.trans.notifications.final_state;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
import org.iana.rzm.trans.notifications.default_producer.DefaultAddresseeProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ExceptionAddresseeProducer extends DefaultAddresseeProducer {
    List<String> additionalEmails = new ArrayList<String>();

    public void setAdditionalEmails(List<String> additionalEmails) {
        this.additionalEmails = additionalEmails;
    }

    public Set<Addressee> produceAddressee(Map dataSource) {
        Set<Addressee> retAddressees = super.produceAddressee(dataSource);
        for (String email : additionalEmails)
            retAddressees.add(new EmailAddressee(email, email));

        return retAddressees;
    }
}