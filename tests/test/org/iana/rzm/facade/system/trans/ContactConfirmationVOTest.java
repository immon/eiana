package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.ContactVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.ConfirmationVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class ContactConfirmationVOTest  extends CommonGuardedSystemTransaction {

    protected void initTestData() {
        Domain domain = new Domain("contactconfirmation");
        domain.setSupportingOrg(new Contact("so-name"));
        domain.setAdminContact(new Contact("ac-name"));
        domain.setTechContact(new Contact("tc-name"));
        domainManager.create(domain);
    }

    @Test
    public void testNoConfirmationReceived() throws Exception {
        setDefaultUser();

        TransactionVO trans = createTransaction();

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        Set<ConfirmationVO> expectedConfirmations = new HashSet<ConfirmationVO>();
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name", false, ""));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name-new", true, ""));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name", false, ""));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true, ""));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, false, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, false, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

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
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.AC, true, "ac-name-new", true));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name", false));
        expectedConfirmations.add(new ConfirmationVO("contactconfirmation", SystemRoleVO.SystemType.TC, true, "tc-name-new", true));
        assert equalConfirmations(expectedConfirmations, trans.getConfirmations());

        closeServices();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        processDAO.deleteAll();
    }
    
    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        for (Domain domain : domainManager.findAll())
//            domainManager.delete(domain.getName());
    }

    private String getToken(long transID, String name) throws Exception {
        return transactionManagerBean.getTransactionToken(transID, name);
    }

    private TransactionVO createTransaction() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation");
        domain.setRegistryUrl("contactconfirmation.registry.url");
        domain.setAdminContact(new ContactVO("ac-name-new", "ac-name-new@no-email.org"));
        domain.setTechContact(new ContactVO("tc-name-new", "tc-name-new@no-email.org"));
        return createTransaction(domain);
    }

    private boolean equalConfirmations(Set<ConfirmationVO> expectedConfirmations, Set<ConfirmationVO> currentConfirmations) {
        assert expectedConfirmations.size() == currentConfirmations.size();
        int exist = 0;
        for (ConfirmationVO confirmationVO: currentConfirmations) {
            for (ConfirmationVO expectedConfirmationVO : expectedConfirmations) {
                if (expectedConfirmationVO.equals(confirmationVO)) {
                    exist++;
                    break;
                }
            }
        }
        return exist == expectedConfirmations.size();
    }
}
