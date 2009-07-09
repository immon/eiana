package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.callback.PageCallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.model.DomainVOWrapper;
import org.iana.rzm.web.common.model.TransactionVOWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class RequestSplitConfirmation extends AdminPage implements PageBeginRenderListener, IExternalPage{

    public static final String PAGE_NAME = "RequestSplitConfirmation";

    public final static Integer ONE_RQUEST = 1;
    public final static Integer TWO_RQUEST = 2;

    @Component(id = "form", type = "Form")
    public abstract IComponent getFormComponent();

     @Component(id = "domainHeader", type = "rzmLib:DomainHeader", bindings = {"countryName=prop:countryName", "domainName=prop:domainName"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "splitRequest", type = "RadioGroup", bindings = {"selected=prop:splitRequest", "disabled=prop:mustSplit"})
    public abstract IComponent getSplitRequestComponent();

    @Component(id = "oneRequest", type = "Radio", bindings = {
        "value=ognl:@org.iana.rzm.web.admin.pages.RequestSplitConfirmation@ONE_RQUEST"
        })
    public abstract IComponent getOneRequestComponent();

    @Component(id = "twoRequest", type = "Radio", bindings = {
        "value=ognl:@org.iana.rzm.web.admin.pages.RequestSplitConfirmation@TWO_RQUEST"
        })
    public abstract IComponent getTwoRequestComponent();

    @Component(id = "makeChanges", type = "LinkSubmit", bindings = {"action=listener:makeMoreChanges"})
    public abstract IComponent getMakeChangesComponent();

    @Component(id = "proceed", type = "LinkSubmit", bindings = {"action=listener:proceed"})
    public abstract IComponent getLinkSubmitComponent();

    @Component(id="pendingRadicalChanges", type="If", bindings = {"condition=prop:displayRadicalChangesMessage"})
    public abstract IComponent getPendingRadicalChangesComponent();

    @InjectPage(Summary.PAGE_NAME)
    public abstract Summary getRequestSummaryPage();

    @Persist("client")
    public abstract void setCallback(ICallback callback);
    public abstract ICallback getCallback();

    @Persist("client")
    public abstract void setDomainId(long domainId);
    public abstract long getDomainId();

    @Persist("client")
    public abstract void setSplitRequest(int value);
    public abstract int getSplitRequest();

    @Persist("client")
    public abstract boolean isMustSplit();
    public abstract void setMustSplit(boolean value);

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @InitialValue("literal:false")
    public abstract void setDisplayRadicalChangesMessage(boolean b);
    public abstract boolean isDisplayRadicalChangesMessage();

    public abstract void setDomainName(String name);
    public abstract String getDomainName();

    public abstract void setCountryName(String countryName);

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        if (getSplitRequest() == 0 || isMustSplit()) {
            setSplitRequest(TWO_RQUEST);
        }

        DomainVOWrapper domainVOWrapper = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(domainVOWrapper.getName());
        setCountryName("(" + getAdminServices().getCountryName(domainVOWrapper.getName()) + ")");
    }


    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(), getSplitRequest(), getCallback(), domain};
        }
        return new Object[]{getDomainId(), getSplitRequest(), getCallback()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 3) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
            return;
        }
        setDomainId((Long) parameters[0]);
        setSplitRequest((Integer) parameters[1]);
        setCallback((ICallback) parameters[2]);
        try {

            restoreCurrentDomain(getDomainId());
            if (parameters.length == 4) {
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }
    }

    public void proceed() {
        try {
            int splitRequest = getSplitRequest();
            AdminServices adminServices = getAdminServices();
            DomainVOWrapper domain = getModifiedDomain();
            boolean split = splitRequest == TWO_RQUEST;
            List<TransactionVOWrapper> results = new ArrayList<TransactionVOWrapper>();
            results.addAll(adminServices.createDomainModificationTrunsaction(domain, split,  getVisitState().getRequestMetaParameters(), isDisplayRadicalChangesMessage()));
            Summary page = getRequestSummaryPage();
            page.setDomainId(domain.getId());
            page.setTikets(results);
            page.setDomainName(getDomainName());
            page.setCallback(new PageCallback(Home.PAGE_NAME));
            getVisitState().markAsNotVisited(getDomainId());
            getRequestCycle().activate(page);

        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (NoDomainModificationException e) {
            setErrorMessage(getMessageUtil().getNoDomainModificationMessage(e.getDomainName()));
        } catch (DNSTechnicalCheckExceptionWrapper e) {
            setErrorMessage(e.getMessage());
        } catch (TransactionExistsException e) {
            setErrorMessage(getMessageUtil().getTransactionExistMessage(e.getDomainName()));
        } catch (NameServerChangeNotAllowedException e) {
            setErrorMessage(getMessageUtil().getNameServerChangeNotAllowedErrorMessage());
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
        } catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getRadicalAlterationCheckMessage(e.getDomainName()));
        }
    }

    public void makeMoreChanges(){
        getCallback().performCallback(getRequestCycle());
    }

}
