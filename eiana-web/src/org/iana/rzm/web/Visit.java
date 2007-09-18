package org.iana.rzm.web;

import org.iana.rzm.web.model.*;

import java.io.*;
import java.util.*;

public class Visit implements Serializable {

    private WebUser user;
    private Map<Long,DomainVOWrapper> visitedDomains  = new HashMap<Long, DomainVOWrapper>();
    private long modifiedDomain;
    private String submitterEmail;

    public WebUser getUser() {
        return user;
    }

    /**
     * Returns the id of the logged in user, or <code> RzmBaseProposal.EMPTY_ID </code> if the user is not logged in.
     */

    public long getUserId() {
        return user == null ? ModelUtil.EMPTY_ID : user.getId();
    }

    /**
     * Changes the logged in user. This is only invoked from the
     * {@link org.iana.rzm.web.model.WebUser} page.
     *
     * @param user
     */

    public void setUser(WebUser user) {
        this.user = user;
    }

    /**
     * Returns true if the user is logged in.
     */

    public boolean isUserLoggedIn() {
        return user != null;
    }

    /**
     * Returns true if the user has not been identified (has not logged in).
     */

    public boolean isUserLoggedOut() {
        return user == null;
    }

    /**
     * Invoked by pages after they perform an operation that changes the backend database in such a
     * way that cached data is no longer valid.
     */

    public void clearCache() {
        user = null;
        visitedDomains = null;
        modifiedDomain = 0;
        submitterEmail = null;
    }


    public boolean isLoggedInUser(long id) {
        return user != null && user.getId() == id;

    }

    public boolean isDomainModified(long domainId) {
        return modifiedDomain == domainId;
    }

    public void markAsVisited(DomainVOWrapper domain) {
        if(!visitedDomains.containsKey(domain.getId())){
            visitedDomains.put(domain.getId(), domain);
        }
    }

    public void markAsNotVisited(long domainId) {
        visitedDomains.remove(domainId);
        if(modifiedDomain == domainId){
            modifiedDomain = 0;
            submitterEmail = null;
        }
    }

    public void resetModifirdDomain(){
        modifiedDomain = 0;
        submitterEmail = null;
    }

    public DomainVOWrapper getCurrentDomain(long domainId) {
        return visitedDomains.get(domainId);
    }

    public void markDomainDirty(long domainId) {
        modifiedDomain = domainId;
    }


    public DomainVOWrapper getMmodifiedDomain() {
        return visitedDomains.get(modifiedDomain);
    }

    public String getSubmitterEmail(){
        return submitterEmail;
    }

    public void setSubmitterEmail(String email){
        this.submitterEmail = email;
    }


}
