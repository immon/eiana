package org.iana.rzm.web.services.user;

import org.apache.log4j.Logger;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.common.cc.CountryCodes;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.web.RzmApplicationException;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.CriteriaBuilder;
import org.iana.rzm.web.tapestry.services.ServiceInitializer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UserServicesImpl implements UserServices {

    private static final Logger LOGGER = Logger.getLogger(UserServicesImpl.class);

    private SystemDomainService domainService;
    private SystemTransactionService transactionService;
    private CountryCodes countryCodeService;


    public UserServicesImpl(ServiceInitializer initializer) {
        domainService = initializer.getBean("GuardedSystemDomainService");
        transactionService = initializer.getBean("GuardedSystemTransactionService");
        countryCodeService = initializer.getBean("cc", CountryCodes.class);
    }

    public String getCountryName(String name) {
        String code = countryCodeService.getCountryName(name.toUpperCase());
        if(code == null){
            return "Top Level Domain";
        }
        return code;
    }

    public List<UserVOWrapper> getUsersForDomain(String domainName) {
        List<UserVO> list = domainService.findDomainUsers(domainName, false);
        List<UserVOWrapper> users = new ArrayList<UserVOWrapper>();
        for (UserVO userVO : list) {
            users.add(new UserVOWrapper(userVO));
        }
        return users;
    }

    public void setAccessToDomain(long domainId, long userId, boolean access) {
        domainService.setAccessToDomain(userId, domainId,access);
    }

    public UserVOWrapper getUser() {
        return new UserVOWrapper(domainService.getUser());
    }

    public void acceptTransaction(long requestId, String token) throws NoObjectFoundException {
        try {
            transactionService.acceptTransaction(requestId, token);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void rejectTransaction(long requestId, String token) throws NoObjectFoundException {
        try {
            transactionService.rejectTransaction(requestId, token);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void changePassword(long userId, String newPassword) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException {
        try {
            IDomainVO vo = domainService.getDomain(domainId);
            return new SystemDomainVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<UserDomain> getUserDomains() {
        List<SimpleDomainVO> list ;
        try {
            list = domainService.findUserDomains();

            List<UserDomain> userDomains = new ArrayList<UserDomain>();
            for (SimpleDomainVO vo : list) {
                Set<RoleVO.Type> roles = vo.getRoles();
                for (RoleVO.Type role : roles) {
                    SystemRoleVOWrapper userRole = new SystemRoleVOWrapper(role);
                    Timestamp modified = vo.getModified() == null ? vo.getCreated() : vo.getModified();
                    userDomains.add(new UserDomain(vo.getObjId(), vo.getName(), userRole.getTypeAsString(), modified));
                }

            }
            return userDomains;
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public int getTotalTransactionCount() {
        try {
            return transactionService.findTransactions(new TransactionCriteriaVO()).size();
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getTransactions() {
        try {
            List<TransactionVO> list = transactionService.findTransactions(new TransactionCriteriaVO());
            List<TransactionVOWrapper>result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transactionVO : list) {
                result.add(new TransactionVOWrapper(transactionVO));
            }
            return result;
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public Collection<TransactionVOWrapper> getOpenTransactionsForDomin(final String domainName) throws NoObjectFoundException {

        try{
            TransactionCriteriaVO criteria = CriteriaBuilder.createOpenTransactionCriteriaForDomain(domainName);
            List<TransactionVO> transactions = transactionService.findTransactions(criteria);
            List<TransactionVOWrapper>result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transaction : transactions) {
                result.add(new TransactionVOWrapper(transaction));
            }
            return result;
        }catch(InfrastructureException e){
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper) throws NoObjectFoundException {

        try {
            List<TransactionVO> list = transactionService.createTransactions(domainVOWrapper.getDomainVO(), false);
            return new TransactionVOWrapper(list.get(0));
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        } catch (NoDomainModificationException e) {
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain) throws NoObjectFoundException {
        try {
            List<TransactionVO> list = transactionService.createTransactions(domain.getDomainVO(), true);
            List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transactionVO : list) {
                result.add(new TransactionVOWrapper(transactionVO));
            }
            return result;
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        } catch (NoDomainModificationException e) {
            throw new RzmApplicationException(e);
        }
    }

    public TransactionVOWrapper getTransaction(long requestId) throws NoObjectFoundException {
        try {
            TransactionVO vo = transactionService.getTransaction(requestId);
            return new TransactionVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getOpenTransaction() throws NoObjectFoundException {
        try {
            List<TransactionVO> list = transactionService.findOpenTransactions();
            List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transactionVO : list) {
                result.add(new TransactionVOWrapper(transactionVO));
            }

            return result;

        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper modifiedDomain) throws NoObjectFoundException {
        try {
            TransactionActionsVO vo = transactionService.detectTransactionActions(modifiedDomain.getDomainVO());
            return new TransactionActionsVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void setDomainService(SystemDomainService domainService) {
        this.domainService = domainService;
    }
}
