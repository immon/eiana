package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 * @author: JaKub Laszkiewicz
 */
@Test(sequential = true)
public class ContactConfirmationInfoTest extends CommonGuardedSystemTransaction {

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

        IDomainVO domain = getDomain("contactconfirmation");
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        assert !trans.soConfirmed();
        assert !trans.tcConfirmed();
        assert !trans.acConfirmed();

        closeServices();
    }

    @Test
    public void testACConfirmationReceived() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("contactconfirmation");
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain);

        String token = getToken(trans.getTransactionID(), SystemRole.SystemType.AC);

        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        assert !trans.soConfirmed();
        assert !trans.tcConfirmed();
        assert trans.acConfirmed();

        closeServices();
    }

    @Test
    public void testTCConfirmationReceived() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("contactconfirmation");
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain);

        String token = getToken(trans.getTransactionID(), SystemRole.SystemType.TC);

        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        assert !trans.soConfirmed();
        assert trans.tcConfirmed();
        assert !trans.acConfirmed();

        closeServices();
    }

    @Test
    public void testACTCConfirmationReceived() throws Exception {
        setDefaultUser();

        IDomainVO domain = getDomain("contactconfirmation");
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain);

        String token = getToken(trans.getTransactionID(), SystemRole.SystemType.TC);
        acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), SystemRole.SystemType.AC);
        acceptTransaction(trans.getTransactionID(), token);

        trans = getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_MANUAL_REVIEW;
        assert !trans.soConfirmed();
        assert trans.tcConfirmed();
        assert trans.acConfirmed();

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

    private String getToken(long transID, SystemRole.SystemType role) {
        ProcessInstance pi = processDAO.getProcessInstance(transID);
        Transaction trans = new Transaction(pi);
        if (trans.getContactConfirmations() == null) return null;
        for (Identity id : trans.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) id;
            if (cid.getType() == role) {
                return cid.getToken();
            }
        }
        throw new IllegalArgumentException("no role to confirm found: " + role);
    }
}
