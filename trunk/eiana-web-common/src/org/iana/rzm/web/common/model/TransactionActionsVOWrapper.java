package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.system.trans.vo.changes.*;

import java.util.*;

public class TransactionActionsVOWrapper extends ValueObject  {

    private TransactionActionsVO vo;

    public TransactionActionsVOWrapper(TransactionActionsVO vo) {
        this.vo = vo;
    }


    public List<ActionVOWrapper>getChanges(){
        List<TransactionActionVO> list = vo.getActions();
        List<ActionVOWrapper>result = new ArrayList<ActionVOWrapper>();
        for (TransactionActionVO actionVO : list) {
            result.add(new ActionVOWrapper(actionVO));
        }
        return result;
    }


    public boolean mustSplitrequest(){
        return vo.getGroups().size() > 1;    
    }


    public boolean isNameServerChange(){
        return vo.containsNameServerAction();
    }

    public boolean hasOtherChanges(){
        return vo.containsOtherAction();
    }

    public boolean offerSeparateRequest(){
        return vo.containsNameServerAction() && vo.containsOtherAction();
    }
}
