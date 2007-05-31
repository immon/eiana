package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"eiana-trans", "jbpm", "NameServersChange"})
public class NameServersChangeTest {
    private PlatformTransactionManager txManager;
    private TransactionDefinition txDefinition = new DefaultTransactionDefinition();
    private TransactionManager transMgr;
    private ProcessDAO processDAO;
    private DomainManager domainManager;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringTransApplicationContext.getInstance().getContext();
        txManager = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());

        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            List<ProcessInstance> pis = processDAO.findAll();
            for (ProcessInstance pi : pis) {
                processDAO.delete(pi);
            }

            domainManager.delete("testdomain-ns.org");

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testNameServersChange() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Host firstNameServer = new Host("first");
            firstNameServer.addIPAddress("192.168.0.1");

            Domain domain = new Domain("testdomain-ns.org");
            domain.addNameServer(firstNameServer);
            domainManager.create(domain);

            Host secondNameServer = new Host("second");
            secondNameServer.addIPAddress("192.168.0.2");

            Domain clonedDomain = domain.clone();
            clonedDomain.addNameServer(secondNameServer);

            Transaction tr = transMgr.createDomainModificationTransaction(clonedDomain);

            ProcessInstance pi = processDAO.getProcessInstance(tr.getTransactionID());


            Token token = pi.getRootToken();
            assert token.getNode().getName().equals("PENDING_CONTACT_CONFIRMATION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_MANUAL_REVIEW") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_IANA_CHECK") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_USDOC_APPROVAL") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_ZONE_INSERTION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_ZONE_PUBLICATION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");

            assert token.getNode().getName().equals("COMPLETED");

//            assert clonedDomain.equals(domainManager.get("testdomain-ns.org").clone());

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }
}
