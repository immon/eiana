package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.IPAddressVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"facade-system"})
public class TransactionPendingClarificationTest extends CommonGuardedSystemTransaction {

    protected void initTestData() {
        Domain domain1 = new Domain("withdraw");
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

    }

    @Test
    public void testTransitToPendingClarificationAndBackToUsDoC() throws Exception {
        IDomainVO domain = getDomain("withdraw");
        domain.setRegistryUrl("withdrawtest.registry.url");
        HostVO host = new HostVO("some.host");
        host.setAddress(new IPAddressVO("192.168.0.1", IPAddressVO.Type.IPv4));
        domain.setNameServers(Arrays.asList(host));

        setDefaultUser();
        TransactionVO trans = GuardedSystemTransactionService.createTransactions(domain, false).get(0);

        GuardedSystemTransactionService.moveTransactionToNextState(trans.getTransactionID());

        TransactionVO ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        String transactionState = ret.getState().getName().toString();
        assert TransactionStateVO.Name.PENDING_MANUAL_REVIEW.toString().equals(transactionState) : transactionState ;

        GuardedSystemTransactionService.moveTransactionToNextState(trans.getTransactionID());

        ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        transactionState = ret.getState().getName().toString();
        assert TransactionStateVO.Name.PENDING_IANA_CHECK.toString().equals(transactionState) : transactionState ;


        GuardedSystemTransactionService.moveTransactionToNextState(trans.getTransactionID());

        ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        transactionState = ret.getState().getName().toString();
        assert TransactionStateVO.Name.PENDING_USDOC_APPROVAL.toString().equals(transactionState) : transactionState ;

        GuardedSystemTransactionService.transitTransaction(trans.getTransactionID(), "withdraw");

        ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        transactionState = ret.getState().getName().toString();
        assert TransactionStateVO.Name.PENDING_CLARIFICATIONS.toString().equals(transactionState) : transactionState ;

        GuardedSystemTransactionService.moveTransactionToNextState(trans.getTransactionID());

        ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        transactionState = ret.getState().getName().toString();
        assert TransactionStateVO.Name.PENDING_USDOC_APPROVAL.toString().equals(transactionState) : transactionState ;

        closeServices();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
    }
}
