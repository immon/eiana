package org.iana.rzm.web.services.admin;

import org.apache.log4j.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.admin.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.common.cc.*;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.services.*;

import java.util.*;

public class AdminServicesImpl implements AdminServices {

    private static final Logger LOGGER = Logger.getLogger(AdminServicesImpl.class);

    private AdminTransactionService transactionService;
    private AdminDomainService domainService;
    private AdminUserService userService;
    private CountryCodes countryCodeService;

    public AdminServicesImpl(ServiceInitializer initializer) {
        domainService = initializer.getBean("GuardedAdminDomainServiceBean");
        transactionService = initializer.getBean("GuardedAdminTransactionServiceBean");
        userService = initializer.getBean("GuardedAdminUserServiceBean");
        countryCodeService = initializer.getBean("cc", CountryCodes.class);
    }

    public int getTransactionCount(Criterion criterion) {
        return transactionService.count(criterion);
    }

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length) {
        List<TransactionVO> list = transactionService.find(criterion, offset, length);
        List<TransactionVOWrapper>result = new ArrayList<TransactionVOWrapper>();
        for (TransactionVO transactionVO : list) {
            result.add(new TransactionVOWrapper(transactionVO));
        }
        return result;
    }

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain) {
        return new TransactionActionsVOWrapper(new TransactionActionsVO());
    }

    public void updateTransaction(TransactionVOWrapper transaction) throws RzmServerException {

        try {
            transactionService.updateTransaction(transaction.getId(),
                    transaction.getRtId(), transaction.getState().getVOName(), transaction.isRedeligation());
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


    public List<DomainVOWrapper> getDomains() {
        List<IDomainVO> list = domainService.findDomains();
        List<DomainVOWrapper> result = new ArrayList<DomainVOWrapper>();
        for (IDomainVO domainVO : list) {
            result.add(new SystemDomainVOWrapper(domainVO));
        }
        return result;
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

    public SystemDomainVOWrapper getDomain(String domainName) {
        try{
            IDomainVO vo = domainService.getDomain(domainName.toLowerCase().trim());
            return new SystemDomainVOWrapper(vo);
        }catch(IllegalArgumentException e){
            return null;
        }
    }

    public int getDomainsCount() {
        return domainService.count(null);
    }


    public List<UserVOWrapper> getUsers() {
        List<UserVO> list = userService.findUsers();
        List<UserVOWrapper> users = new ArrayList<UserVOWrapper>();
        for (UserVO userVO : list) {
            users.add(new UserVOWrapper(userVO));
        }

        return users;
    }

    public List<UserVOWrapper> getUsers(Criterion criterion, int offset, int length) {
        List<UserVO> list = userService.find(criterion, new Order("loginName", true),  offset, length);
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
        try{
            UserVO vo = userService.getUser(userName);
            return new UserVOWrapper(vo);
        }catch(IllegalArgumentException e){
            return null;
        }
    }

    public int getUserCount(Criterion criterion) {
        return userService.count(criterion);
    }


    public int getTotalUserCount() {
        return userService.count(null);
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


    public void changePassword(long userId, String newPassword) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
