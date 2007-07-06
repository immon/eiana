package org.iana.rzm.web.services.user;

import org.apache.log4j.Logger;
import org.iana.codevalues.Value;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
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
import java.util.*;

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
        if (code == null) {
            return "Top Level Domain";
        }
        return code;
    }

    public boolean isValidCountryCode(String code){
        List<Value> list = countryCodeService.getCountries();
        for (Value value : list) {
            if(value.getValueId().equals(code)){
                return true;
            }
        }

        return false;

    }

    public List<Value> getCountrys() {
        List<Value> list = countryCodeService.getCountries();
        Collections.sort(list, new CountryCodeSorter());
        return list;
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
        domainService.setAccessToDomain(userId, domainId, access);
    }

    public UserVOWrapper getUser() {
        return new UserVOWrapper(domainService.getUser());
    }

    public void acceptTransaction(long requestId, String token) throws NoObjectFoundException, AccessDeniedException {
        try {
            transactionService.acceptTransaction(requestId, token);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void rejectTransaction(long requestId, String token) throws NoObjectFoundException, AccessDeniedException {
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

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException, AccessDeniedException {
        try {
            IDomainVO vo = domainService.getDomain(domainId);
            return new SystemDomainVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<UserDomain> getUserDomains() throws AccessDeniedException {
        try {
            List<SimpleDomainVO> list = domainService.findUserDomains();
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

    public int getTotalTransactionCount() throws AccessDeniedException {
        try {
            return transactionService.findTransactions(new TransactionCriteriaVO()).size();
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getTransactions() throws AccessDeniedException {
        try {
            List<TransactionVO> list = transactionService.findTransactions(new TransactionCriteriaVO());
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

    public Collection<TransactionVOWrapper> getOpenTransactionsForDomin(final String domainName) throws NoObjectFoundException, AccessDeniedException {

        try {
            TransactionCriteriaVO criteria = CriteriaBuilder.createOpenTransactionCriteriaForDomain(domainName);
            List<TransactionVO> transactions = transactionService.findTransactions(criteria);
            List<TransactionVOWrapper> result = new ArrayList<TransactionVOWrapper>();
            for (TransactionVO transaction : transactions) {
                result.add(new TransactionVOWrapper(transaction));
            }
            return result;
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper, String submmiterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException {

        try {
            List<TransactionVO> list;
            if(submmiterEmail != null){
                list = transactionService.createTransactions(domainVOWrapper.getDomainVO(), false, submmiterEmail);
            }else{
                list = transactionService.createTransactions(domainVOWrapper.getDomainVO(), false);
            }

            return new TransactionVOWrapper(list.get(0));
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException {
        try {
             List<TransactionVO> list;
            if(submitterEmail != null){
                list = transactionService.createTransactions(domain.getDomainVO(), true, submitterEmail);
            }else{
                list = transactionService.createTransactions(domain.getDomainVO(), true);                
            }
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

    public TransactionVOWrapper getTransaction(long requestId) throws NoObjectFoundException, AccessDeniedException {
        try {
            TransactionVO vo = transactionService.getTransaction(requestId);
            return new TransactionVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getOpenTransaction() throws NoObjectFoundException, AccessDeniedException {
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

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper modifiedDomain) throws NoObjectFoundException, AccessDeniedException {
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

    private static class CountryCodeSorter implements Comparator<Value> {

        public int compare(Value o1, Value o2) {
            return o1.getValueName().compareTo(o2.getValueName());
        }
    }
}
