package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.trans.TransactionActionVO;
import org.iana.rzm.facade.system.trans.TransactionStateLogEntryVO;
import org.iana.rzm.facade.system.trans.TransactionStateVO;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.web.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionVOWrapper extends ValueObject implements PaginatedEntity {

    private TransactionVO vo;

    private TransactionStateVOWrapper state;

    public TransactionVOWrapper(TransactionVO vo) {
        this.vo = vo;
        this.state = new TransactionStateVOWrapper(vo.getState());
    }

    public String getCreatedBy() {
        return vo.getCreatedBy();
    }

    public String getModifiedby() {
        return vo.getModifiedBy();
    }

    public String getCreated() {
        return DateUtil.formatDate(vo.getCreated());
    }

    public Date getStart(){
        return vo.getStart();
    }

    public String getModified() {
        return DateUtil.formatDate(vo.getModified());
    }

    public long getId() {
        return this.vo.getTransactionID();
    }

    public String getDomainName() {
        return vo.getDomainName();
    }

    public long getRtId() {
        return vo.getTicketID();
    }

    public boolean acConfirmed(){
        return vo.acConfirmed();
    }

    public boolean tcConfirmed(){
        return vo.tcConfirmed();
    }


    public List<ActionVOWrapper> getChanges() {
        List<TransactionActionVO> actions = vo.getDomainActions();
        List<ActionVOWrapper> wrappers = new ArrayList<ActionVOWrapper>();
        for (TransactionActionVO action : actions) {
            wrappers.add(new ActionVOWrapper(action));
        }
        return wrappers;
    }

    public List<TransactionStateLogVOWrapper>getStateHistory(){
        List<TransactionStateLogEntryVO> list = vo.getStateLog();
        List<TransactionStateLogVOWrapper>history = new ArrayList<TransactionStateLogVOWrapper>();
        for (TransactionStateLogEntryVO stateLog : list) {
            history.add(new TransactionStateLogVOWrapper(stateLog));    
        }

        return history;
    }

    public String getCurrentStateAsString() {
        return state.getStateName();
    }

    public TransactionStateVOWrapper.State getState() {
        return state.getState();
    }

    public void setRt(long rt) {
        vo.setTicketID(rt);
    }

    public void setState( TransactionStateVOWrapper.State state){
        TransactionStateVO.Name name = state.getVOName();
        TransactionStateVO voState = new TransactionStateVO();
        voState.setName(name);
        vo.setState(voState);
        this.state = new TransactionStateVOWrapper(vo.getState());
    }
}
