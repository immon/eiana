package org.iana.rzm.web.user.services;

import org.apache.log4j.*;
import org.iana.codevalues.*;
import org.iana.commons.*;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.common.cc.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.facade.services.*;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.DNSTechnicalCheckExceptionWrapper;
import org.iana.rzm.facade.system.trans.vo.*;
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.technical_check.DNSTechnicalCheckErrorsXmlParser;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.resolver.*;
import org.iana.rzm.web.common.utils.*;
import org.iana.web.tapestry.services.*;

import java.sql.*;
import java.util.*;

public class UserServicesImpl implements UserServices {

    private static final Logger LOGGER = Logger.getLogger(UserServicesImpl.class);

    private SystemDomainService domainService;
    private TransactionService transactionService;
    private TransactionDetectorService detectorService;
    private CountryCodes countryCodeService;
    private PasswordChangeService changePasswordService;
    private UserVOManager userManager;
    private DNSTechnicalCheckErrorsXmlParser technicalErrorsXmlParser;


    public UserServicesImpl(ServiceInitializer<RZMStatefulService> initializer) {
        domainService = initializer.getBean("remoteGuardedSystemDomainService");
        transactionService = initializer.getBean("remoteGuardedSystemTransactionServiceBean");
        detectorService = initializer.getBean("remoteDetectorService");
        countryCodeService = initializer.getBean("remoteCc", CountryCodes.class);
        changePasswordService = initializer.getBean("remotePasswordChangeService", PasswordChangeService.class);
        userManager = initializer.getBean("remoteUserManager", UserVOManager.class);
        technicalErrorsXmlParser = initializer.getBean("technicalErrorsXmlParser", DNSTechnicalCheckErrorsXmlParser.class);
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

    public List<String> parseErrors(String technicalErrors) {
        return technicalErrorsXmlParser.getTechnicalCheckErrors(technicalErrors);
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

    public UserVOWrapper getUser(long userId) {
        return new UserVOWrapper(userManager.getUserVO(userId));
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

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper, String submmiterEmail, boolean useRadicalChangesCheck)
        throws
        AccessDeniedException,
        NoObjectFoundException,
        NoDomainModificationException,
        DNSTechnicalCheckExceptionWrapper,
        TransactionExistsException,
        NameServerChangeNotAllowedException,
        SharedNameServersCollisionException,
        RadicalAlterationException {

        PerformTechnicalCheck type = useRadicalChangesCheck ? PerformTechnicalCheck.ON : PerformTechnicalCheck.ON_NO_RADICAL_ALTERATION;

        try {
            List<TransactionVO> list = transactionService.createTransactions(domainVOWrapper.getDomainVO(), false, submmiterEmail, type, null);
            return new TransactionVOWrapper(list.get(0));
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain, String submitterEmail, boolean useRadicalChangesCheck)
            throws
            AccessDeniedException,
            NoObjectFoundException,
            NoDomainModificationException,
            TransactionExistsException,
            NameServerChangeNotAllowedException,
            SharedNameServersCollisionException,
            RadicalAlterationException,
            DNSTechnicalCheckExceptionWrapper
    {
        try {
            PerformTechnicalCheck type = useRadicalChangesCheck ? PerformTechnicalCheck.ON : PerformTechnicalCheck.ON_NO_RADICAL_ALTERATION;
            List<TransactionVO> list = null;
            list = transactionService.createTransactions(domain.getDomainVO(), true, submitterEmail, type, null);
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

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper modifiedDomain, boolean useRadicalChangesCheck)
        throws NoObjectFoundException, AccessDeniedException,SharedNameServersCollisionException,RadicalAlterationException {
        try {
            PerformTechnicalCheck type = useRadicalChangesCheck ? PerformTechnicalCheck.ON : PerformTechnicalCheck.ON_NO_RADICAL_ALTERATION;
            TransactionActionsVO vo = detectorService.detectTransactionActions(modifiedDomain.getDomainVO(), type);
            return new TransactionActionsVOWrapper(vo);
        } catch (InfrastructureException e) {
            LOGGER.warn("InfrastructureException", e);
            throw new RzmApplicationException(e);
        }
    }

    public void withdrawnTransaction(long requestId)
        throws
        NoObjectFoundException,
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

}
