package org.iana.rzm.web.admin.services;

import org.iana.codevalues.*;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.DNSTechnicalCheckExceptionWrapper;
import org.iana.rzm.web.admin.*;
import org.iana.rzm.web.admin.model.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.services.*;
import org.iana.rzm.web.tapestry.services.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 8, 2007
 * Time: 5:10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AdminServices extends RzmServices, WhoisDataProducer, ZoneProducer {

    List<TransactionVOWrapper> createDomainModificationTrunsaction(DomainVOWrapper domain, boolean splitNameServerChange, RequestMetaParameters params, boolean checkRadicalChanges)
        throws
        AccessDeniedException,
        NoObjectFoundException,
        NoDomainModificationException,
        InvalidCountryCodeException,
        DNSTechnicalCheckExceptionWrapper,
        TransactionExistsException,
        NameServerChangeNotAllowedException,
        SharedNameServersCollisionException,
        RadicalAlterationException;


    public void updateTransaction(TransactionVOWrapper transaction) throws RzmServerException;

    public TransactionVOWrapper getTransactionByRtId(long rtId);

    public int getDomainsCount();

    public void updateDomain(DomainVOWrapper domain);

    public void createDomain(DomainVOWrapper domain);

    public List<DomainVOWrapper> getDomains(int offset, int length, SortOrder sort);

    public List<UserVOWrapper> getUsers(Criterion criterion, int offset, int length);

    public int getUserCount(Criterion criterion);

    public void deleteUser(long userId);

    public UserVOWrapper getUser(long userId);

    public UserVOWrapper getUser(String userName);

    public void createUser(UserVOWrapper user);

    public void updateUser(UserVOWrapper user);

    public List<NotificationVOWrapper> getNotifications(long requestId);

    public Set<Value> getDomainTypes();

    public void sendNotification(long transactionId, NotificationVOWrapper vo, String comment, String email)throws FacadeTransactionException;

    public void moveTransactionNextState(long id) throws NoObjectFoundException, IllegalTransactionStateException;

    public void transitTransactionToState(long id, TransactionStateVOWrapper.State state) throws FacadeTransactionException, NoObjectFoundException, NoSuchStateException, StateUnreachableException;

    public void approveByUSDoC(long transactionId) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException;

    public void rejectByUSDoC(long transactionId) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException;

    public List<DomainVOWrapper> getDomains(Criterion criterion);

    public List<PollMessageVOWrapper> getPollMessagesbyRtId(long rtId) throws NoObjectFoundException;

    public List<PollMessageVOWrapper>getPollMessages(Criterion criterion, int offset, int length, SortOrder sort);

    public int getPollMessagesCount(Criterion criterion); 

    public String getVerisignStatus(long rtId) throws NoObjectFoundException, InvalidEPPTransactionException;

    public List<String> getDomainNames();

    public void deletePollMessage(long id) throws NoObjectFoundException;

    public PollMessageVOWrapper getPollMessage(long id) throws NoObjectFoundException;

    boolean isDomainExist(String domainName);
}
