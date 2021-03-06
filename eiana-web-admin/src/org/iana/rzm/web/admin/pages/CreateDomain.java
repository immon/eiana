package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.engine.state.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.web.admin.pages.editors.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.model.*;

import java.util.*;

public abstract class CreateDomain extends AdminPage implements DomainAttributeEditor, IExternalPage {

    public final static String PAGE_NAME = "CreateDomain";

    @Component(id = "domainEditor",
               type = "DomainEditor",
               bindings = {
                   "domain=prop:domain",
                   "editor=prop:domainEditor",
                   "contactListener=listener:newContact",
                   "nameServerListener=listener:newNameServerList",
                   "subDomainListener=listener:newSubDomain",
                   "edit=literal:false"
                   })
    public abstract IComponent getDomainEditorComoponent();

     @Component(id = "domainHeader",
               type = "rzmLib:DomainHeader",
               bindings = {"countryName=prop:countryName", "domainName=prop:domain.name"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryComponent();

    @InjectObject("infrastructure:applicationStateManager")
    public abstract ApplicationStateManager getApplicationStateManager();

    @InjectPage(Domains.PAGE_NAME)
    public abstract Domains getDomainsPage();

    @InjectPage(NewContact.PAGE_NAME)
    public abstract NewContact getNewContactPage();

    @InjectPage(EditSubDomain.PAGE_NAME)
    public abstract EditSubDomain getEditSubDomain();

    @InjectPage(EditNameServerList.PAGE_NAME)
    public abstract EditNameServerList getEditNameServerList() ;

    @Persist("client")
    public abstract String getCountryName();
    public abstract void setCountryName(String name);

    public abstract void setCountryCode(String countryCode);

    @Persist("client")
    public abstract SystemDomainVOWrapper getOriginalDomain();
    public abstract void setOriginalDomain(SystemDomainVOWrapper domain);

    @Persist("client")
    public abstract void setDomain(SystemDomainVOWrapper domain);
    public abstract SystemDomainVOWrapper getDomain();

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long id) ;


    protected Object[] getExternalParameters() {
        return new Object[] {getDomainId(),getDomain(), getOriginalDomain(), getCountryName()};
    }

    public void newContact(String type) {
        NewContact page = getNewContactPage();
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        page.setContactAttributes(new HashMap<String, String>());
        page.setOriginalContact(getDomain().getContact(getDomainId(),type));
        page.setDomainId(getDomainId());
        page.setContactType(type);
        getRequestCycle().activate(page);
    }

    public void newContact(long id, String type) {
        NewContact page = getNewContactPage();
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        page.setContactAttributes(getDomain().getContact(getDomainId(),type).getMap());
        page.setOriginalContact(new ContactVOWrapper(type));
        page.setDomainId(getDomainId());
        page.setContactType(type);
        getRequestCycle().activate(page);
    }

    public void newNameServerList(){
        EditNameServerList page = getEditNameServerList();
        page.setOriginalNameServers(new ArrayList<NameServerVOWrapper>());
        page.setDomainId(getDomainId());
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        getRequestCycle().activate(page);
    }

    public void newSubDomain() {
        EditSubDomain editSubDomain = getEditSubDomain();
        editSubDomain.setDomainId(getDomain().getId());
        editSubDomain.setOriginalRegistryUrl(getOriginalDomain().getRegistryUrl());
        editSubDomain.setOriginalWhoisServer(getOriginalDomain().getWhoisServer());
        editSubDomain.setRegistryUrl(getDomain().getRegistryUrl());
        editSubDomain.setWhoisServer(getDomain().getWhoisServer());
        editSubDomain.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        getRequestCycle().activate(editSubDomain);
    }


    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        setDomainId((Long)parameters[0]);
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        setDomain((SystemDomainVOWrapper) (domain == null ? (SystemDomainVOWrapper) parameters[1] : domain));
        setOriginalDomain((SystemDomainVOWrapper) parameters[2]);
        setCountryName((String) parameters[3]);
    }



    public DomainAttributeEditor getDomainEditor() {
        return this;
    }


    public void save() {
        try{
            getAdminServices().createDomain(getDomain());
            Domains page = getDomainsPage();
            getRequestCycle().activate(page);
        }catch(InvalidCountryCodeException e){
            setErrorMessage(getMessageUtil().getInvalidCountryCodeErrorMessage(e.getCountryCode()));
        }catch( AccessDeniedException e){
            setErrorMessage(getMessageUtil().getAccessDeniedMessage());
        }
    }

    public void revert() {
        Domains domainsPage = getDomainsPage();
        getRequestCycle().activate(domainsPage);
    }


}
