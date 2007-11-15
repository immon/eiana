/**
 * org.iana.rzm.trans.jbpm.handlers.ticketingservice.RequestTrackerTicket
 * (C) NASK 2006
 * jakubl, 2007-10-05 12:24:44
 */
package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.iana.objectdiff.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.change.*;
import org.iana.ticketing.*;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class RequestTrackerTicket implements Ticket {
    private enum RequestType {
        NS("ns"),
        AC_DATA("ac-data"),
        URL("url"),
        WHOIS("whois"),
        TC_DATA("tc-data"),
        AC_CHANGE("ac change"),
        TC_CHANGE("tc change"),
        SO_DATA("so-data"),
        SO_CHANGE("so change"),
        REDELEGATION("redelegation"),
        ROOT_SERVER("root-server"),
        DELEGATION("delegation");

        private final String name;

        RequestType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static Map<TransactionState.Name, String> transactionToIanaState = new HashMap<TransactionState.Name, String>();

    static {
        transactionToIanaState.put(TransactionState.Name.PENDING_CONTACT_CONFIRMATION, "AC/TC");
        transactionToIanaState.put(TransactionState.Name.PENDING_TECH_CHECK, "tech-check");
        transactionToIanaState.put(TransactionState.Name.PENDING_TECH_CHECK_REMEDY, "tech-check");
        transactionToIanaState.put(TransactionState.Name.PENDING_EXT_APPROVAL, "wait on letter");
        transactionToIanaState.put(TransactionState.Name.PENDING_EVALUATION, "prepare IANA Board report");
        transactionToIanaState.put(TransactionState.Name.PENDING_IANA_CHECK, "IANA Review - IN");
        transactionToIanaState.put(TransactionState.Name.PENDING_SUPP_TECH_CHECK, "tech-check");
        transactionToIanaState.put(TransactionState.Name.PENDING_SUPP_TECH_CHECK_REMEDY, "tech-check");
        transactionToIanaState.put(TransactionState.Name.PENDING_USDOC_APPROVAL, "wait on DoC");
        transactionToIanaState.put(TransactionState.Name.PENDING_ZONE_INSERTION, "wait on VGRS");
        transactionToIanaState.put(TransactionState.Name.PENDING_ZONE_PUBLICATION, "VGRS confirmed");
        transactionToIanaState.put(TransactionState.Name.PENDING_DATABASE_INSERTION, "commit data changes");
        transactionToIanaState.put(TransactionState.Name.COMPLETED, "Completed");
        transactionToIanaState.put(TransactionState.Name.WITHDRAWN, "withdrawn");
        transactionToIanaState.put(TransactionState.Name.REJECTED, "rejected");
        transactionToIanaState.put(TransactionState.Name.ADMIN_CLOSED, "admin-close");
        transactionToIanaState.put(TransactionState.Name.PENDING_IANA_CONFIRMATION, "pre-review");
    }

    private Transaction transaction;

    public RequestTrackerTicket(Transaction transaction) {
        CheckTool.checkNull(transaction, "transaction");
        this.transaction = transaction;
    }

    public Long getId() {
        return transaction.getTicketID();
    }

    public String getTld() {
        return transaction.getCurrentDomain().getIanaCode();
    }

    public List<String> getRequestType() {
        List<String> result = new ArrayList<String>();
        ObjectChange domainChange = transaction.getDomainChange();
        if (domainChange.getFieldChanges().containsKey("nameServers"))
            result.add(RequestType.NS.getName());
        if (domainChange.getFieldChanges().containsKey("registryUrl"))
            result.add(RequestType.URL.getName());
        if (domainChange.getFieldChanges().containsKey("whoisServer"))
            result.add(RequestType.WHOIS.getName());
        if (domainChange.getFieldChanges().containsKey("adminContact"))
            result.add(getContactRequestType(domainChange, "adminContact", RequestType.AC_CHANGE, RequestType.AC_DATA));
        if (domainChange.getFieldChanges().containsKey("supportingOrg"))
            result.add(getContactRequestType(domainChange, "supportingOrg", RequestType.SO_CHANGE, RequestType.SO_DATA));
        if (domainChange.getFieldChanges().containsKey("techContact"))
            result.add(getContactRequestType(domainChange, "techContact", RequestType.TC_CHANGE, RequestType.TC_DATA));
        return result;
    }

    private String getContactRequestType(ObjectChange domainChange, String fieldName, RequestType change, RequestType data) {
        ObjectChange contactChange = (ObjectChange) domainChange.getFieldChanges().get(fieldName);
        if (contactChange.getFieldChanges().containsKey("name") ||
                contactChange.getFieldChanges().containsKey("organization"))
            return change.getName();
        else
            return data.getName();
    }

    public String getIanaState() {
        TransactionState.Name name = transaction.getState().getName();
        return transactionToIanaState.get(name);
    }

    public String getComment() {
        return DomainChangePrinter.print(transaction.getDomainChange());
    }
}
