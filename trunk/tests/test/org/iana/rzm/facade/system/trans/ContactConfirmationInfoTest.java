package org.iana.rzm.facade.system.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.auth.Identity;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true)
public class ContactConfirmationInfoTest extends CommonGuardedSystemTransaction {

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

        TransactionVO trans = createTransaction(domain, iana);

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        assert !trans.soConfirmed();
        assert !trans.tcConfirmed();
        assert !trans.acConfirmed();
    }

    @Test
    public void testACConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain, iana);

        String token = getToken(trans.getTransactionID(), SystemRole.SystemType.AC);

        setGSTSAuthUser(iana);
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        assert !trans.soConfirmed();
        assert !trans.tcConfirmed();
        assert trans.acConfirmed();
    }

    @Test
    public void testTCConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain, iana);

        String token = getToken(trans.getTransactionID(), SystemRole.SystemType.TC);

        setGSTSAuthUser(iana);
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION;
        assert !trans.soConfirmed();
        assert trans.tcConfirmed();
        assert !trans.acConfirmed();
    }

    @Test
    public void testACTCConfirmationReceived() throws Exception {
        IDomainVO domain = getDomain("contactconfirmation", iana);
        domain.setRegistryUrl("contactconfirmation.registry.url");

        TransactionVO trans = createTransaction(domain, iana);

        setGSTSAuthUser(iana);

        String token = getToken(trans.getTransactionID(), SystemRole.SystemType.TC);
        gsts.acceptTransaction(trans.getTransactionID(), token);

        token = getToken(trans.getTransactionID(), SystemRole.SystemType.AC);
        gsts.acceptTransaction(trans.getTransactionID(), token);

        trans = gsts.getTransaction(trans.getTransactionID());

        assert trans.getState().getName() == TransactionStateVO.Name.PENDING_MANUAL_REVIEW;
        assert !trans.soConfirmed();
        assert trans.tcConfirmed();
        assert trans.acConfirmed();
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
        TransactionData td = (TransactionData) pi.getContextInstance().getVariable("TRANSACTION_DATA");
        for (Identity id : td.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) id;
            if (cid.getType() == role) {
                return cid.getToken();
            }
        }
        throw new IllegalArgumentException("no role to confirm found: " + role);
    }
}
