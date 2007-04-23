package org.iana.rzm.facade.system.trans;

import org.iana.rzm.trans.TransactionCriteria;

/**
 * @author Jakub Laszkiewicz
 */
class TransactionCriteriaConverter {
    public static TransactionCriteria convert(TransactionCriteriaVO criteria) {
        TransactionCriteria result = new TransactionCriteria();
        result.addAllCreators(criteria.getCreators());
        result.addAllDomainNames(criteria.getDomainNames());
        result.addAllModifiers(criteria.getModifiers());
        result.addAllProcessNames(criteria.getProcessNames());
        result.addAllStates(criteria.getStates());
        result.addAllTicketIds(criteria.getTicketIds());
        result.setCreatedAfter(criteria.getCreatedAfter());
        result.setCreatedBefore(criteria.getCreatedBefore());
        result.setFinishedAfter(criteria.getFinishedAfter());
        result.setFinishedBefore(criteria.getFinishedBefore());
        result.setModifiedAfter(criteria.getModifiedAfter());
        result.setModifiedBefore(criteria.getModifiedBefore());
        result.setStartedAfter(criteria.getStartedAfter());
        result.setStartedBefore(criteria.getStartedBefore());
        return result;
    }
}
