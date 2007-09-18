package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.facade.admin.*;
import org.iana.rzm.facade.system.trans.*;

import java.util.*;

public class CriteriaBuilder {

    public static Criterion createOpenTransactions() {
        List<Criterion> andList = new ArrayList<Criterion>();
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.COMPLETED.name())));
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.REJECTED.name())));
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.ADMIN_CLOSED.name())));
        andList.add(new Not(new Equal(TransactionCriteriaFields.STATE, TransactionStateVO.Name.WITHDRAWN.name())));
        return new And(andList);
    }

    public static Criterion openTransactionForDomain(String domain) {
        return new And(Arrays.asList(createOpenTransactions(),
                                     new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, domain)));
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

    public static Criterion forUserName(String userName) {
        return new Equal("loginName", userName);
    }
}
