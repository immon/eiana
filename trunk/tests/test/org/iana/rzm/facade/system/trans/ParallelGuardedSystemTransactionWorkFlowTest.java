package org.iana.rzm.facade.system.trans;

import org.testng.annotations.Test;

/**
 * Excluded: at this moment parallel transactions can be only the result of a split.
 *
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"facade-system", "ParallelGuardedSystemTransactionWorkFlowTest"})
public class ParallelGuardedSystemTransactionWorkFlowTest extends CommonGuardedSystemTransaction {

/*
    RZMUser userAC, userTC, userIANA, userUSDoC;
    IDomainVO firstModificationVO, secondModificationVO;
    Domain domain;

    final static String DOMAIN_NAME = "gstsignaltest.org";

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

        domain = createDomain(DOMAIN_NAME);
        domain.setWhoisServer("oldwhoisserver.org");
        domain.addNameServer(setupFirstHost("pr1"));
        domain.addNameServer(setupSecondHost("pr2"));
        domainManager.create(domain);

        domain.setRegistryUrl("newregurl.org");

        firstModificationVO = DomainToVOConverter.toDomainVO(domain);

        domain.setWhoisServer("newwhoisserver.com");

        secondModificationVO = DomainToVOConverter.toDomainVO(domain);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

    }

    @Test
    public void testParallelRun1() throws Exception {
        Long transId = createTransaction(firstModificationVO, userAC).getTransactionID();     //1.1
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);                          //1.2
        Long secTransId = createTransaction(secondModificationVO, userAC).getTransactionID(); //2.1
        acceptMANUAL_REVIEW(userIANA, transId);                                               //1.3
        acceptPENDING_CONTACT_CONFIRMATION(userAC, secTransId, 2);                       //2.2
        acceptIANA_CHECK(userIANA, transId);                                                  //1.4
        acceptMANUAL_REVIEW(userIANA, secTransId);                                            //2.3
        acceptIANA_CHECK(userIANA, secTransId);                                               //2.4
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);                                   //1.5

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

    @Test(dependsOnMethods = {"testParallelRun1"})
    public void testParallelRun2() throws Exception {
        firstModificationVO = getDomain(DOMAIN_NAME, userAC);
        secondModificationVO = getDomain(DOMAIN_NAME, userAC);

        ContactVO firstContactVO = DomainToVOConverter.toContactVO(new Contact("TechContact"));
        ContactVO secondContactVO = DomainToVOConverter.toContactVO(new Contact("AdminContact"));

        firstModificationVO.setTechContact(firstContactVO);

        secondModificationVO.setAdminContact(secondContactVO);

        Long transId = createTransaction(firstModificationVO, userAC).getTransactionID();     //1.1
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 3);                          //1.2
        Long secTransId = createTransaction(secondModificationVO, userAC).getTransactionID(); //2.1
        acceptMANUAL_REVIEW(userIANA, transId);                                               //1.3
        acceptPENDING_CONTACT_CONFIRMATION(userAC, secTransId, 3);                       //2.2
        acceptIANA_CHECK(userIANA, transId);                                                  //1.4
        acceptMANUAL_REVIEW(userIANA, secTransId);                                            //2.3
        acceptIANA_CHECK(userIANA, secTransId);                                               //2.4
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);                                   //1.5

        IDomainVO retDomain = getDomain(DOMAIN_NAME, userAC);
        assert retDomain != null;
        Set<ContactVO> retContacts = new TreeSet<ContactVO>(contactVOComparator);
        retContacts.add(retDomain.getTechContact());
        assert retContacts.size() == 1;
        Iterator<ContactVO> retContactsIterator = retContacts.iterator();
        assert "TechContact".equals(retContactsIterator.next().getName());
        assert "admin".equals(retDomain.getAdminContact().getName());

        acceptUSDOC_APPROVALnoNSChange(userUSDoC, secTransId);                                //2.5

        retDomain = getDomain(DOMAIN_NAME, userAC);
        assert retDomain != null;
        retContacts = new TreeSet<ContactVO>(contactVOComparator);
        retContacts.add(retDomain.getAdminContact());
        assert retContacts.size() == 1;
        retContactsIterator = retContacts.iterator();
        assert "AdminContact".equals(retContactsIterator.next().getName());


    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        processDAO.deleteAll();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    private static final ContactVOComparator contactVOComparator = new ContactVOComparator();

    static class ContactVOComparator implements Comparator<ContactVO> {
        public int compare(ContactVO o1, ContactVO o2) {
            if (o1 == null) return -1;
            return o1.getName().compareTo(o2.getName());
        }
    }
*/
}
