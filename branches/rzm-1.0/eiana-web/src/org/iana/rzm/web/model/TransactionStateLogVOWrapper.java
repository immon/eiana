package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.trans.vo.*;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 14, 2007
 * Time: 2:45:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionStateLogVOWrapper extends ValueObject implements Comparable<TransactionStateLogVOWrapper> {

    private TransactionStateVOWrapper state;
    private String userName;

    public TransactionStateLogVOWrapper(TransactionStateLogEntryVO vo) {
        state = new TransactionStateVOWrapper(vo.getState());
        userName = vo.getUserName();
    }

    public String getState(){
        return state.getStateName();
    }

    public String getStart(){
        return state.getStart();
    }

    public String getEnd(){
        return state.getEnd();
    }

    public String getApprovedBy(){
        return userName;
    }

    public int compareTo(TransactionStateLogVOWrapper o) {
        return o.state.getEndDate().compareTo(state.getEndDate());
    }
}
