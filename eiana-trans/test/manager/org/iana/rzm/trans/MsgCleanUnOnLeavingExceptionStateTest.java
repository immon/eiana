package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.testng.annotations.Test;

/**
 * Tests that the state msg is null-ed on leaving the EXCEPTION state.
 *
 * @author Patrycja Wegrzynowicz
 */
public class MsgCleanUnOnLeavingExceptionStateTest extends TransactionalSpringContextTests {

    protected TransactionManager transactionManagerBean;
    protected DomainManager domainManager;
    protected ProcessDAO processDAO;

    protected Domain domain;

    public MsgCleanUnOnLeavingExceptionStateTest() {
         super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
            domain = new Domain("exception.org");
            domainManager.create(domain);
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        processDAO.deleteAll();
        domainManager.deleteAll();
    }


    @Test
    public void testNullMsgOnLeave() throws Exception {
        try {
            domain.setWhoisServer("whoisserver");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(domain, null);
            transaction.exception("msg");
            assert transaction.getState().getName() == TransactionState.Name.EXCEPTION;
            assertEquals(transaction.getStateMessage(), "msg");

            transaction.transitTo(new RZMUser("fn", "ln", "org", "loginname", "email", "pwd", false), TransactionState.Name.PENDING_CREATION.toString());
            assert transaction.getStateMessage() == null;
        } finally {
            processDAO.close();
        }
    }

}
