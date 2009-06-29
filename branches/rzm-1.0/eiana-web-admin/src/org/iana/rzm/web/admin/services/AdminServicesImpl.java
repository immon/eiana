package org.iana.rzm.web.admin.services;

import org.apache.log4j.*;
import org.iana.codevalues.*;
import org.iana.commons.*;
import org.iana.criteria.*;
import org.iana.dns.check.*;
import org.iana.notifications.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.domain.*;
import org.iana.rzm.facade.admin.domain.dns.*;
import org.iana.rzm.facade.admin.msgs.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.admin.trans.notifications.*;
import org.iana.rzm.facade.admin.users.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.common.cc.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.facade.services.*;
import org.iana.rzm.facade.system.domain.types.*;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.system.notification.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.DNSTechnicalCheckExceptionWrapper;
import org.iana.rzm.facade.system.trans.vo.*;
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.admin.*;
import org.iana.rzm.web.admin.model.*;
import org.iana.rzm.web.admin.query.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.technical_check.DNSTechnicalCheckErrorsXmlParser;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.resolver.*;
import org.iana.rzm.web.common.utils.*;
import org.iana.web.tapestry.services.*;

import java.io.*;
import java.util.*;

public class AdminServicesImpl implements AdminServices, Serializable {

    private static final Logger LOGGER = Logger.getLogger(AdminServicesImpl.class);

    private AdminTransactionService transactionService;
    private AdminNotificationService notificationService;
    private AdminDomainService domainService;
    private AdminUserService userService;
    private TransactionDetectorService detectorService;
    private CountryCodes countryCodeService;
    private DomainTypes domainTypesService;
    private AdminDNSService dnsServices;
    private PollMessagesService pollMessagesService;
    private DNSTechnicalCheckErrorsXmlParser technicalErrorsXmlParser;
    

    private PasswordChangeService changePasswordService;

    public AdminServicesImpl(ServiceInitializer<RZMStatefulService> initializer) {
        domainService = initializer.getBean("remoteGuardedAdminDomainServiceBean");
        transactionService = initializer.getBean("remoteGuardedAdminTransactionServiceBean");
        notificationService = initializer.getBean("remoteNotificationService");
        userService = initializer.getBean("remoteGuardedAdminUserServiceBean");
        detectorService = initializer.getBean("remoteAdminDetectorService");
        pollMessagesService = initializer.getBean("remotePollMessagesService");
        countryCodeService = initializer.getBean("remoteCc", CountryCodes.class);
        domainTypesService = initializer.getBean("remoteDomainTypes", DomainTypes.class);
        changePasswordService = initializer.getBean("remotePasswordChangeService", PasswordChangeService.class);
        dnsServices = initializer.getBean("remoteDnsService", AdminDNSService.class);
        technicalErrorsXmlParser = initializer.getBean("technicalErrorsXmlParser", DNSTechnicalCheckErrorsXmlParser.class);

    }

