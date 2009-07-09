package org.iana.rzm.web.admin.services;

import org.iana.codevalues.Value;
import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.admin.trans.InvalidEPPTransactionException;
import org.iana.rzm.facade.admin.trans.NoSuchStateException;
import org.iana.rzm.facade.admin.trans.StateUnreachableException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.admin.RzmServerException;
import org.iana.rzm.web.admin.model.*;
import org.iana.rzm.web.common.RequestMetaParameters;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.SortOrder;
import org.iana.rzm.web.common.services.RzmServices;
import org.iana.rzm.web.tapestry.services.WhoisDataProducer;
import org.iana.rzm.web.tapestry.services.ZoneProducer;

import java.util.List;
import java.util.Set;

public interface AdminServices extends RzmServices, WhoisDataProducer, ZoneProducer {

    List<TransactionVOWrapper> createDomainModificationTrunsaction(DomainVOWrapper domain, boolean splitNameServerChange, RequestMetaParameters params, boolean useRadicalChangeCheck)
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

    public SmtpConfigVOWrapper getSmtpConfigSettings();

    public POP3ConfigVOWrapper getPop3ConfigSettings();

    public VersignConfigVOWrpper getVerisignConfigSettings();

    public void setApplicationConfiguration(ApplicationConfig config);

    public USDoCConfigVOWrapper getDoCConfigSettings();

    public PgpConfigVOWrapper getPgpConfigSettings();

    EmailTemplateVOWrapper getEmailTemplate(String templateName);

    void saveTemplate(EmailTemplateVOWrapper vo);
}
