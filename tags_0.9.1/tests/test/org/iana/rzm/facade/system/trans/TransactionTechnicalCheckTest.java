package org.iana.rzm.facade.system.trans;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.user.RZMUser;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionTechnicalCheckTest extends CommonGuardedSystemTransaction {

    @BeforeClass
    public void init() {
        Domain domain1 = new Domain("technicalcheck");
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test(expectedExceptions = DNSTechnicalCheckException.class)
    public void testMinNumberOfNameServersTest() throws Exception {
        IDomainVO domain = getDomain("technicalcheck");
        domain.getNameServers().add(new HostVO("host"));

        setDefaultUser();
        gsts.createTransactions(domain, false, "", true, "");
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
