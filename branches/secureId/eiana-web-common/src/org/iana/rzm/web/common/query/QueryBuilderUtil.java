package org.iana.rzm.web.common.query;

import org.iana.criteria.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.*;
import org.iana.rzm.web.common.query.resolver.*;

import java.util.*;

public class QueryBuilderUtil {

    public static Criterion openTransactions() {
        List<Criterion> andList = new ArrayList<Criterion>();
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.COMPLETED.name())));
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.REJECTED.name())));
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.ADMIN_CLOSED.name())));
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.WITHDRAWN.name())));
        return new And(andList);
    }

    public static Criterion closeTransactions() {
        List<Criterion> orList = new ArrayList<Criterion>();
        orList.add(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.COMPLETED.name()));
        orList.add(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.REJECTED.name()));
        orList.add(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.ADMIN_CLOSED.name()));
        orList.add(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.WITHDRAWN.name()));
        return new Or(orList);
    }

    public static Criterion openTransactionForDomains(List<String> domains) {

        if(domains == null || domains.isEmpty()){
            return empty();
        }

        List<Criterion> orList = new ArrayList<Criterion>();
        for (String domain : domains) {
            orList.add(new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, domain));
        }
        return new And(Arrays.asList(openTransactions(), new Or(orList)));
    }

    public static Criterion closeTransactionForDomains(List<String> domains) {
        List<Criterion> orList = new ArrayList<Criterion>();
        for (String domain : domains) {
            orList.add(new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, domain));
        }
        return new And(Arrays.asList(closeTransactions(), new Or(orList)));
    }




    public static Criterion empty() {
        return null;
    }


    public static Criterion userName(String userName) {
        return new Equal("loginName", userName);
    }

    public static Criterion impactedParty(List<String> domains) {
        return new And(new In(TransactionCriteriaFields.IMPACTED_DOMAIN, new HashSet<String>(domains)),  openTransactions());
    }

    public static Criterion domainsByName(String entity) {
        return new Like(new DomainFieldNameResolver().resolve("domainName"), entity);
    }

    public static Criterion usersForDomains(List<String> domains) {

        //List<Or>orList = new ArrayList<Or>();
        //
        //for (String domain : domains) {
        //    Criterion systemRole = new Equal(UserCriteria.ROLE,
        //    UserCriteria.SYSTEM_ROLE);
        //    Criterion systemDomain = new Equal(UserCriteria.ROLE_DOMAIN, "tld2");
        //    Criterion and = new And(systemRole, systemDomain);
        //
        //}

        return empty();
    }
}
