package org.iana.rzm.facade.system.trans;

import org.iana.dao.DataAccessObject;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.notifications.PNotification;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.admin.domain.AdminDomainService;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.admin.trans.notifications.AdminNotificationService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.notification.NotificationConverter;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateLogEntryVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 * @author: JaKub Laszkiewicz
 */

public abstract class CommonGuardedSystemTransaction extends RollbackableSpringContextTest {
    protected ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
    protected ProcessDAO processDAO; //= (ProcessDAO) appCtx.getBean("processDAO");
    protected HostManager hostManager; //= (HostManager) appCtx.getBean("hostManager");
    protected UserManager userManager; //= (UserManager) appCtx.getBean("userManager");
    protected DomainManager domainManager; //= (DomainManager) appCtx.getBean("domainManager");
    protected AdminNotificationService notificationService;// = (AdminNotificationService) appCtx.getBean("notificationService");
    protected DataAccessObject<PNotification> notificationDAO; //= (DataAccessObject<PNotification>) appCtx.getBean("notificationDAO");
    protected TransactionManager transactionManagerBean;// = (TransactionManager) appCtx.getBean("transactionManagerBean");
    protected TransactionService GuardedSystemTransactionService;// = (TransactionService) appCtx.getBean("GuardedSystemTransactionService");
    protected SystemDomainService GuardedSystemDomainService; // = (SystemDomainService) appCtx.getBean("GuardedSystemDomainService");
    protected AdminDomainService GuardedAdminDomainServiceBean; // = (AdminDomainService) appCtx.getBean("GuardedAdminDomainServiceBean");
    protected AdminTransactionService GuardedAdminTransactionServiceBean; // = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
    protected AuthenticationService authenticationServiceBean; // = (AuthenticationService) appCtx.getBean("authenticationServiceBean");

    protected RZMUser defaultIana;

    private boolean wasInit = false;

    public CommonGuardedSystemTransaction() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected abstract void initTestData();

    protected final void init() {
        if (!wasInit) {
            initTestData();
            wasInit = true;
        }
    }

    @BeforeClass
    public void commonInit() {
        defaultIana = new RZMUser("fn", "ln", "org", "default-iana", "iana@nowhere", "", false);
        defaultIana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(defaultIana);

        AuthenticatedUser authUser = new AuthenticatedUser(defaultIana.getObjId(), defaultIana.getLoginName(), true);
        notificationService.setUser(authUser);
    }

    protected void acceptZONE_PUBLICATION(RZMUser user, long transId) throws Exception {
        setUser(user);     //iana
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        GuardedAdminTransactionServiceBean.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        closeServices();
    }

    protected void acceptZONE_INSERTION(RZMUser user, long transId) throws Exception {
        setUser(user); //iana
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        GuardedAdminTransactionServiceBean.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        closeServices();
    }

