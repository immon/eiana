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

@Test(sequential = true, groups = {"test", "jbpm", "UpdateDomain"})
public class DomainModificationWorkflowTest {

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

            domainManager.delete("testdomain.org");

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
    public void doUpdate() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);

        try {
            Domain domain = new Domain("testdomain.org");
            domain.setRegistryUrl("http://www.oldregistryurl.org");
            domainManager.create(domain);

            Domain clonedDomain = domainManager.get("testdomain.org").clone();

            clonedDomain.setWhoisServer("newwhoisserver");
            clonedDomain.setRegistryUrl(null);

            Host nameServer = new Host("newnameserver1.org");
            nameServer.addIPAddress("81.50.50.10");
            clonedDomain.addNameServer(nameServer);
            nameServer = new Host("newnameserver2.org");
            nameServer.addIPAddress("82.50.50.10");
            clonedDomain.addNameServer(nameServer);

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

            Domain retrivedDomain = domainManager.get(domain.getName()).clone();
            assert clonedDomain.equals(retrivedDomain);

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
