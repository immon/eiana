package org.iana.rzm.trans.dao;

import org.iana.criteria.Criterion;
import org.iana.dao.hibernate.HQLBuffer;
import org.iana.dao.hibernate.HQLGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
class JbpmProcessCriteriaTranslator {

    private static final String JOIN_ROOT = "ProcessInstance as pi";
    private static final String DOMAIN_JOIN =
            "HibernateLongInstance as hli,\n" +
                    "TransactionData as td\n" +
                    "inner join td.currentDomain as domain";
    private static final String DOMAIN_JOIN_CONDITIONS =
            "hli.value.class = 'org.iana.rzm.trans.TransactionData'\n" +
                    "and hli.value.id = td.objId\n" +
                    "and pi = hli.processInstance";
    private static final String USER_JOIN =
            "RZMUser as user inner join user.roles as role";
    private static final String USER_JOIN_CONDITIONS =
            "role.class = SystemRole\n" +
                        "and role.name = domain.name.name";
    private static Map<String, String> criteriaFields = new HashMap<String, String>();
    private static Map<String, String> criteriaJoins = new HashMap<String, String>();

    static {
        // field names to field full names
        criteriaFields.put("transactionData", "td");
        criteriaFields.put("objId", "pi.id");
        criteriaFields.put("transactionID", "pi.id");
        criteriaFields.put("ticketID", "td.ticketID");
        criteriaFields.put("name", "pd.name");
        criteriaFields.put("currentDomain", "domain");
        criteriaFields.put("state", "node.name");
        criteriaFields.put("start", "pi.start");
        criteriaFields.put("end", "pi.end");
        criteriaFields.put("created", "td.trackData.created");
        criteriaFields.put("modified", "td.trackData.modified");
        criteriaFields.put("createdBy", "td.trackData.createdBy");
        criteriaFields.put("modifiedBy", "td.trackData.modifiedBy");
        criteriaFields.put("redelegation", "td.redelegation");
        criteriaFields.put("submitterEmail", "td.submitterEmail");
        criteriaFields.put("loginName", "user.loginName");
        // aliases to joins
        criteriaJoins.put("pd", "inner join pi.processDefinition as pd");
        criteriaJoins.put("node",
                "inner join pi.rootToken as rt\n" +
                        "inner join rt.node as node");
    }

    private HQLBuffer buff;
    private String joinConditions;

    public JbpmProcessCriteriaTranslator(Criterion criteria) {
        JbpmProcessCriteriaFieldNamesExtractor ext = new JbpmProcessCriteriaFieldNamesExtractor(criteriaFields);
        criteria.accept(ext);
        Set<String> fieldNames = ext.getNames();
        StringBuffer from = new StringBuffer(JOIN_ROOT);
        Set<String> addedAliases = new HashSet<String>();
        for (String name : fieldNames) {
            String alias = name.split("\\.", 2)[0];
            if (!addedAliases.contains(alias)) {
                String join = criteriaJoins.get(alias);
                if (join != null)
                    from.append("\n").append(join);
                addedAliases.add(alias);
            }
        }
        from.append(",\n").append(DOMAIN_JOIN);
        joinConditions = DOMAIN_JOIN_CONDITIONS;
        if (addedAliases.contains("user")) {
            from.append(",\n").append(USER_JOIN);
            joinConditions += "\nand " + USER_JOIN_CONDITIONS;
        }
        buff = HQLGenerator.from(from.toString(), criteria);
    }

    public String getHQL() {
        String[] elements = buff.getHQL().split("order by");
        StringBuffer hql = new StringBuffer("select pi ");
        hql.append(elements[0].trim());
        hql.append("\nand ").append(joinConditions);
        if (elements.length == 2) {
            hql.append("\norder by\n");
            String second = elements[1].trim();
            if (second.startsWith(","))
                second = second.substring(1);
            hql.append(second);
        }
        return hql.toString();
    }

    public Object[] getParams() {
        return buff.getParams();
    }
}
