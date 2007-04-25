package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"excluded", "eiana-trans", "jbpm", "NameServersChange"})
public class NameServersChangeTest {
    private TransactionManager transMgr;
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;
    private SchedulerThread schedulerThread;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private Long testProcessInstanceId;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringTransApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        schedulerThread = new SchedulerThread((JbpmConfiguration) appCtx.getBean("jbpmConfiguration"));

        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            ProcessInstance pi = processDAO.getProcessInstance(testProcessInstanceId);
            if (pi != null) processDAO.delete(pi);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            Domain domain = domainDAO.get("testdomain-ns.org");
            if (domain != null) domainDAO.delete(domain);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testNameServersChange() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        Domain domain = null;
        try {
            Host firstNameServer = new Host("first");
            firstNameServer.addIPAddress("192.168.0.1");

            domain = new Domain("testdomain-ns.org");
            domain.addNameServer(firstNameServer);
            domainDAO.create(domain);

            Host secondNameServer = new Host("second");
            secondNameServer.addIPAddress("192.168.0.2");

            Domain clonedDomain = (Domain) domain.clone();
            clonedDomain.addNameServer(secondNameServer);

            Transaction tr = transMgr.createDomainModificationTransaction(clonedDomain);

            ProcessInstance pi = processDAO.getProcessInstance(tr.getTransactionID());
            testProcessInstanceId = pi.getId();

            Token token = pi.getRootToken();
            assert token.getNode().getName().equals("PENDING_CONTACT_CONFIRMATION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_IMPACTED_PARTIES") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_IANA_CONFIRMATION") : "unexpected state: " + token.getNode().getName();
            token.signal("normal");
            assert token.getNode().getName().equals("PENDING_EXT_APPROVAL") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_USDOC_APPROVAL") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_ZONE_INSERTION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_ZONE_PUBLICATION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
        
        try {
            txStatus = txMgr.getTransaction(txDef);

            Domain retrivedDomain = domainDAO.get(domain.getName());

            //assert (retrivedDomain.getWhoisServer().equals("newwhoisserver") &&
            //        retrivedDomain.getRegistryUrl() == null);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }
}
