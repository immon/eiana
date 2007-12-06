package org.iana.rzm.facade.system.trans;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.dao.EmailAddresseeDAO;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.admin.domain.AdminDomainService;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateLogEntryVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 * @author: JaKub Laszkiewicz
 */

public abstract class CommonGuardedSystemTransaction {
    protected ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
    protected ProcessDAO processDAO = (ProcessDAO) appCtx.getBean("processDAO");
    protected UserManager userManager = (UserManager) appCtx.getBean("userManager");
    protected DomainManager domainManager = (DomainManager) appCtx.getBean("domainManager");
    protected NotificationManager notificationManagerBean =
            (NotificationManager) appCtx.getBean("NotificationManagerBean");
    protected TransactionManager transactionManagerBean =
            (TransactionManager) appCtx.getBean("transactionManagerBean");
    protected TransactionService gsts =
            (TransactionService) appCtx.getBean("GuardedSystemTransactionService");
    protected SystemDomainService gsds =
            (SystemDomainService) appCtx.getBean("GuardedSystemDomainService");
    protected AdminDomainService ads =
            (AdminDomainService) appCtx.getBean("GuardedAdminDomainServiceBean");
    protected EmailAddresseeDAO emailAddresseeDAO = (EmailAddresseeDAO) appCtx.getBean("emailAddresseeDAO");
    protected RZMUser defaultIana;
    protected AdminTransactionService ats = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
    protected AuthenticationService authService = (AuthenticationService) appCtx.getBean("authenticationServiceBean");

    @BeforeClass
    public void commonInit() {
        defaultIana = new RZMUser("fn", "ln", "org", "default-iana", "iana@nowhere", "", false);
        defaultIana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(defaultIana);
    }

    protected void acceptZONE_PUBLICATION(RZMUser user, long transId) throws Exception {
        setUser(user);     //iana
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        ats.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        closeServices();
    }

    protected void acceptZONE_INSERTION(RZMUser user, long transId) throws Exception {
        setUser(user); //iana
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        ats.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        closeServices();
    }

    protected void acceptUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);   //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        closeServices();
    }

    protected void acceptUSDOC_APPROVALnoNSChange(RZMUser user, long transId) throws Exception {
        setUser(user); //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        closeServices();
    }

    protected void acceptEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        closeServices();
    }

    protected void acceptMANUAL_REVIEW(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_MANUAL_REVIEW", transId);
        gsts.transitTransaction(transId, "accept");
        assert isTransactionInDesiredState("PENDING_IANA_CHECK", transId);
        closeServices();
    }

    protected void acceptIANA_CHECK(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_IANA_CHECK", transId);
        gsts.transitTransaction(transId, "accept");
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        closeServices();
    }

    protected void acceptIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.moveTransactionToNextState(transId);
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION", transId);
        closeServices();
    }

    protected void rejectPENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        TransactionVO trans = gsts.get(transId);
        List<String> tokens = trans.getTokens();
        assert tokens.size() > 0;
        gsts.rejectTransaction(transId, tokens.iterator().next());
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void rejectPENDING_CONTACT_CONFIRMATIONWrongToken(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.rejectTransaction(transId, "0");
        closeServices();
    }

    protected void closeEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        closeServices();
    }

    protected void rejectEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void rejectUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setUser(user);  //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void rejectIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setUser(user);  //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        closeServices();
    }

    protected void closeIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        closeServices();
    }

    protected void closePENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        closeServices();
    }

    protected void acceptPENDING_CONTACT_CONFIRMATION(RZMUser user, long transId, int tokenCount) throws Exception {
        setUser(user); //userAC
        TransactionVO trans = gsts.get(transId);
        List<String> tokens = trans.getTokens();
        assert tokens.size() == tokenCount : "unexpected token count: " + tokens.size();
        for (String token : tokens) {
            assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
            gsts.acceptTransaction(transId, token);
        }
        assert isTransactionInDesiredState("PENDING_MANUAL_REVIEW", transId);
        closeServices();
    }

    protected void acceptPENDING_CONTACT_CONFIRMATIONWrongToken(RZMUser firstUser, RZMUser secondUser, long transId) throws Exception {
        setUser(firstUser); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.acceptTransaction(transId, "0");
        closeServices();
    }

    protected void acceptPENDING_CREATION(long transId) throws Exception {
        setDefaultUser();
        assert isTransactionInDesiredState("PENDING_CREATION", transId);
        gsts.transitTransaction(transId, "go-on");
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
        gsts.setUser(authUser);
        gsds.setUser(authUser);
        ats.setUser(authUser);
    }

    protected void setUser(String loginName) throws Exception {
        AuthenticatedUser authUser = authService.authenticate(new PasswordAuth(loginName, ""));
        gsts.setUser(authUser);
        gsds.setUser(authUser);
        ats.setUser(authUser);
        ads.setUser(authUser);
    }

    protected void setDefaultUser() throws Exception {
        setUser(defaultIana.getLoginName());
    }

    protected RZMUser getDefaultUser() {
        return defaultIana;
    }

    protected void closeServices() {
        gsts.close();
        gsds.close();
        ats.close();
    }

    protected IDomainVO getDomain(String domainName, RZMUser user) throws Exception {
        setUser(user);
        return gsds.getDomain(domainName);
    }

    protected IDomainVO getDomain(String domainName) throws Exception {
        return getDomain(domainName, defaultIana);
    }

    protected TransactionVO createTransaction(IDomainVO domainVO, RZMUser user) throws Exception {
        try {
            setUser(user);
            List<TransactionVO> transaction = gsts.createTransactions(domainVO, false);
            assert transaction != null;
            assert transaction.size() == 1;
            return transaction.iterator().next();
        } catch (CreateTicketException e) {
            // ignored
            return gsts.get(e.getTransactionId());
        }
    }

    protected TransactionVO createTransaction(IDomainVO domainVO) throws Exception {
        return createTransaction(domainVO, defaultIana);
    }

    protected List<TransactionVO> createTransactions(IDomainVO domainVO, boolean splitNameServerChange) throws Exception {
        return gsts.createTransactions(domainVO, splitNameServerChange);
    }

    protected void transitTransaction(long id, String transitionName) throws Exception {
        gsts.transitTransaction(id, transitionName);
    }

    protected void updateTransaction(long id, Long ticketId, boolean redelegation) throws Exception {
        TransactionVO trans = new TransactionVO();
        trans.setTransactionID(id);
        trans.setTicketID(ticketId);
        trans.setRedelegation(redelegation);
        ats.updateTransaction(trans);
    }

    protected void transitTransactionToState(Long id, String state) throws Exception {
        ats.transitTransactionToState(id, state);
    }

    protected void acceptTransaction(long transactionId, String token) throws Exception {
        assert token != null;
        gsts.acceptTransaction(transactionId, token);
    }

    protected TransactionVO getTransaction(long transactionId) throws Exception {
        return gsts.get(transactionId);
    }

    private boolean isTransactionInDesiredState(String stateName, long transId) throws Exception {
        TransactionVO retTransactionVO = gsts.get(transId);
        return retTransactionVO.getState().getName().toString().equals(stateName);
    }

    protected void checkStateLog(RZMUser user, Long transId, String[][] usersStates) throws Exception {
        setUser(user);
        TransactionVO trans = gsts.get(transId);
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
}