    protected void acceptUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);   //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        GuardedSystemTransactionService.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        closeServices();
    }

    protected void acceptUSDOC_APPROVALnoNSChange(RZMUser user, long transId) throws Exception {
        setUser(user); //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        GuardedSystemTransactionService.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        closeServices();
    }

    protected void acceptEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        GuardedSystemTransactionService.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        closeServices();
    }

    protected void acceptMANUAL_REVIEW(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_MANUAL_REVIEW", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "accept");
        assert isTransactionInDesiredState("PENDING_IANA_CHECK", transId);
        closeServices();
    }

    protected void acceptIANA_CHECK(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_IANA_CHECK", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "accept");
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        closeServices();
    }

    protected void acceptIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        GuardedSystemTransactionService.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION", transId);
        closeServices();
    }

    protected void rejectPENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        TransactionVO trans = GuardedSystemTransactionService.get(transId);
        List<String> tokens = trans.getTokens(SystemRoleVO.SystemType.AC);
        assert tokens.size() > 0;
        GuardedSystemTransactionService.rejectTransaction(transId, tokens.iterator().next());
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void rejectPENDING_CONTACT_CONFIRMATIONWrongToken(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        GuardedSystemTransactionService.rejectTransaction(transId, "0");
        closeServices();
    }

    protected void closeEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        closeServices();
    }

    protected void rejectEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        GuardedSystemTransactionService.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void rejectUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "reject");
        assert isTransactionInDesiredState("EXCEPTION", transId);
        closeServices();
    }

    protected void rejectIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setUser(user);  //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        GuardedSystemTransactionService.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void closeIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        closeServices();
    }

    protected void closePENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        closeServices();
    }

    protected void acceptPENDING_CONTACT_CONFIRMATION(RZMUser user, long transId, int tokenCount) throws Exception {
        setUser(user);
        TransactionVO trans = GuardedSystemTransactionService.get(transId);
        List<String> tokens = trans.getTokens();
        assert tokens.size() == tokenCount : "unexpected token count: " + tokens.size();
        for (String token : tokens) {
            assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
            GuardedSystemTransactionService.acceptTransaction(transId, token);
        }
        assert isTransactionInDesiredState("PENDING_MANUAL_REVIEW", transId);
        closeServices();
    }

    protected void acceptPENDING_CONTACT_CONFIRMATION_IMPACTED_PARTIES(RZMUser user, long transId, int tokenCount) throws Exception {
        setUser(user);
        TransactionVO trans = GuardedSystemTransactionService.get(transId);
        List<String> tokens = trans.getTokens();
        assert tokens.size() == tokenCount : "unexpected token count: " + tokens.size();
        for (String token : tokens) {
            assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
            GuardedSystemTransactionService.acceptTransaction(transId, token);
        }
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        closeServices();
    }

    protected void acceptPENDING_IMPACTED_PARTIES(RZMUser user, long transId, int tokenCount) throws Exception {
        setUser(user); //userAC
        TransactionVO trans = GuardedSystemTransactionService.get(transId);
        List<String> tokens = trans.getTokens();
        assert tokens.size() == tokenCount : "unexpected token count: " + tokens.size();
        for (String token : tokens) {
            assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
            GuardedSystemTransactionService.acceptTransaction(transId, token);
        }
        assert isTransactionInDesiredState("PENDING_MANUAL_REVIEW", transId);
        closeServices();
    }

    protected void acceptPENDING_CONTACT_CONFIRMATIONWrongToken(RZMUser firstUser, RZMUser secondUser, long transId) throws Exception {
        setUser(firstUser); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        GuardedSystemTransactionService.acceptTransaction(transId, "0");
        closeServices();
    }

    protected void acceptPENDING_CREATION(long transId) throws Exception {
        setDefaultUser();
        assert isTransactionInDesiredState("PENDING_CREATION", transId);
        GuardedSystemTransactionService.transitTransaction(transId, "go-on");
        closeServices();
    }

    protected Domain createDomain(String name) {
        Domain newDomain = new Domain(name);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        Contact tech = new Contact("tech");
        tech.setEmail("tech@" + name + ".org");
        newDomain.setTechContact(tech);
        Contact admin = new Contact("admin");
        admin.setEmail("admin@" + name + ".org");
        newDomain.setAdminContact(admin);
        return newDomain;
    }

    protected void setUser(RZMUser user) throws Exception {
        AuthenticatedUser authUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        GuardedSystemTransactionService.setUser(authUser);
        GuardedSystemDomainService.setUser(authUser);
        GuardedAdminTransactionServiceBean.setUser(authUser);
    }

    protected void setUser(String loginName) throws Exception {
        AuthenticatedUser authUser = authenticationServiceBean.authenticate(new PasswordAuth(loginName, ""));
        setUser(authUser);
    }

    protected void setUser(AuthenticatedUser authUser) {
        GuardedSystemTransactionService.setUser(authUser);
        GuardedSystemDomainService.setUser(authUser);
        GuardedAdminTransactionServiceBean.setUser(authUser);
        GuardedAdminDomainServiceBean.setUser(authUser);
        notificationService.setUser(authUser);
    }

    protected void setDefaultUser() throws Exception {
        setUser(defaultIana.getLoginName());
    }

    protected RZMUser getDefaultUser() {
        return defaultIana;
    }

    protected void closeServices() {
        GuardedSystemTransactionService.close();
        GuardedSystemDomainService.close();
        GuardedAdminTransactionServiceBean.close();
    }

    protected IDomainVO getDomain(String domainName, RZMUser user) throws Exception {
        setUser(user);
        return GuardedSystemDomainService.getDomain(domainName);
    }

    protected IDomainVO getDomain(String domainName) throws Exception {
        return getDomain(domainName, defaultIana);
    }

    protected TransactionVO createTransaction(IDomainVO domainVO, RZMUser user) throws Exception {
        return createTransaction(domainVO, user, null);
    }

    protected TransactionVO createTransaction(IDomainVO domainVO, RZMUser user, String submitterEmail) throws Exception {
        try {
            setUser(user);
            List<TransactionVO> transaction = GuardedSystemTransactionService.createTransactions(domainVO, false, submitterEmail);
            assert transaction != null;
            assert transaction.size() == 1;
            return transaction.iterator().next();
        } catch (CreateTicketException e) {
            // ignored
            return GuardedSystemTransactionService.get(e.getTransactionId());
        }
    }

    protected TransactionVO createTransaction(IDomainVO domainVO) throws Exception {
        return createTransaction(domainVO, defaultIana);
    }

    protected List<TransactionVO> createTransactions(IDomainVO domainVO, boolean splitNameServerChange) throws Exception {
        return GuardedSystemTransactionService.createTransactions(domainVO, splitNameServerChange);
    }

    protected void transitTransaction(long id, String transitionName) throws Exception {
        GuardedSystemTransactionService.transitTransaction(id, transitionName);
    }

    protected void updateTransaction(long id, Long ticketId, boolean redelegation) throws Exception {
        TransactionVO trans = new TransactionVO();
        trans.setTransactionID(id);
        trans.setTicketID(ticketId);
        trans.setRedelegation(redelegation);
        GuardedAdminTransactionServiceBean.updateTransaction(trans);
    }

    protected void transitTransactionToState(Long id, String state) throws Exception {
        GuardedAdminTransactionServiceBean.transitTransactionToState(id, state);
    }

    protected void acceptTransaction(long transactionId, String token) throws Exception {
        assert token != null;
        GuardedSystemTransactionService.acceptTransaction(transactionId, token);
    }

    protected TransactionVO getTransaction(long transactionId) throws Exception {
        return GuardedSystemTransactionService.get(transactionId);
    }

    private boolean isTransactionInDesiredState(String stateName, long transId) throws Exception {
        TransactionVO retTransactionVO = GuardedSystemTransactionService.get(transId);
        return retTransactionVO.getState().getName().toString().equals(stateName);
    }

    protected void checkStateLog(RZMUser user, Long transId, String[][] usersStates) throws Exception {
        setUser(user);
        TransactionVO trans = GuardedSystemTransactionService.get(transId);
        List<TransactionStateLogEntryVO> log = trans.getStateLog();
        assert log != null;
        assert log.size() == usersStates.length;
        int i = 0;
        for (TransactionStateLogEntryVO entry : log) {
            assert usersStates[i][0].equals(entry.getUserName()) :
                    "unexpected user in log entry: " + i + ", " + entry.getUserName();
            assert usersStates[i][1].equals(entry.getState().getName().toString()) :
                    "unexpected state in log entry: " + i + ", " + entry.getState().getName();
            i++;
        }
        closeServices();
    }

    protected Host setupFirstHost(String prefix) throws InvalidIPAddressException, InvalidDomainNameException {
        Host host = new Host(prefix + ".ns1.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("11.2.3.4"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    protected Host setupSecondHost(String prefix) throws InvalidIPAddressException, InvalidDomainNameException {
        Host host = new Host(prefix + ".ns2.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("21.2.3.5"));
        host.addIPAddress(IPAddress.createIPv6Address("2235:5678::90AB"));
        return host;
    }

    protected void assertPersistentNotifications(Long transId, String type, int count) throws Exception {
        List<NotificationVO> notifications = notificationService.getNotifications(transId);
        assert notifications != null : "notifications list is null";
        assert notifications.size() == count : "unexpected notifications count: " + notifications.size();
        for (NotificationVO notif : notifications) {
            if (type != null)
                assert NotificationConverter.isType(type, notif.getType()) :
                        "unexpected notification type: " + notif.getType();
            // assert notif.isPersistent() : "notification is not simple";
        }
    }

}
