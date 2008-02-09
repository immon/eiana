package org.iana.rzm.trans.dao;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
class ProcessCriteriaHqlTranslator {
    public static HqlQuery translate(ProcessCriteria criteria) {
        List<String> froms = new ArrayList<String>();
        List<String> wheres = new ArrayList<String>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        froms.add("ProcessInstance as pi");

        if (criteria.getProcessNames() != null && !criteria.getProcessNames().isEmpty()) {
            froms.add("inner join pi.processDefinition as pd");
            wheres.add("pd.name in (:name)");
            parameters.put("name", criteria.getProcessNames());
        }

        if (criteria.getStates() != null && !criteria.getStates().isEmpty()) {
            froms.add("inner join pi.rootToken rt");
            froms.add("inner join rt.node node");
            wheres.add("node.name in (:state)");
            parameters.put("state", criteria.getStates());
        }

        if (criteria.getStartedAfter() != null) {
            wheres.add("pi.start >= :startedAfter");
            parameters.put("startedAfter", criteria.getStartedAfter());
        }

        if (criteria.getStartedBefore() != null) {
            wheres.add("pi.start <= :startedBefore");
            parameters.put("startedBefore", criteria.getStartedBefore());
        }

        if (criteria.getFinishedAfter() != null) {
            wheres.add("pi.end >= :finishedAfter");
            parameters.put("finishedAfter", criteria.getFinishedAfter());
        }

        if (criteria.getFinishedBefore() != null) {
            wheres.add("pi.end <= :finishedBefore");
            parameters.put("finishedBefore", criteria.getFinishedBefore());
        }

/*
        if ((criteria.getDomainNames() != null && !criteria.getDomainNames().isEmpty()) ||
                (criteria.getTicketIds() != null && !criteria.getTicketIds().isEmpty()) ||
                criteria.getCreatedAfter() != null ||
                criteria.getCreatedBefore() != null ||
                (criteria.getCreators() != null && !criteria.getCreators().isEmpty()) ||
                criteria.getModifiedAfter() != null ||
                criteria.getModifiedBefore() != null ||
                (criteria.getModifiers() != null && !criteria.getModifiers().isEmpty()) ||
                (criteria.getUserNames() != null && !criteria.getUserNames().isEmpty())) {
*/
            froms.add("HibernateLongInstance as hli");
            froms.add("TransactionData as td");
            froms.add("inner join td.currentDomain as domain");
            wheres.add("hli.value.class = 'org.iana.rzm.trans.TransactionData'");
            wheres.add("hli.value.id = td.objId");
            wheres.add("pi = hli.processInstance");
/*
        }
*/

        if (criteria.getDomainNames() != null && !criteria.getDomainNames().isEmpty()) {
            wheres.add("domain.name.name in (:domainName)");
            parameters.put("domainName", criteria.getDomainNames());
        }

        if (criteria.getTicketIds() != null && !criteria.getTicketIds().isEmpty()) {
            wheres.add("td.ticketID in (:ticketID)");
            parameters.put("ticketID", criteria.getTicketIds());
        }

        if (criteria.getCreatedAfter() != null) {
            wheres.add("td.trackData.created >= :createdAfter");
            parameters.put("createdAfter", criteria.getCreatedAfter());
        }

        if (criteria.getCreatedBefore() != null) {
            wheres.add("td.trackData.created <= :createdBefore");
            parameters.put("createdBefore", criteria.getCreatedBefore());
        }

        if (criteria.getCreators() != null && !criteria.getCreators().isEmpty()) {
            wheres.add("td.trackData.createdBy in (:createdBy)");
            parameters.put("createdBy", criteria.getCreators());
        }

        if (criteria.getModifiedAfter() != null) {
            wheres.add("td.trackData.modified >= :modifiedAfter");
            parameters.put("modifiedAfter", criteria.getModifiedAfter());
        }

        if (criteria.getModifiedBefore() != null) {
            wheres.add("td.trackData.modified <= :modifiedBefore");
            parameters.put("modifiedBefore", criteria.getModifiedBefore());
        }

        if (criteria.getModifiers() != null && !criteria.getModifiers().isEmpty()) {
            wheres.add("td.trackData.modifiedBy in (:modifiedBy)");
            parameters.put("modifiedBy", criteria.getModifiers());
        }

        if (criteria.getUserNames() != null && !criteria.getUserNames().isEmpty()) {
            froms.add("RZMUser as user");
            froms.add("inner join user.roles as role");
            wheres.add("user.loginName = :loginName");
            wheres.add("role.class = SystemRole");
            wheres.add("role.name = domain.name.name");
            parameters.put("loginName", criteria.getUserNames());
        }

        if (criteria.getOpen() != null) {
            if (criteria.getOpen().booleanValue()) {
                wheres.add("pi.end is null");
            } else {
                wheres.add("pi.end is not null");
            }
        }

        StringBuffer hql = new StringBuffer("select pi from\n");

        Iterator<String> iFroms = froms.iterator();
        if (iFroms.hasNext()) {
            hql.append(iFroms.next());
            while (iFroms.hasNext()) {
                String line = iFroms.next();
                if (!line.startsWith("inner"))
                    hql.append(",\n");
                else
                    hql.append("\n");
                hql.append(line);
            }
        }

        if (!wheres.isEmpty()) {
            hql.append("\nwhere\n");
            Iterator<String> iWheres = wheres.iterator();
            if (iWheres.hasNext()) {
                hql.append(iWheres.next());
                while (iWheres.hasNext()) {
                    hql.append("\nand ");
                    hql.append(iWheres.next());
                }
            }
        }

        return new HqlQuery(hql.toString(), parameters);
    }
}
