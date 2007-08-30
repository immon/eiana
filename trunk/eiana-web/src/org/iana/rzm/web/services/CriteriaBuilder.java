package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.facade.system.trans.*;

import java.util.*;

public class CriteriaBuilder {

    public static TransactionCriteriaVO createOpenTransactionCriteria() {
        TransactionCriteriaVO criteria = new TransactionCriteriaVO();

        criteria.addState(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION.name());
        criteria.addState(TransactionStateVO.Name.PENDING_DATABASE_INSERTION.name());
        criteria.addState(TransactionStateVO.Name.PENDING_EVALUATION.name());
        criteria.addState(TransactionStateVO.Name.PENDING_EXT_APPROVAL.name());
        criteria.addState(TransactionStateVO.Name.PENDING_IANA_CHECK.name());
        criteria.addState(TransactionStateVO.Name.PENDING_IANA_CONFIRMATION.name());
        criteria.addState(TransactionStateVO.Name.PENDING_IMPACTED_PARTIES.name());
        criteria.addState(TransactionStateVO.Name.PENDING_TECH_CHECK.name());
        criteria.addState(TransactionStateVO.Name.PENDING_TECH_CHECK_REMEDY.name());
        criteria.addState(TransactionStateVO.Name.PENDING_USDOC_APPROVAL.name());
        criteria.addState(TransactionStateVO.Name.PENDING_ZONE_INSERTION.name());
        criteria.addState(TransactionStateVO.Name.PENDING_ZONE_PUBLICATION.name());
        criteria.addState(TransactionStateVO.Name.PENDING_MANUAL_REVIEW.name());
        criteria.addState(TransactionStateVO.Name.PENDING_SOENDORSEMENT.name());
        criteria.addState(TransactionStateVO.Name.PENDING_SUPP_TECH_CHECK.name());
        criteria.addState(TransactionStateVO.Name.PENDING_SUPP_TECH_CHECK_REMEDY.name());
        return criteria;
    }

    public static TransactionCriteriaVO createOpenTransactionCriteriaForDomain(String... domains) {
        TransactionCriteriaVO criteria = createOpenTransactionCriteria();
        for (String domain : domains) {
            criteria.addDomainName(domain);
        }

        return criteria;
    }


    public static Criterion createOpenTransactions() {
        List<Criterion> andList = new ArrayList<Criterion>();
        andList.add(new Not(new Equal("state", TransactionStateVO.Name.COMPLETED.name())));
        andList.add(new Not(new Equal("state", TransactionStateVO.Name.REJECTED.name())));
        andList.add(new Not(new Equal("state", TransactionStateVO.Name.ADMIN_CLOSED.name())));
        andList.add(new Not(new Equal("state", TransactionStateVO.Name.WITHDRAWN.name())));
        return new And(andList);
    }

    public static Criterion creteOpenTransactionCriterionForDomain(String domain) {
        return new And(Arrays.asList(createOpenTransactions(),new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, domain)));
    }

    public static Criterion empty() {
        return null;
    }

    public static Criterion systemUsers() {
        return null;
    }

    public static Criterion adminUsers() {
        return null;
    }
}
