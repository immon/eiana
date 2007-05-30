package org.iana.rzm.web.services.admin;

import org.iana.rzm.web.model.DomainVOWrapper;
import org.iana.rzm.web.model.TransactionVOWrapper;
import org.iana.rzm.web.model.UserVOWrapper;
import org.iana.rzm.web.services.RzmServices;

import java.util.List;

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
    public void saveTransaction(TransactionVOWrapper transaction);
    public int getDomainsCount();
    public List<DomainVOWrapper> getDomains(int offset, int length);
    public int getTotalUserCount();
    public List<UserVOWrapper> getUsers(int offset, int length);
}
