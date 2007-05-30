package org.iana.rzm.web.services.admin;

import org.apache.log4j.Logger;
import org.iana.criteria.Order;
import org.iana.rzm.facade.admin.AdminDomainService;
import org.iana.rzm.facade.admin.AdminTransactionService;
import org.iana.rzm.facade.admin.AdminUserService;
import org.iana.rzm.facade.admin.NoTransactionException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.common.cc.CountryCodes;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.trans.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.web.RzmApplicationException;
import org.iana.rzm.web.model.DomainVOWrapper;
import org.iana.rzm.web.model.SystemDomainVOWrapper;
import org.iana.rzm.web.model.TransactionVOWrapper;
import org.iana.rzm.web.model.UserVOWrapper;
import org.iana.rzm.web.services.CriteriaBuilder;
import org.iana.rzm.web.tapestry.services.ServiceInitializer;

import java.util.ArrayList;
import java.util.List;

public class AdminServicesImpl implements AdminServices {

    private static final Logger LOGGER = Logger.getLogger(AdminServicesImpl.class);

    private AdminTransactionService transactionService;
    private AdminDomainService domainService;
    private AdminUserService   userService;
    private CountryCodes       countryCodeService;

    public AdminServicesImpl(ServiceInitializer initializer) {
        domainService = initializer.getBean("GuardedAdminDomainServiceBean");
        transactionService = initializer.getBean("GuardedAdminTransactionServiceBean");
        userService = initializer.getBean("GuardedAdminUserServiceBean");
        countryCodeService = initializer.getBean("cc", CountryCodes.class);
    }

    public List<TransactionVOWrapper> getTransactions() {
        List<TransactionVO> all = transactionService.findAll();
        List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
        for (TransactionVO transaction : all) {
            result.add(new TransactionVOWrapper(transaction));
        }
        return result;
    }

    public List<DomainVOWrapper> getDomains() {
        List<IDomainVO> list = domainService.findDomains();
        List<DomainVOWrapper> result = new ArrayList<DomainVOWrapper>();
        for (IDomainVO domainVO : list) {
            result.add(new SystemDomainVOWrapper(domainVO));
        }
        return result;
    }

    public List<UserVOWrapper> getUsers() {
        List<UserVO> list = userService.findUsers();
        List<UserVOWrapper> users = new ArrayList<UserVOWrapper>();
        for (UserVO userVO : list) {
            users.add(new UserVOWrapper(userVO));
        }

        return users;
    }

    public void saveTransaction(TransactionVOWrapper transaction) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<TransactionVOWrapper> getOpenTransaction() throws NoObjectFoundException {
        TransactionCriteriaVO criteria = CriteriaBuilder.createOpenTransactionCriteria();
        List<TransactionVO> list = transactionService.find(criteria);
        List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
        for (TransactionVO transaction : list) {
            result.add(new TransactionVOWrapper(transaction));
        }
        return result;

    }

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException {


        try {
            TransactionVO vo = transactionService.getTransaction(id);
            return new TransactionVOWrapper(vo);
        } catch (NoTransactionException e) {
             LOGGER.warn("NoTransactionException", e);
            throw new RzmApplicationException(e);
        }

    }

    public DomainVOWrapper getDomain(long domainId) throws NoObjectFoundException {
        IDomainVO vo = domainService.getDomain(domainId);
        return new SystemDomainVOWrapper(vo);
    }

    public String getCountryName(String domainCode) {
        return countryCodeService.getCountryName(domainCode);
    }

    public int getTotalTransactionCount() {
        return transactionService.findAll().size();
    }

    public int getDomainsCount() {
        return domainService.count(null);
    }

    public List<DomainVOWrapper> getDomains(int offset, int length) {
        List<IDomainVO> list = domainService.find(new Order("name.name", true), offset, length);
        List<DomainVOWrapper>result = new ArrayList<DomainVOWrapper>();
        for (IDomainVO iDomainVO : list) {
            result.add(new SystemDomainVOWrapper(iDomainVO));
        }

        return result;
    }

    public int getTotalUserCount() {
        return userService.count(null);
    }

    public List<UserVOWrapper> getUsers(int offset, int length) {
        List<UserVO> list = userService.find(new Order("loginName", true), offset, length);
         List<UserVOWrapper> users = new ArrayList<UserVOWrapper>();
        for (UserVO userVO : list) {
            users.add(new UserVOWrapper(userVO));
        }

        return users;
    }

    public void changePassword(long userId, String newPassword) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
