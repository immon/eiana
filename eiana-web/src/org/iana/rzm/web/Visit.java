package org.iana.rzm.web;

import org.apache.tapestry.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.pages.user.*;

import java.io.*;
import java.util.*;

public class Visit implements Serializable {

    private WebUser user;
    private Map<Long, DomainVOWrapper> visitedDomains = new HashMap<Long, DomainVOWrapper>();
    private Map<Long, ModifiedDomain >modifiedDomains = new HashMap<Long, ModifiedDomain>();
    private ModifiedDomain domain;
    private RequestMetaParameters requestMetaParam;


    public Visit() {
        setRequestMetaParameters(new RequestMetaParameters());
    }

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
        modifiedDomains.clear();;
        requestMetaParam = null;
    }


    public boolean isLoggedInUser(long id) {
        return user != null && user.getId() == id;

    }

    public boolean isDomainModified(long domainId) {
        return modifiedDomains.containsKey(domainId);
    }

    public void markAsVisited(DomainVOWrapper domain) {
        if (!visitedDomains.containsKey(domain.getId())) {
            visitedDomains.put(domain.getId(), domain);
        }
    }

    public void markAsNotVisited(long domainId) {
        visitedDomains.remove(domainId);
        if (modifiedDomains.containsKey(domainId)) {
            modifiedDomains.remove(domainId);
            requestMetaParam = null;
        }
    }

    public void resetModifiedDomain(long id) {
        modifiedDomains.remove(id);
        requestMetaParam = null;
    }

    public DomainVOWrapper getCurrentDomain(long domainId) {
        return visitedDomains.get(domainId);
    }

    public void markDomainDirty(long domainId, DomainChangeType type) {
        ModifiedDomain modifiedDomain = modifiedDomains.get(domainId);
        if(modifiedDomain == null){
            modifiedDomain = new ModifiedDomain(domainId, type);
        }

        modifiedDomain.addChange(type);
        modifiedDomains.put(domainId, modifiedDomain);
    }


    public DomainVOWrapper getModifiedDomain(long domainId) {
        ModifiedDomain modifiedDomain = modifiedDomains.get(domainId);
        if(modifiedDomain != null){
            return visitedDomains.get(modifiedDomain.getId());
        }

        return null;
    }

    public String getSubmitterEmail() {
        return getRequestMetaParameters().getEmail();
    }

    public void setSubmitterEmail(String email) {
        setRequestMetaParameters(new RequestMetaParameters(email, null));
    }


    public void setRequestMetaParameters(RequestMetaParameters parmeter) {
        requestMetaParam = parmeter;
    }

    public RequestMetaParameters getRequestMetaParameters() {
            if(requestMetaParam == null){
                requestMetaParam = new RequestMetaParameters();
            }
        return requestMetaParam;
    }


    public void storeDomain(DomainVOWrapper domain) {
        visitedDomains.put(domain.getId(), domain);
    }

    public boolean isAdminPage(IRequestCycle cycle, String pageName) {
        IPage page = cycle.getPage(pageName);
        return AdminPage.class.isAssignableFrom(page.getClass());
    }

    public boolean isUserPage(IRequestCycle cycle, String pageName) {
        IPage page = cycle.getPage(pageName);
        return UserPage.class.isAssignableFrom(page.getClass());
    }

    public void clearChange(long domainId, DomainChangeType type) {
        ModifiedDomain modifiedDomain = modifiedDomains.get(domainId);
        if(modifiedDomain == null){
            return;
        }

        modifiedDomain.removeChange(type);
        if(!modifiedDomain.isChanged()){
            modifiedDomains.remove(domainId);
        }
    }

    public void resetAllModifiedDomain() {
        modifiedDomains.clear();
    }

    private static class ModifiedDomain{
        private long domainId;
        private boolean nameServerChange;
        private boolean adminChange;
        private boolean techChange;
        private boolean soChange;
        private boolean subDomainChange;

        ModifiedDomain(long domainId, DomainChangeType change){
            this.domainId = domainId;
            addChange(change);
        }

        public boolean isAdminChange(){
            return adminChange;
        }

        public boolean isTechChange(){
            return techChange;
        }

        public boolean isSoChange(){
            return soChange;
        }

        public boolean isNameServerChange(){
            return nameServerChange;
        }

       public long getId(){
           return domainId;
       }


        public void addChange(DomainChangeType type) {
            if(type.equals(DomainChangeType.ns)){
                nameServerChange = true;
            } else if(type.equals(DomainChangeType.sudomain)){
                subDomainChange = true;
            }  else if(type.equals(DomainChangeType.Administrative)){
                adminChange = true;
            } else if(type.equals(DomainChangeType.Technical)){
                techChange = true;
            } else if(type.equals(DomainChangeType.SO)){
                soChange = true;
            }
        }

        public void removeChange(DomainChangeType type){
             if(type.equals(DomainChangeType.ns)){
                nameServerChange = false;
            } else if(type.equals(DomainChangeType.sudomain)){
                subDomainChange = false;
            }  else if(type.equals(DomainChangeType.Administrative)){
                adminChange = false;
            } else if(type.equals(DomainChangeType.Technical)){
                techChange = false;
            } else if(type.equals(DomainChangeType.SO)){
                soChange = false;
            }
        }

        public boolean isChanged(){
            return nameServerChange || adminChange
                   || techChange || soChange || subDomainChange;
        }

    }
}
