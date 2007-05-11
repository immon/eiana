package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.*;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

public abstract class CommonGuardedSystemTransaction {

    protected ApplicationContext appCtx;

    protected ProcessDAO processDAO;
    protected UserManager userManager;
    protected DomainManager domainManager;

    protected SystemTransactionService gsts;
    protected SystemDomainService gsds;

    protected void init() {
        appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        gsts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        gsds = (SystemDomainService) appCtx.getBean("GuardedSystemDomainService");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
    }
   
    protected void acceptZONE_PUBLICATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);     //iana
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        gsts.close();
    }

    protected void acceptZONE_INSERTION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //iana
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        gsts.close();
    }

    protected void acceptUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);   //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        gsts.close();
    }

    protected void acceptUSDOC_APPROVALnoNSChange(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        gsts.close();
    }

    protected void acceptEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.close();
    }

    protected void normalIANA_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION", transId);
        gsts.transitTransaction(transId, "normal");
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.close();
    }

    protected void acceptIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION", transId);
        gsts.close();
    }

    protected void rejectPENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    protected void closeEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        gsts.close();
    }

    protected void rejectEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    protected void rejectUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    protected void rejectIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    protected void closeIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        gsts.close();
    }

    protected void closePENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        gsts.close();
    }

    protected void acceptPENDING_CONTACT_CONFIRMATION(RZMUser firstUser, RZMUser secondUser, long transId) throws Exception {
        setGSTSAuthUser(firstUser); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.close();
        setGSTSAuthUser(secondUser); //userTC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.close();
    }

    protected Domain createDomain(String name) {
        Domain newDomain = new Domain(name);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
    }

    protected void setGSTSAuthUser(RZMUser user) {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gsts.setUser(testAuthUser);
    }

    protected IDomainVO getDomain(String domainName, RZMUser user) throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        try {
            gsds.setUser(testAuthUser);
            return gsds.getDomain(domainName);
        } finally {
            gsds.close();
        }
    }

    protected TransactionVO createTransaction(IDomainVO domainVO, RZMUser user) throws Exception {
//        domainManager.delete(domain);
//        domainManager.create(domain);
        setGSTSAuthUser(user);  //userAC
        List<TransactionVO> transaction = gsts.createTransactions(domainVO, false);

        gsts.close();
        return transaction.iterator().next();
    }

    private boolean isTransactionInDesiredState(String stateName, long transId) throws Exception {
        TransactionVO retTransactionVO = gsts.getTransaction(transId);
        return retTransactionVO.getState().getName().toString().equals(stateName);
    }

    protected void checkStateLog(RZMUser user, Long transId, String [][] usersStates) throws Exception {
        setGSTSAuthUser(user);
        TransactionVO trans = gsts.getTransaction(transId);
        List<TransactionStateLogEntryVO> log = trans.getStateLog();
        assert log != null;
        assert log.size() == usersStates.length;
        int i = 0;
        for (TransactionStateLogEntryVO entry : log) {
            assert usersStates[i][0].equals(entry.getUserName()) :
                    "unexpected user in log entry: " + i + ", " + usersStates[i][0];
            assert usersStates[i][1].equals(entry.getState().getName().toString()) :
                    "unexpected state in log entry: " + i + ", " + usersStates[i][1];
            i++;
        }
    }

    protected Host setupFirstHost(String prefix) throws InvalidIPAddressException, InvalidNameException {
        Host host = new Host(prefix + ".ns1.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("11.2.3.4"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    protected Host setupSecondHost(String prefix) throws InvalidIPAddressException, InvalidNameException {
        Host host = new Host(prefix + ".ns2.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("21.2.3.5"));
        host.addIPAddress(IPAddress.createIPv6Address("2235:5678::90AB"));
        return host;
    }
}
