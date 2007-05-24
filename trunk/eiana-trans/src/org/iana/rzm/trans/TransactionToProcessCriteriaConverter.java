package org.iana.rzm.trans;

import org.iana.rzm.trans.dao.ProcessCriteria;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionToProcessCriteriaConverter {
    public static ProcessCriteria convert(TransactionCriteria criteria) {
        ProcessCriteria result = new ProcessCriteria ();
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
        result.setOpen(criteria.getOpen());
        return result;
    }
}
