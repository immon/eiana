package org.iana.rzm.facade.system.trans;

import org.iana.rzm.auth.Identity;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.ContactVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.ConfirmationVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class ContactConfirmationVOTest extends CommonGuardedSystemTransaction {

    @BeforeClass
    public void init() throws Exception {
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
        setDefaultUser();

        TransactionVO trans = createTransaction();

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testACConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "ac-name");

        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testNewACConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "ac-name-new");

        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testTCConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "tc-name");

        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testNewTCConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "tc-name-new");

        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testACTCConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "tc-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name");
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testNewACNewTCConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "tc-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testACNewACConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "ac-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testTCNewTCConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "tc-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "tc-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testACNewACTCConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "ac-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "tc-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
    }

    @Test
    public void testAllConfirmationsReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        String token = getToken(trans.getTransactionID(), "ac-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "tc-name");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "ac-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), "tc-name-new");
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_MANUAL_REVIEW;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO(SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert expectedConfirmations.equals(trans.getConfirmations());

        closeServices();
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
        if (td.getContactConfirmations() == null) return null;
        for (Identity id : td.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) id;
            if (name.equals(cid.getName())) {
                return cid.getToken();
            }
        }
        throw new IllegalArgumentException("no name to confirm found: " + name);
    }

    private TransactionVO createTransaction() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation");
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));
        domain.setTechContact(new ContactVO("tc-name-new", "tc-name-new@no-email.org"));
        return createTransaction(domain);
    }
}
