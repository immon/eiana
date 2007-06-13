package org.iana.rzm.facade.system.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.ContactVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.auth.Identity;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true)
public class ContactConfirmationVOTest extends CommonGuardedSystemTransaction {

    RZMUser iana;

    @BeforeClass
    public void init() {
        iana = new RZMUser("fn", "ln", "org", "iana", "iana@nowhere", "", false);
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(iana);
        Domain domain = new Domain("contactconfirmation");
        domain.setSupportingOrg(new Contact("so-name"));
        domain.setAdminContact(new Contact("ac-name"));
        domain.setTechContact(new Contact("tc-name"));
        domainManager.create(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test
    public void testNoConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));

        TransactionVO trans = createTransaction(domain, iana);

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations =  new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        assert expectedConfirmations.equals(trans.getConfirmations());
    }

    @Test
    public void testACConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));

        TransactionVO trans = createTransaction(domain, iana);

        String token = getToken(trans.getTransactionID(), "ac-name");

        setGSTSAuthUser(iana);
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations =  new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        assert expectedConfirmations.equals(trans.getConfirmations());
    }

    @Test
    public void testTCConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));

        TransactionVO trans = createTransaction(domain, iana);

        String token = getToken(trans.getTransactionID(), "tc-name");

        setGSTSAuthUser(iana);
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations =  new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        assert expectedConfirmations.equals(trans.getConfirmations());
    }

    @Test
    public void testACTCConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));

        TransactionVO trans = createTransaction(domain, iana);

        setGSTSAuthUser(iana);

        String token = getToken(trans.getTransactionID(), "tc-name");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations =  new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        assert expectedConfirmations.equals(trans.getConfirmations());
    }

    @Test
    public void testACNewACConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));

        TransactionVO trans = createTransaction(domain, iana);

        setGSTSAuthUser(iana);

        String token = getToken(trans.getTransactionID(), "ac-name");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name-new");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations =  new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        assert expectedConfirmations.equals(trans.getConfirmations());
    }

    @Test
    public void testACNewACTCConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));

        TransactionVO trans = createTransaction(domain, iana);

        setGSTSAuthUser(iana);

        String token = getToken(trans.getTransactionID(), "ac-name");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "tc-name");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name-new");
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_MANUAL_REVIEW;
        Set<ConfirmationVO> expectedConfirmations =  new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        assert expectedConfirmations.equals(trans.getConfirmations());
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

    private String getToken(long transID, String name) {
        ProcessInstance pi = processDAO.getProcessInstance(transID);
        TransactionData td = (TransactionData) pi.getContextInstance().getVariable("TRANSACTION_DATA");
        for (Identity id : td.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) id;
            if (name.equals(cid.getName())) {
                return cid.getToken();
            }
        }
        throw new IllegalArgumentException("no name to confirm found: " + name);
    }
}
