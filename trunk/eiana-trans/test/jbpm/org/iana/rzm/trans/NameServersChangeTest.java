package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author JaKub Laszkiewicz
 */

@Test(sequential = true, groups = {"eiana-trans", "jbpm", "NameServersChange"})
public class NameServersChangeTest extends TransactionalSpringContextTests {
    protected TransactionManager transactionManagerBean;
    protected ProcessDAO processDAO;
    protected DomainManager domainManager;

    public NameServersChangeTest() {
        super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        try {
            List<ProcessInstance> pis = processDAO.findAll();
            for (ProcessInstance pi : pis) {
                processDAO.delete(pi);
            }
            domainManager.delete("testdomain-ns.org");
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testNameServersChange() throws Exception {
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

            Transaction tr = transactionManagerBean.createDomainModificationTransaction(clonedDomain);

            ProcessInstance pi = processDAO.getProcessInstance(tr.getTransactionID());


            Token token = pi.getRootToken();
            assert token.getNode().getName().equals("PENDING_CREATION") : "unexpected state: " + token.getNode().getName();
            token.signal("go-on");
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
            assert token.getNode().getName().equals("COMPLETED") : "unexpected state: " + token.getNode().getName();

//            assert clonedDomain.equals(domainManager.get("testdomain-ns.org").clone());

        } finally {
            processDAO.close();
        }
    }
}
