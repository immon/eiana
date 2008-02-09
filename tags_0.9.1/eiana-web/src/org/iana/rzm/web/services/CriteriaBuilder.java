package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.facade.admin.users.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.*;

import java.util.*;

public class CriteriaBuilder {

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

    public static Criterion systemUsers() {
        return new Equal(UserCriteriaFields.ROLE, "SystemRole");
    }

    public static Criterion adminUsers() {
        return new And(new Equal(UserCriteriaFields.ROLE, "AdminRole"),
                       new Equal(UserCriteriaFields.ROLE_TYPE, "IANA"));
    }

    public static Criterion userName(String userName) {
        return new Equal("loginName", userName);
    }

    public static Criterion docVerisignUsers() {
        And DoC = new And(new ArrayList<Criterion>(Arrays.asList(
            new Equal(UserCriteriaFields.ROLE, "AdminRole"),
            new Equal(UserCriteriaFields.ROLE_TYPE, "GOV_OVERSIGHT"))));

        And verisign = new And(new ArrayList<Criterion>(Arrays.asList(
            new Equal(UserCriteriaFields.ROLE, "AdminRole"),
            new Equal(UserCriteriaFields.ROLE_TYPE, "ZONE_PUBLISHER"))));

       return new Or(new ArrayList<Criterion>(Arrays.asList(DoC,verisign)));
    }
}
