package org.iana.rzm.web.services;

import org.iana.rzm.facade.system.trans.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.TransactionStateVO;

public class CriteriaBuilder {

    public static TransactionCriteriaVO createOpenTransactionCriteria() {
        TransactionCriteriaVO criteria = new TransactionCriteriaVO();

        criteria.addState(TransactionStateVO.Name.FIRST_NSLINK_CHANGE_DECISION.name());
        criteria.addState(TransactionStateVO.Name.MATCHES_SI_BREAKPOINT_DECISION.name());
        criteria.addState(TransactionStateVO.Name.MODIFICATIONS_IN_CONTACT_DECISION.name());
        criteria.addState(TransactionStateVO.Name.NS_CHANGE_DECISION.name());
        criteria.addState(TransactionStateVO.Name.NS_SHARED_GLUE_CHANGE_DECISION.name());
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
        criteria.addState(TransactionStateVO.Name.REDEL_FLAG_SET_DECISION.name());
        criteria.addState(TransactionStateVO.Name.SECOND_NSLINK_CHANGE_DECISION.name());
        return criteria;
    }

    public static TransactionCriteriaVO createOpenTransactionCriteriaForDomain(String... domains){
        TransactionCriteriaVO criteria = createOpenTransactionCriteria();
        for (String domain : domains) {
            criteria.addDomainName(domain);
        }

        return criteria;
    }
}
