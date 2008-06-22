package org.iana.rzm.web.services.user;

import org.apache.log4j.*;
import org.iana.codevalues.*;
import org.iana.criteria.*;
import org.iana.dns.check.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.common.cc.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.*;
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.tapestry.services.*;
import org.iana.rzm.web.util.*;

import java.sql.*;
import java.util.*;

public class UserServicesImpl implements UserServices {

    private static final Logger LOGGER = Logger.getLogger(UserServicesImpl.class);

    private SystemDomainService domainService;
    private TransactionService transactionService;
    private TransactionDetectorService detectorService;
    private CountryCodes countryCodeService;
    private PasswordChangeService changePasswordService;


    public UserServicesImpl(ServiceInitializer initializer) {
        domainService = initializer.getBean("GuardedSystemDomainService");
        transactionService = initializer.getBean("GuardedSystemTransactionService");
        detectorService = initializer.getBean("detectorService");
        countryCodeService = initializer.getBean("cc", CountryCodes.class);
        changePasswordService = initializer.getBean("passwordChangeService", PasswordChangeService.class);
    }

    public String getCountryName(String name) {
        if(name == null){
            LOGGER.warn("Calling UserServicesImpl.getCountryName with  null");
            return "";
        }
        String code = countryCodeService.getCountryName(name.toUpperCase());
        if (code == null) {
            return "Top Level Domain";
        }
        return code;
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
        try {
            domainService.setAccessToDomain(userId, domainId, access);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
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

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword)
        throws PasswordChangeException {

        try {
            changePasswordService.changePassword(username,oldPassword, newPassword, confirmedNewPassword);
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
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

    public SystemDomainVOWrapper getDomain(String domainName) throws NoObjectFoundException {
        try {
            IDomainVO vo = domainService.getDomain(domainName);
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

            for (final SimpleDomainVO vo : list) {
                Set<RoleVO.Type> roles = vo.getRoles();
                boolean once = false;
                for (RoleVO.Type role : roles) {
                    SystemRoleVOWrapper userRole = new SystemRoleVOWrapper(role);
                    Timestamp modified = vo.getModified() == null ? vo.getCreated() : vo.getModified();
                    if(!once){
                        userDomains.add(new UserDomain(vo.getObjId(), vo.getName(), userRole.getTypeAsString(), modified));
                        once = true;
                    }else{
                        UserDomain userDomain = ListUtil.find(userDomains, new ListUtil.Predicate<UserDomain>() {
                            public boolean evaluate(UserDomain object) {
                                return object.getDomainId() == vo.getObjId();
                            }
                        });
                        userDomain.addRole(userRole.getTypeAsString());
                    }
                }
            }
            return userDomains;
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper, String submmiterEmail)
            throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException,
            DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException {

        try {
            List<TransactionVO> list = transactionService.createTransactions(domainVOWrapper.getDomainVO(), false, submmiterEmail, true, null);
            return new TransactionVOWrapper(list.get(0));
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        } catch (DNSTechnicalCheckException e) {
            throw new DNSTechnicalCheckExceptionWrapper(e);
        }
    }

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain, String submitterEmail)
            throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, TransactionExistsException, NameServerChangeNotAllowedException {
        try {
            List<TransactionVO> list;
            if (submitterEmail != null) {
                list = transactionService.createTransactions(domain.getDomainVO(), true, submitterEmail);
            } else {
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
            TransactionVO vo = transactionService.get(requestId);
            return new TransactionVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public int getTransactionCount(Criterion criterion) {
        try {
            return transactionService.count(criterion);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length, SortOrder sort) {
        List<TransactionVO> list = null;
        try {
            if(sort != null && sort.isValid()){
                Order order = new Order(new RequestFieldNameResolver().resolve(sort.getFieldName()), sort.isAscending());
                 list = transactionService.find(criterion, order, offset, length);
            }else{
                 list = transactionService.find(criterion,  offset, length);
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

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper modifiedDomain)
        throws NoObjectFoundException, AccessDeniedException {
        try {
            TransactionActionsVO vo = detectorService.detectTransactionActions(modifiedDomain.getDomainVO());
            return new TransactionActionsVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void withdrawnTransaction(long requestId)
        throws
        FacadeTransactionException,
        NoSuchStateException,
        NoObjectFoundException,
        StateUnreachableException,
        TransactionCannotBeWithdrawnException {
        try {
            transactionService.withdrawTransaction(requestId);
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
