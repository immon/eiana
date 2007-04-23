package org.iana.rzm.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.RZMUser;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"eiana-trans", "jbpm", "NameServersChange"})
public class NameServersChangeTest {
    ApplicationContext appCtx;
    TransactionManager transMgr;
    ProcessDAO processDAO;
    DomainDAO domainDAO;
    SchedulerThread schedulerThread;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private Long testProcessInstanceId;

    @BeforeClass
    public void init() {
        appCtx = SpringTransApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        schedulerThread = new SchedulerThread((JbpmConfiguration) appCtx.getBean("jbpmConfiguration"));

        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @AfterClass
    public void cleanUp() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        processDAO.delete(processDAO.getProcessInstance(testProcessInstanceId));
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        Domain domain = domainDAO.get("testdomain-ns.org");
        domainDAO.delete(domain);
        txMgr.commit(txStatus);
    }

    @Test
    public void testNameServersChange() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Host firstNameServer = new Host("first");
        firstNameServer.addIPAddress("192.168.0.1");


        Domain domain = new Domain("testdomain-ns.org");
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
        token.signal();

        token.signal("accept");
        token.signal("normal");
        token.signal("accept");

        token.signal("accept");
        assert token.getNode().getName().equals("PENDING_ZONE_INSERTION");
        token.signal("accept");
        assert token.getNode().getName().equals("PENDING_ZONE_PUBLICATION");
        token.signal("accept");

        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);

        Domain retrivedDomain = domainDAO.get(domain.getName());

//        assert (retrivedDomain.getWhoisServer().equals("newwhoisserver") &&
//                retrivedDomain.getRegistryUrl() == null);

        txMgr.commit(txStatus);
    }
}
