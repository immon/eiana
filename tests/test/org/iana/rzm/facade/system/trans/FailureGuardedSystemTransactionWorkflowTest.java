package org.iana.rzm.facade.system.trans;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"facade-system", "FailureGuardedSystemTransactionWorkflowTest"})
public class FailureGuardedSystemTransactionWorkflowTest extends CommonGuardedSystemTransaction{

    private RZMUser userAC, userACWrong, userTC, userIANA, userUSDoC;
    private IDomainVO domainVO;

    final static String DOMAIN_NAME = "gstsfailuretest.org";
    final static String WRONG_NAME = "wrongdomainname.org";

    @BeforeClass
    public void init() {
        userAC = new RZMUser();
        userAC.setLoginName("gstsignaluser");
        userAC.setFirstName("ACuser");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME, true, true));
        userManager.create(userAC);

        userTC = new RZMUser();
        userTC.setLoginName("gstsignalseconduser");
        userTC.setFirstName("TCuser");
        userTC.setLastName("lastName");
        userTC.setEmail("email@some.com");
        userTC.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAME, true, true));
        userManager.create(userTC);

        userIANA = new RZMUser();
        userIANA.setLoginName("gstsignaliana");
        userIANA.setFirstName("IANAuser");
        userIANA.setLastName("lastName");
        userIANA.setEmail("email@some.com");
        userIANA.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(userIANA);

        userUSDoC = new RZMUser();
        userUSDoC.setLoginName("gstsignalusdoc");
        userUSDoC.setFirstName("USDoCuser");
        userUSDoC.setLastName("lastName");
        userUSDoC.setEmail("email@some.com");
        userUSDoC.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(userUSDoC);

        userACWrong = new RZMUser();
        userACWrong.setLoginName("gstsignalwronguser");
        userACWrong.setFirstName("ACWronguser");
        userACWrong.setLastName("lastName");
        userACWrong.setEmail("email@some.com");
        userACWrong.addRole(new SystemRole(SystemRole.SystemType.AC, WRONG_NAME, true, true));
        userManager.create(userACWrong);


        Domain domain = createDomain(DOMAIN_NAME);
        domain.addNameServer(setupFirstHost("pr1"));
        domain.addNameServer(setupSecondHost("pr2"));
        domainManager.create(domain);

        domain.setRegistryUrl("newregurl.org");

        domainVO = ToVOConverter.toDomainVO(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test (expectedExceptions = {AccessDeniedException.class})
    public void testFAILURE_createTransaction() throws Exception {
        createTransaction(domainVO, userUSDoC);
    }

    @Test (expectedExceptions = {AccessDeniedException.class})
    public void testFAILURE_REJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVO, userAC).getTransactionID();
        rejectPENDING_CONTACT_CONFIRMATIONWrongToken(userACWrong, transId);
    }

    @Test (expectedExceptions = {AccessDeniedException.class})
    public void testFAILURE_CLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVO, userAC).getTransactionID();
        closePENDING_CONTACT_CONFIRMATION(userACWrong, transId);
    }

    @Test (expectedExceptions = {AccessDeniedException.class})
    public void testFAILURE_ACCEPT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVO, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATIONWrongToken(userACWrong, userTC, transId);
    }

//    @Test (expectedExceptions = {AccessDeniedException.class})
//    public void testFAILURE_REJECT_IMPACTED_PARTIES() throws Exception {
//        Long transId = createTransaction(domainVO, userAC).getTransactionID();
//        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
////        rejectIMPACTED_PARTIES(userUSDoC, transId); todo
//
//    }

//    @Test  (expectedExceptions = {AccessDeniedException.class})
//    public void testFAILURE_CLOSE_IMPACTED_PARTIES() throws Exception {
//        Long transId = createTransaction(domainVO, userAC).getTransactionID();
//        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        closeIMPACTED_PARTIES(userUSDoC, transId);
//    }                   todo



    @AfterClass (alwaysRun = true)
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
