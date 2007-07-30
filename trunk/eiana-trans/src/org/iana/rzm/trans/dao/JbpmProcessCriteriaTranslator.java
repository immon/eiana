package org.iana.rzm.trans.dao;

import org.iana.criteria.Criterion;
import org.iana.dao.hibernate.HQLBuffer;
import org.iana.dao.hibernate.HQLGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class JbpmProcessCriteriaTranslator {

    HQLBuffer buff;

    public JbpmProcessCriteriaTranslator(Criterion criteria) {
        List<String> froms = new ArrayList<String>();
        froms.add("ProcessInstance as pi");
        froms.add("inner join pi.processDefinition as pd");
        froms.add("HibernateLongInstance as hli");
        froms.add("TransactionData as td");
        froms.add("inner join td.currentDomain as domain");
        froms.add("inner join pi.rootToken rt");
        froms.add("inner join rt.node node");
        froms.add("RZMUser as user");
        froms.add("inner join user.roles as role");

        buff = HQLGenerator.from(froms, criteria);
    }

    public String getHQL() {
        String[] elements = buff.getHQL().split("order by");
        StringBuffer hql = new StringBuffer("select pi ");
        hql.append(elements[0].trim());
        hql.append(" and hli.value.class = 'org.iana.rzm.trans.TransactionData'");
        hql.append(" and hli.value.id = td.objId");
        hql.append(" and pi = hli.processInstance");
        hql.append(" and role.class = SystemRole");
        hql.append(" and role.name = domain.name.name");
        if (elements.length == 2) {
            hql.append(" order by ");
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
