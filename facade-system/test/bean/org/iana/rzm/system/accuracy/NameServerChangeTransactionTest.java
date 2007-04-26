package org.iana.rzm.system.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.system.conf.SpringSystemApplicationContext;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;

/**
 * @author: piotrt
 */

@Test(sequential = true, groups ={"facade-system", "NameServerChangeTransactionTest"})
public class NameServerChangeTransactionTest {
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private SystemTransactionService gsts;
    private UserDAO userDAO;
    private DomainDAO domainDAO;
    private IDomainVO domain;
    private ProcessDAO processDAO;
    private TransactionVO transaction;
    private UserVO userAc, userTc;
    private List<Long> transactionIds = new ArrayList<Long>();

    @BeforeClass
    public void init() throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        processDAO = (ProcessDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("processDAO");
        userDAO = (UserDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("userDAO");
        domainDAO = (DomainDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("domainDAO");
        txMgr = (PlatformTransactionManager) SpringSystemApplicationContext.getInstance().getContext().getBean("transactionManager");
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        userAc = createUser("acNSC", SystemRole.SystemType.AC);
        userTc = createUser("tcNSC", SystemRole.SystemType.TC);
        gsts = (SystemTransactionService) SpringSystemApplicationContext.getInstance().getContext().getBean("guardedSystemTransactionService");
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(userAc);
        gsts.setUser(testAuthUser.getAuthUser());
        domain = createDomain();
        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test
    public void testNameServersChange() throws InfrastructureException, NoObjectFoundException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        transaction = gsts.createTransaction(domain);
        transactionIds.add(transaction.getTransactionID());
        assert transaction != null;
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        TransactionVO loadedTransaction = gsts.getTransaction(transaction.getTransactionID());
        assert loadedTransaction != null;
        assert transaction.equals(loadedTransaction);
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
            /*ChangeVO cVO = valueVO.getChanges().get(0);
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
            }*/
        }
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @AfterClass
    public void cleanUp() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        gsts.close();
        for (Long id : transactionIds) {
            ProcessInstance pi = processDAO.getProcessInstance(id);
            if (pi != null) processDAO.delete(pi);
        }
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        RZMUser user = userDAO.get(userAc.getObjId());
        if (user != null) userDAO.delete(user);
        user = userDAO.get(userTc.getObjId());
        if (user != null) userDAO.delete(user);
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        Domain dom = domainDAO.get(domain.getObjId());
        domainDAO.delete(dom);
        txMgr.commit(txStatus);
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
        userDAO.create(user);
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

    private Host setupHost(Host host) throws InvalidIPAddressException, InvalidNameException {
        host.setName(host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    private Domain setupDomain(String name) throws MalformedURLException, InvalidNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        Domain domain = new Domain(name);
        domain.setRegistryUrl("http://www.registry." + name);
        domain.setSpecialInstructions(name + " special instructions");
        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(setupContact(new Contact(), "supporting-org", name, "US"));
        domain.setWhoisServer("whois." + name);
        domain.addAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupHost(new Host("ns1." + name)));
        domain.addNameServer(setupHost(new Host("ns2." + name)));
        domain.addTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }

    private IDomainVO createDomain() throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = setupDomain("gstsNSC");
        domainDAO.create(domain);
        return ToVOConverter.toDomainVO(domain);
    }
}
