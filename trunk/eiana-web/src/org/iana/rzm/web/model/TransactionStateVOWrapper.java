package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.trans.TransactionStateVO;
import org.iana.rzm.web.util.DateUtil;

public class TransactionStateVOWrapper extends ValueObject {
    private TransactionStateVO vo;
    
    public static enum State {
        PENDING_TECH_CHECK("Pending Tech Check"),
        PENDING_TECH_CHECK_REMEDY("Pending Tech Check Remedy"),
        PENDING_CONTACT_CONFIRMATION("Pending Contact Confirmation"),
        PENDING_SOENDORSEMENT("PENDING_SOENDORSEMENT"),
        PENDING_IMPACTED_PARTIES("Pending Impacted Parties"),
        PENDING_MANUAL_REVIEW("Pending manual Review"),
        PENDING_EXT_APPROVAL("Pending Ext Approval"),
        PENDING_EVALUATION("Pending Evaluation"),
        PENDING_IANA_CHECK("Pending IANA Check"),
        PENDING_SUPP_TECH_CHECK("Pending Supplement Tech Check"),
        PENDING_SUPP_TECH_CHECK_REMEDY("Pending Supplement Tech Check Remedy"),
        PENDING_USDOC_APPROVAL("Pending USDOC Approval"),
        PENDING_ZONE_INSERTION("Pending Zone Publication"),
        PENDING_ZONE_PUBLICATION("Pending Zone Publication"),
        PENDING_DATABASE_INSERTION("Pending Database Insertion"),
        COMPLETED("Completed"),
        WITHDRAWN("Withdrawn"),
        REJECTED("Rejected"),
        ADMIN_CLOSE("Admin Close"),
        EXCEPTION("Exception"),
        PENDING_IANA_CONFIRMATION("Pending Iana Confirmation");

        private String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

        public TransactionStateVO.Name getVOName() {
            return TransactionStateVO.Name.values()[this.ordinal()];
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    public TransactionStateVOWrapper(TransactionStateVO vo) {
        this.vo = vo;
    }

    public String getStateName(){
        return State.values()[vo.getName().ordinal()].getDisplayName();
    }

    public State getState(){
        return State.values()[vo.getName().ordinal()];
    }

    public String getStart(){
        return DateUtil.formatDate(vo.getStart());
    }

    public String getEnd(){
        return DateUtil.formatDate(vo.getEnd());
    }


}
