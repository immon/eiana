package org.iana.rzm.web.services.admin;

import org.iana.codevalues.*;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 8, 2007
 * Time: 5:10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AdminServices extends RzmServices {

    List<TransactionVOWrapper> createDomainModificationTrunsaction(DomainVOWrapper domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException,
                                                                                                                                                    InvalidCountryCodeException, CreateTicketException;


    public void updateTransaction(TransactionVOWrapper transaction) throws RzmServerException;

    public TransactionVOWrapper getTransactionByRtId(long rtId);

    public int getDomainsCount();

    public void updateDomain(DomainVOWrapper domain);

    public List<DomainVOWrapper> getDomains(int offset, int length);

    public List<UserVOWrapper> getUsers(Criterion criterion, int offset, int length);

    public int getUserCount(Criterion criterion);

    public void deleteUser(long userId);

    public UserVOWrapper getUser(long userId);

    public UserVOWrapper getUser(String userName);

    public void createUser(UserVOWrapper user);

    public void updateUser(UserVOWrapper user);

    public List<NotificationVOWrapper> getNotifications(long requestId);

    public Set<Value> getDomainTypes();

    public void sendNotification(long transactionId, NotificationVOWrapper vo, String comment )throws FacadeTransactionException;
}
