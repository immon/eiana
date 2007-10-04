package org.iana.rzm.web.services.admin;

import org.apache.log4j.*;
import org.iana.codevalues.*;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.domain.AdminDomainService;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.admin.trans.StateUnreachableException;
import org.iana.rzm.facade.admin.trans.AdminNotificationService;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.common.cc.*;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.types.*;
import org.iana.rzm.facade.system.notification.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionsVO;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.services.*;
import org.iana.rzm.web.util.*;

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

    public AdminServicesImpl(ServiceInitializer initializer) {
        domainService = initializer.getBean("GuardedAdminDomainServiceBean");
        transactionService = initializer.getBean("GuardedAdminTransactionServiceBean");
        notificationService = initializer.getBean("transactionNotificationService");
        userService = initializer.getBean("GuardedAdminUserServiceBean");
        detectorService = initializer.getBean("adminDetectorService");
        countryCodeService = initializer.getBean("cc", CountryCodes.class);
        domainTypesService = initializer.getBean("domainTypes", DomainTypes.class);
    }

    public int getTransactionCount(Criterion criterion) {
        try {
            return transactionService.count(criterion);
        } catch (InfrastructureException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length) {
        try {
            List<TransactionVO> list = transactionService.find(criterion, offset, length);
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

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain) throws NoObjectFoundException {
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

    public List<TransactionVOWrapper> createDomainModificationTrunsaction(DomainVOWrapper domain, boolean splitNameServerChange, RequestMetaParameters params) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException,
            InvalidCountryCodeException,
            CreateTicketException {

        //todo Nask need to add method include comment and boolean field for performTechCheck
        try {
            List<TransactionVO> list = transactionService.createTransactions(domain.getDomainVO(), splitNameServerChange, params.getEmail());
            List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transactionVO : list) {
                TransactionVOWrapper o = new TransactionVOWrapper(transactionVO);
                o.setComment(params.getComment());
                //todo Nask need to add a way to update comment
                result.add(o);
            }
            return result;
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public void updateTransaction(TransactionVOWrapper transaction) throws RzmServerException {

        try {
            //todo Nask need to add a way to update comment
            transactionService.updateTransaction(transaction.getVO());
        } catch (NoObjectFoundException e) {
            throw new RzmServerException("Can not find Transaction with id " + e.getId());
        } catch (FacadeTransactionException e) {
            LOGGER.warn("NoObjectFoundException", e);
            throw new RzmApplicationException(e);
        } catch (StateUnreachableException e) {
            throw new RzmServerException("Transaction State " + transaction.getCurrentStateAsString() + " is Unreachable ");
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


    public List<DomainVOWrapper> getDomains(int offset, int length) {
        try {
            List<IDomainVO> list = domainService.find(new Order("name.name", true), offset, length);
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
        try {
            return domainService.count(null);
        } catch (InfrastructureException e) {
            LOGGER.warn("NoTransactionException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void updateDomain(DomainVOWrapper domain) {
        domainService.updateDomain(domain.getDomainVO());
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

    public void sendNotification(long requestId, NotificationVOWrapper notification, String comment) throws FacadeTransactionException {
        try {
            notificationService.resendNotification(requestId, notification.getType().voType(), comment);
        } catch (InfrastructureException e) {
            LOGGER.warn("Infrastructure Exception", e);
            throw new RzmApplicationException(e);
        }
    }

    public void changePassword(long userId, String newPassword) {
        //To change body of implemented methods use File | Settings | File Templates.
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
