package org.iana.rzm.trans;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.IPAddressVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "NameServerChangeTransactionTest"})
public class NameServerChangeTransactionTest {
    private SystemTransactionService gsts;
    private UserManager userManager;
    private DomainManager domainManager;
    private IDomainVO domain;
    private ProcessDAO processDAO;


    @BeforeClass
    public void init() throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        userManager = (UserManager) appCtx.getBean("userManager");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        gsts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");

        UserVO userAc;
        userAc = createUser("acNSC", SystemRole.SystemType.AC);
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(userAc);
        gsts.setUser(testAuthUser.getAuthUser());

        domain = createDomain();

        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testNameServersChange() throws InfrastructureException, NoDomainModificationException, NoObjectFoundException {
        TransactionVO transaction;

        List<HostVO> ns = domain.getNameServers();
        HostVO newHostVO = new HostVO("new.host.name");
        IPAddressVO ipAddressVO = new IPAddressVO();
        ipAddressVO.setAddress("200.32.46.35");
        Set<IPAddressVO> setIPAddressVOs = new HashSet<IPAddressVO>();
        setIPAddressVOs.add(ipAddressVO);
        newHostVO.setAddresses(setIPAddressVOs);
        ns.add(newHostVO);
        domain.setNameServers(ns);

        transaction = gsts.createTransactions(domain, false).get(0);
        assert transaction != null;

        TransactionVO loadedTransaction = gsts.getTransaction(transaction.getTransactionID());
        assert loadedTransaction != null;
        assert compareTransactionVOs(transaction, loadedTransaction);
        Host newNameServer = new Host("newNameServer");
        List<HostVO> hostVOList = new ArrayList<HostVO>();
        hostVOList.add(ToVOConverter.toHostVO(newNameServer));
        domain.setNameServers(hostVOList);
        TransactionActionsVO transactionActionsVO = gsts.detectTransactionActions(domain);
        assert transactionActionsVO.containsNameServerAction();
        assert transactionActionsVO.getActions().size() == 1;
        for (ChangeVO changeVO : transactionActionsVO.getActions().get(0).getChange()) {
            assert "nameServers".equals(changeVO.getFieldName());
            ObjectValueVO valueVO = (ObjectValueVO) changeVO.getValue();
            ChangeVO cVO = valueVO.getChanges().get(0);
            if (cVO.getType().equals(ChangeVO.Type.REMOVAL)) {
                assert cVO.getValue() instanceof StringValueVO;
                StringValueVO stringValueVO = (StringValueVO) cVO.getValue();
                assert stringValueVO.getNewValue() == null;
                assert "ns2.gstsnsc".equals(stringValueVO.getOldValue()) || "ns1.gstsnsc".equals(stringValueVO.getOldValue());
            }
            if (cVO.getType().equals(ChangeVO.Type.ADDITION)) {
                assert cVO.getValue() instanceof StringValueVO;
                StringValueVO stringValueVO = (StringValueVO) cVO.getValue();
                assert "newnameserver".equals(stringValueVO.getNewValue());
                assert stringValueVO.getOldValue() == null;
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        gsts.close();
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user.getLoginName());
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    private UserVO createUser(String name, SystemRole.SystemType role) {
        RZMUser user = new RZMUser();
        user.setEmail(name + "gsts@no-mail.org");
        user.setFirstName(name + "gsts first name");
        user.setLastName(name + "gsts last name");
        user.setLoginName(name + "gsts");
        user.setOrganization(name + "gsts organization");
        user.setPassword(new MD5Password(name + "gsts"));
        user.setSecurID(false);
        user.addRole(new SystemRole(role, "gsts", true, false));
        userManager.create(user);
        return UserConverter.convert(user);
    }

    private Address setupAddress(Address address, String prefix, String countryCode) {
        address.setTextAddress(prefix + " text address");
        address.setCountryCode(countryCode);
        return address;
    }

    private Contact setupContact(Contact contact, String prefix, String domainName, String countryCode) {
        contact.setName(prefix + " name");
        contact.addAddress(setupAddress(new Address(), "contact", countryCode));
        contact.addEmail(prefix + "@no-mail." + domainName);
        contact.addFaxNumber("+1234567890");
        contact.addPhoneNumber("+1234567891");
        return contact;
    }

    private Host setupFirstHost(Host host) throws InvalidIPAddressException, InvalidNameException {
        host.setName(host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("81.50.50.10"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    private Host setupSecondHost(Host host) throws InvalidIPAddressException, InvalidNameException {
        host.setName(host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("82.52.50.10"));
        host.addIPAddress(IPAddress.createIPv6Address("2234:5678::90AB"));
        return host;
    }

    private Domain setupDomain(String name) throws MalformedURLException, InvalidNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        Domain domain = new Domain(name);
        domain.setRegistryUrl("http://www.registry." + name);
        domain.setSpecialInstructions(name + " special instructions");
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(setupContact(new Contact(), "supporting-org", name, "US"));
        domain.setWhoisServer("whois." + name);
        domain.addAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupFirstHost(new Host("ns1." + name)));
        domain.addNameServer(setupSecondHost(new Host("ns2." + name)));
        domain.addTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }

    private IDomainVO createDomain() throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = setupDomain("gstsNSC");
        domainManager.create(domain);
        return ToVOConverter.toDomainVO(domain);
    }

    private boolean compareTransactionVOs(TransactionVO first, TransactionVO second) {

        if (first.getDomainActions() != null ? !first.getDomainActions().equals(second.getDomainActions()) : second.getDomainActions() != null)
            return false;
        if (first.getDomainName() != null ? !first.getDomainName().equals(second.getDomainName()) : second.getDomainName() != null)
            return false;
        if (first.getEnd() != null ? !first.getEnd().equals(second.getEnd()) : second.getEnd() != null) return false;
        if (first.getName() != null ? !first.getName().equals(second.getName()) : second.getName() != null)
            return false;
        if (first.getStart() != null ? !first.getStart().equals(second.getStart()) : second.getStart() != null)
            return false;
        if (first.getState() != null ? !first.getState().equals(second.getState()) : second.getState() != null)
            return false;
        if (first.getTicketID() != null ? !first.getTicketID().equals(second.getTicketID()) : second.getTicketID() != null)
            return false;
        if (first.getTransactionID() != null ? !first.getTransactionID().equals(second.getTransactionID()) : second.getTransactionID() != null)
            return false;

        return true;
    }
}
