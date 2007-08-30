package org.iana.rzm.web.services.admin;

import org.iana.criteria.*;
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

    public List<DomainVOWrapper>getDomains();
    public List<UserVOWrapper> getUsers();
    public void updateTransaction(TransactionVOWrapper transaction) throws RzmServerException;
    public int getDomainsCount();
    public List<DomainVOWrapper> getDomains(int offset, int length);
    public int getTotalUserCount();
    public List<UserVOWrapper> getUsers(Criterion criterion, int offset, int length);
    public UserVOWrapper getUser(long userId);
    public void createUser(UserVOWrapper user);
    public void updateUser(UserVOWrapper user);
    public TransactionVOWrapper getTransactionByRtId(long rtId);
    public SystemDomainVOWrapper getDomain(String domainName);
    public UserVOWrapper getUser(String userName);
    public int getUserCount(Criterion criterion);
}
