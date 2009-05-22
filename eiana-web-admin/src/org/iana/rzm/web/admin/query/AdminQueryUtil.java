package org.iana.rzm.web.admin.query;

import org.iana.criteria.And;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.Or;
import org.iana.rzm.facade.admin.msgs.PollMsgFields;
import org.iana.rzm.facade.admin.users.UserCriteria;
import org.iana.rzm.web.common.query.QueryBuilderUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class AdminQueryUtil extends QueryBuilderUtil {

    public static Criterion pollMessagesByTransaction(long transactionId){
        return new Equal(PollMsgFields.TRANSACTION_ID, transactionId);
    }

    public static Criterion pollMessagesByRtId(long rtId) {
        return  new Equal(PollMsgFields.TICKET_ID, rtId);
    }


    public static Criterion systemUsers() {
        return new Equal(UserCriteria.ROLE, "SystemRole");
    }

    public static Criterion adminUsers() {
        And iana = new And(new Equal(UserCriteria.ROLE, "AdminRole"),
                          new Equal(UserCriteria.ROLE_TYPE, "IANA"));
        And root = new And(new Equal(UserCriteria.ROLE, "AdminRole"),
                          new Equal(UserCriteria.ROLE_TYPE, "ROOT"));
        
        return new Or(new ArrayList<Criterion>(Arrays.asList(iana, root)));
    }

     public static Criterion docVerisignUsers() {
        And DoC = new And(new ArrayList<Criterion>(Arrays.asList(
            new Equal(UserCriteria.ROLE, "AdminRole"),
            new Equal(UserCriteria.ROLE_TYPE, "GOV_OVERSIGHT"))));

        And verisign = new And(new ArrayList<Criterion>(Arrays.asList(
            new Equal(UserCriteria.ROLE, "AdminRole"),
            new Equal(UserCriteria.ROLE_TYPE, "ZONE_PUBLISHER"))));

       return new Or(new ArrayList<Criterion>(Arrays.asList(DoC,verisign)));
    }
}
