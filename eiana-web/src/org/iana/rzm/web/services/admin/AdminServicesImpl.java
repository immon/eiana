package org.iana.rzm.web.services.admin;

import org.apache.log4j.*;
import org.iana.codevalues.*;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.common.cc.*;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.facade.system.domain.types.*;
import org.iana.rzm.facade.system.notification.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.services.*;

import java.io.*;
import java.util.*;

public class AdminServicesImpl implements AdminServices, Serializable {

    private static final Logger LOGGER = Logger.getLogger(AdminServicesImpl.class);

    private AdminTransactionService transactionService;
    private AdminDomainService domainService;
    private AdminUserService userService;
    private CountryCodes countryCodeService;
    private DomainTypes domainTypesService;

    public AdminServicesImpl(ServiceInitializer initializer) {
        domainService = initializer.getBean("GuardedAdminDomainServiceBean");
        transactionService = initializer.getBean("GuardedAdminTransactionServiceBean");
        userService = initializer.getBean("GuardedAdminUserServiceBean");
        countryCodeService = initializer.getBean("cc", CountryCodes.class);
        domainTypesService = initializer.getBean("domainTypes", DomainTypes.class);
    }

    public int getTransactionCount(Criterion criterion) {
        return transactionService.count(criterion);
    }

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length) {
        List<TransactionVO> list = transactionService.find(criterion, offset, length);
        List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
        for (TransactionVO transactionVO : list) {
            result.add(new TransactionVOWrapper(transactionVO));
        }
        return result;
    }

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain) throws NoObjectFoundException {
        try {
            TransactionActionsVO vo = transactionService.detectTransactionActions(domain.getDomainVO());
            return new TransactionActionsVOWrapper(vo);
        } catch (NoTransactionException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new NoObjectFoundException(e.getId(), "Request");
        } catch (InfrastructureException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> createDomainModificationTrunsaction(DomainVOWrapper domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException,
                                                                                                                                                               InvalidCountryCodeException,
                                                                                                                                                               CreateTicketException {
        try {
            List<TransactionVO> list = transactionService.createDomainModificationTransactions(domain.getDomainVO(), splitNameServerChange, submitterEmail);
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
            transactionService.updateTransaction(transaction.getId(), transaction.getRtId(), transaction.getState().getVOName(), transaction.isRedeligation());
        } catch (NoTransactionException e) {
            throw new RzmServerException("Can not find Transaction with id " + e.getId());
        } catch (FacadeTransactionException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new RzmApplicationException(e);
        } catch (StateUnreachableException e) {
            throw new RzmServerException("Transaction State " + transaction.getCurrentStateAsString() + " is Unreachable ");
        }
    }

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException {
        try {
            TransactionVO vo = transactionService.getTransaction(id);
            return new TransactionVOWrapper(vo);
        } catch (NoTransactionException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new NoObjectFoundException(e.getId(), "Request");
        }

    }

    public TransactionVOWrapper getTransactionByRtId(long rtId) {
        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.addTickedId(rtId);
        List<TransactionVO> list = transactionService.find(criteria);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new TransactionVOWrapper(list.get(0));
    }


    public List<DomainVOWrapper> getDomains(int offset, int length) {
        List<IDomainVO> list = domainService.find(new Order("name.name", true), offset, length);
        List<DomainVOWrapper> result = new ArrayList<DomainVOWrapper>();
        for (IDomainVO iDomainVO : list) {
            result.add(new SystemDomainVOWrapper(iDomainVO));
        }

        return result;
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

    public int getDomainsCount() {
        return domainService.count(null);
    }

    public void updateDomain(DomainVOWrapper domain) {
        domainService.updateDomain(domain.getDomainVO());
    }

    public List<UserVOWrapper> getUsers(Criterion criterion, int offset, int length) {
        List<UserVO> list = userService.find(criterion, new Order("loginName", true), offset, length);
        List<UserVOWrapper> users = new ArrayList<UserVOWrapper>();
        for (UserVO userVO : list) {
            users.add(new UserVOWrapper(userVO));
        }

        return users;
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
        return userService.count(criterion);
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


    public void changePassword(long userId, String newPassword) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<NotificationVOWrapper> getNotifications(long requestId) {
        List<NotificationVOWrapper> result = new ArrayList<NotificationVOWrapper>();
        try {
            List<NotificationVO> list = transactionService.getNotifications(requestId);
            for (NotificationVO notificationVO : list) {
                result.add(new NotificationVOWrapper(notificationVO));
            }
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
        return result;
    }

}
