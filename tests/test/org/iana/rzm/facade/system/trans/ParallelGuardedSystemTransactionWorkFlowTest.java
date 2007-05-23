package org.iana.rzm.facade.system.trans;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.facade.system.domain.ContactVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"facade-system", "ParallelGuardedSystemTransactionWorkFlowTest"})
public class ParallelGuardedSystemTransactionWorkFlowTest extends CommonGuardedSystemTransaction {

    PlatformTransactionManager txMgr;
    TransactionDefinition txDef = new DefaultTransactionDefinition();
    RZMUser userAC, userTC, userIANA, userUSDoC;
    IDomainVO firstModificationVO, secondModificationVO;
    Domain domain;

    final static String DOMAIN_NAME = "gstsignaltest.org";

    @BeforeClass
    public void init() {

        appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        gsts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

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

        domain = createDomain(DOMAIN_NAME);
        domain.setWhoisServer("oldwhoisserver.org");
        domain.addNameServer(setupFirstHost("pr1"));
        domain.addNameServer(setupSecondHost("pr2"));
        domainManager.create(domain);

        domain.setRegistryUrl("newregurl.org");

        firstModificationVO = ToVOConverter.toDomainVO(domain);

        domain.setWhoisServer("newwhoisserver.com");

        secondModificationVO = ToVOConverter.toDomainVO(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

    }

    @Test
    public void testParallelRun1() throws Exception {
        Long transId = createTransaction(firstModificationVO, userAC).getTransactionID();     //1.1
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);                          //1.2
        Long secTransId = createTransaction(secondModificationVO, userAC).getTransactionID(); //2.1
//        acceptIMPACTED_PARTIES(userAC, transId);          todo                                    //1.3
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, secTransId);                       //2.2
        normalIANA_CONFIRMATION(userIANA, transId);                                           //1.4
//        acceptIMPACTED_PARTIES(userAC, secTransId);     todo                                      //2.3
        normalIANA_CONFIRMATION(userIANA, secTransId);                                        //2.4
        acceptEXT_APPROVAL(userIANA, transId);                                                //1.5
        acceptEXT_APPROVAL(userIANA, secTransId);                                             //2.5
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);                                   //1.6

        Domain retDomain = domainManager.get(DOMAIN_NAME);
        assert retDomain != null;
        assert "newregurl.org".equals(retDomain.getRegistryUrl());
        assert "oldwhoisserver.org".equals(retDomain.getWhoisServer());

        acceptUSDOC_APPROVALnoNSChange(userUSDoC, secTransId);                              //2.6

        retDomain = domainManager.get(DOMAIN_NAME);
        assert retDomain != null;
        assert "newregurl.org".equals(retDomain.getRegistryUrl());
        assert "newwhoisserver.com".equals(retDomain.getWhoisServer());
    }

    @Test (dependsOnMethods = {"testParallelRun1"})
    public void testParallelRun2() throws Exception {

        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        Domain retDomain = domainManager.get(DOMAIN_NAME);
        firstModificationVO = ToVOConverter.toDomainVO(retDomain);
        secondModificationVO = ToVOConverter.toDomainVO(retDomain);
        txMgr.commit(txStatus);

        ContactVO firstContactVO = ToVOConverter.toContactVO(new Contact("firstTechContact"));
        ContactVO secondContactVO = ToVOConverter.toContactVO(new Contact("secondTechContact"));

        List<ContactVO> techContactVOs = firstModificationVO.getTechContacts();
        techContactVOs.add(firstContactVO);
        firstModificationVO.setTechContacts(techContactVOs);

        techContactVOs = secondModificationVO.getTechContacts();
        techContactVOs.add(secondContactVO);
        secondModificationVO.setTechContacts(techContactVOs);

        Long transId = createTransaction(firstModificationVO, userAC).getTransactionID();     //1.1
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);                          //1.2
        Long secTransId = createTransaction(secondModificationVO, userAC).getTransactionID(); //2.1
//        acceptIMPACTED_PARTIES(userAC, transId);   todo                                           //1.3
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, secTransId);                       //2.2
        normalIANA_CONFIRMATION(userIANA, transId);                                           //1.4
//        acceptIMPACTED_PARTIES(userAC, secTransId);  todo                                         //2.3
        normalIANA_CONFIRMATION(userIANA, secTransId);                                        //2.4
        acceptEXT_APPROVAL(userIANA, transId);                                                //1.5
        acceptEXT_APPROVAL(userIANA, secTransId);                                             //2.5
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);                                   //1.6

        txStatus = txMgr.getTransaction(txDef);
        retDomain = domainManager.get(DOMAIN_NAME);
        assert retDomain != null;
        List<Contact> contactsList = new ArrayList<Contact>();
        contactsList.add(new Contact("firstTechContact"));
        assert contactsList.equals(retDomain.getTechContacts());
        txMgr.commit(txStatus);

        acceptUSDOC_APPROVALnoNSChange(userUSDoC, secTransId);                                //2.6

        txStatus = txMgr.getTransaction(txDef);
        retDomain = domainManager.get(DOMAIN_NAME);
        assert retDomain != null;
        contactsList.add(new Contact("secondTechContact"));
        assert contactsList.equals(retDomain.getTechContacts());
        txMgr.commit(txStatus);
    }

    @AfterClass
    public void cleanUp() {
        List<ProcessInstance> processInstances = processDAO.findAll();
        for (ProcessInstance processInstance : processInstances)
            processDAO.delete(processInstance);
        processDAO.close();

        userManager.delete(userAC);
        userManager.delete(userTC);
        userManager.delete(userIANA);
        userManager.delete(userUSDoC);
        domainManager.delete(DOMAIN_NAME);
    }
}