    public int getTransactionCount(Criterion criterion) {
        try {
            return transactionService.count(criterion);
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length, SortOrder sort) {
        try {
            List<TransactionVO> list;
            if (sort.isValid()) {
                Order order =
                    new Order(new RequestFieldNameResolver().resolve(sort.getFieldName()), sort.isAscending());
                list = transactionService.find(criterion, order, offset, length);
            } else {
                list = transactionService.find(criterion, offset, length);
            }

            List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transactionVO : list) {
                result.add(new TransactionVOWrapper(transactionVO));
            }
            return result;
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }
    }

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain)
        throws NoObjectFoundException, SharedNameServersCollisionException, RadicalAlterationException {
        try {
            TransactionActionsVO vo = detectorService.detectTransactionActions(domain.getDomainVO());
            return new TransactionActionsVOWrapper(vo);
        } catch (NoObjectFoundException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new NoObjectFoundException(e.getId(), "Request");
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void withdrawnTransaction(long requestId)
        throws
        NoObjectFoundException,
        InfrastructureException,
        TransactionCannotBeWithdrawnException {
        transactionService.withdrawTransaction(requestId);
    }

     public List<Value> getCountrys() {
        List<Value> list = countryCodeService.getCountries();
        Collections.sort(list, new CountryCodeSorter());
        return list;
    }

    public boolean isValidCountryCode(String code) {
        List<Value> list = countryCodeService.getCountries();
            for (Value value : list) {
                if (value.getValueId().equals(code)) {
                    return true;
                }
            }

            return false;
    }

    public List<String> parseErrors(String technicalErrors) {
        return technicalErrorsXmlParser.getTechnicalCheckErrors(technicalErrors);
    }

    public List<TransactionVOWrapper> createDomainModificationTrunsaction(DomainVOWrapper domain,
                                                                          boolean splitNameServerChange,
                                                                          RequestMetaParameters params)
        throws
        AccessDeniedException,
        NoObjectFoundException,
        NoDomainModificationException,
        InvalidCountryCodeException,
            DNSTechnicalCheckExceptionWrapper,
        TransactionExistsException,
        NameServerChangeNotAllowedException,
        SharedNameServersCollisionException,
        RadicalAlterationException {
        try {
            List<TransactionVO> list =
                transactionService.createTransactions(domain.getDomainVO(),
                                                      splitNameServerChange,
                                                      params.getEmail(),
                                                      false,
                                                      params.getComment());
            List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transactionVO : list) {
                result.add(new TransactionVOWrapper(transactionVO));
            }
            return result;
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public void updateTransaction(TransactionVOWrapper transaction) throws RzmServerException {

        try {
            transactionService.updateTransaction(transaction.getVO());
        } catch (NoObjectFoundException e) {
            throw new RzmServerException("Can not find Transaction with id " + e.getId());
        }
        catch (StateUnreachableException e) {
            throw new RzmServerException("Transaction State " +
                                         transaction.getCurrentStateAsString() +
                                         " is Unreachable ");
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException {
        try {
            TransactionVO vo = transactionService.get(id);
            return new TransactionVOWrapper(vo);
        } catch (NoObjectFoundException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new NoObjectFoundException(e.getId(), "Request");
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }

    }

    public TransactionVOWrapper getTransactionByRtId(long rtId) {
        try {
            Criterion tickedId = new Equal(TransactionCriteriaFields.TICKET_ID, rtId);
            List<TransactionVO> list = transactionService.find(tickedId);
            if (list == null || list.isEmpty()) {
                return null;
            }

            return new TransactionVOWrapper(list.get(0));
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }
    }


    public List<DomainVOWrapper> getDomains(int offset, int length, SortOrder sortOrder) {
        try {
            Order order = new Order(new DomainFieldNameResolver().resolve("domainName"));
            if (sortOrder.isValid()) {
                order = new Order(sortOrder.getFieldName(), sortOrder.isAscending());
            }
            List<IDomainVO> list = domainService.find(order, offset, length);
            List<DomainVOWrapper> result = new ArrayList<DomainVOWrapper>();
            for (IDomainVO iDomainVO : list) {
                result.add(new SystemDomainVOWrapper(iDomainVO));
            }

            return result;
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<DomainVOWrapper> getDomains(Criterion criterion) {
        try {
            List<IDomainVO> list =
                domainService.find(criterion, new Order(new DomainFieldNameResolver().resolve("domainName")), 0, 300);
            List<DomainVOWrapper> result = new ArrayList<DomainVOWrapper>();
            for (IDomainVO iDomainVO : list) {
                result.add(new SystemDomainVOWrapper(iDomainVO));
            }

            return result;

        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException {
        IDomainVO vo = domainService.getDomain(domainId);
        return new SystemDomainVOWrapper(vo);
    }

    public SystemDomainVOWrapper getDomain(String domainName) throws NoObjectFoundException {
        try {
            IDomainVO vo = domainService.getDomain(domainName.toLowerCase().trim());
            return new SystemDomainVOWrapper(vo);
        } catch (IllegalArgumentException e) {
            throw new NoObjectFoundException(domainName, "domain");
        }
    }

    public boolean isDomainExist(String domainName){
        try {
            return domainService.count(QueryBuilderUtil.domainsByName(domainName.toLowerCase())) >= 1;
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public int getDomainsCount() {
        try {
            return domainService.count(null);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void updateDomain(DomainVOWrapper domain) {
        domainService.updateDomain(domain.getDomainVO());
    }

    public void createDomain(DomainVOWrapper domain) {
        domainService.createDomain(domain.getDomainVO());
    }

    public List<UserVOWrapper> getUsers(Criterion criterion, int offset, int length) {
        try {
            List<UserVO> list = userService.find(criterion, new Order("loginName", true), offset, length);
            List<UserVOWrapper> users = new ArrayList<UserVOWrapper>();
            for (UserVO userVO : list) {
                users.add(new UserVOWrapper(userVO));
            }

            return users;
        } catch (InfrastructureException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new RzmApplicationException(e);
        }
    }

    public UserVOWrapper getUser(long userId) {
        return new UserVOWrapper(userService.getUser(userId));
    }

    public UserVOWrapper getUser(String userName) {
        try {
            UserVO vo = userService.getUser(userName);
            return new UserVOWrapper(vo);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public int getUserCount(Criterion criterion) {
        try {
            return userService.count(criterion);
        } catch (InfrastructureException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void deleteUser(long userId) {
        userService.deleteUser(userId);
    }


    public void createUser(UserVOWrapper user) {
        userService.createUser(user.getVo());
    }

    public void updateUser(UserVOWrapper user) {
        userService.updateUser(user.getVo());
    }

    public String getCountryName(String domainCode) {
        String code = countryCodeService.getCountryName(domainCode.toUpperCase());
        if (code == null) {
            return "Top Level Domain";
        }
        return code;
    }

    public Set<Value> getDomainTypes() {
        return domainTypesService.getDomainTypes();
    }

    public void sendNotification(long requestId, NotificationVOWrapper notification, String comment, String email)
        throws FacadeTransactionException {
        try {
            notificationService.resendNotification(requestId, notification.getType().voType(), comment, email);
        } catch (InfrastructureException e) {
            if (NotificationSenderException.class.equals(e.getCause())) {
                LOGGER.warn("Notification Exception", e.getCause());
                throw new RzmApplicationError(e, e.getCause().getMessage());
            }
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public void transitTransactionToState(long id, TransactionStateVOWrapper.State state)
        throws FacadeTransactionException, NoObjectFoundException, NoSuchStateException, StateUnreachableException {
        transactionService.transitTransactionToState(id, state.getVOName());
    }

    public void approveByUSDoC(long transactionId)
        throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException {
        try {
            transactionService.approveByUSDoC(transactionId);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public void rejectByUSDoC(long transactionId)
        throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException {
        try {
            transactionService.rejectByUSDoC(transactionId);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }


    public void moveTransactionNextState(long id)
        throws AccessDeniedException, NoObjectFoundException, IllegalTransactionStateException {
        try {
            transactionService.moveTransactionToNextState(id);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword)
        throws PasswordChangeException {
        try {
            changePasswordService.changePassword(username, oldPassword, newPassword, confirmedNewPassword);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<NotificationVOWrapper> getNotifications(long requestId) {
        List<NotificationVOWrapper> result = new ArrayList<NotificationVOWrapper>();
        try {
            List<NotificationVO> list = notificationService.getNotifications(requestId);
            ListUtil.filter(list, new DuplicateNotitificationType());

            for (NotificationVO notificationVO : list) {
                result.add(new NotificationVOWrapper(notificationVO));
            }
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
        return result;
    }

    public String getWhoisData() {
        try {
            return domainService.saveDomainsToXML();
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public String getZoneData() {
        try {
            return dnsServices.exportZoneFile();
        }
        catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<PollMessageVOWrapper> getPollMessagesbyRtId(long rtId) throws NoObjectFoundException {
        try {
            Criterion criterion = AdminQueryUtil.pollMessagesByRtId(rtId);
            List<PollMsgVO> list = pollMessagesService.find(criterion, new Order(PollMsgFields.CREATED, true), 0, 25);
            List<PollMessageVOWrapper> result = new ArrayList<PollMessageVOWrapper>(list.size());

            for (PollMsgVO pollMsgVO : list) {
                result.add(new PollMessageVOWrapper(pollMsgVO));
            }

            return result;

        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);

        }
    }

    public List<PollMessageVOWrapper> getPollMessages(Criterion criterion, int offset, int length, SortOrder sort) {

        try {
            List<PollMsgVO> list;
            if (sort.isValid()) {
                Order order =
                    new Order(new PollMsgFieldNameResolver().resolve(sort.getFieldName()), sort.isAscending());
                list = pollMessagesService.find(criterion, order, offset, length);
            } else {
                list = pollMessagesService.find(criterion, offset, length);
            }

            List<PollMessageVOWrapper> result = new ArrayList<PollMessageVOWrapper>(list.size());
            for (PollMsgVO pollMsgVO : list) {
                result.add(new PollMessageVOWrapper(pollMsgVO));
            }

            return result;
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public int getPollMessagesCount(Criterion criterion) {
        try {
            return pollMessagesService.count(criterion);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);

        }
    }

    public String getVerisignStatus(long rtId) throws NoObjectFoundException, InvalidEPPTransactionException {
        try {
            return transactionService.queryTransactionEPPStatus(rtId);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<String> getDomainNames() {
        List<DomainVOWrapper> list = getDomains(QueryBuilderUtil.empty());
        List<String>results = new ArrayList<String>(list.size());
        for (DomainVOWrapper domainVOWrapper : list) {
            results.add(domainVOWrapper.getName());
        }

        return results;
    }

    public void deletePollMessage(long id) throws NoObjectFoundException {
        try {
            pollMessagesService.delete(id);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public PollMessageVOWrapper getPollMessage(long id) throws NoObjectFoundException {
        try {
            return new PollMessageVOWrapper(pollMessagesService.get(id));
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }



    private static class DuplicateNotitificationType implements ListUtil.Predicate<NotificationVO> {

        private int contact = 0;

        private int doc = 0;

        public boolean evaluate(NotificationVO object) {
            if (object.getType().equals(NotificationVO.Type.CONTACT_CONFIRMATION)) {
                boolean result = contact > 0;
                contact++;
                return result;
            }
            if (object.getType().equals(NotificationVO.Type.USDOC_CONFIRMATION)) {
                boolean result = doc > 0;
                doc++;
                return result;
            }
            return false;
        }

    }

}
