package org.iana.rzm.trans.manager;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;

/**
 * Tests that the state msg is null-ed on leaving the EXCEPTION state.
 *
 * @author Patrycja Wegrzynowicz
 */
public class MsgCleanUnOnLeavingExceptionStateTest extends RollbackableSpringContextTest {

    protected TransactionManager transactionManagerBean;
    protected DomainManager domainManager;
    protected ProcessDAO processDAO;

    protected Domain domain;

    public MsgCleanUnOnLeavingExceptionStateTest() {
         super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        domain = new Domain("exception.org");
        domainManager.create(domain);
    }

    protected void cleanUp() throws Exception {
        processDAO.deleteAll();
    }


    @Test
    public void testNullMsgOnLeave() throws Exception {
        Domain d = domain.clone();
        d.setWhoisServer("whoisserver");
        Transaction transaction = transactionManagerBean.createDomainModificationTransaction(d, null);
        transaction.exception("msg");
        assert transaction.getState().getName() == TransactionState.Name.EXCEPTION;
        assertEquals(transaction.getStateMessage(), "msg");

        transaction.transitTo(new RZMUser("fn", "ln", "org", "loginname", "email", "pwd", false), TransactionState.Name.PENDING_CREATION.toString());
        assert transaction.getStateMessage() == null;
    }

}
