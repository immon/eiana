package org.iana.rzm.web.user.pages;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.user.query.retriver.*;
import org.iana.rzm.web.user.services.*;

import java.util.*;

public abstract class SeparateRequest extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "SeparateRequest";
    public static final Logger LOGGER = Logger.getLogger(SeparateRequest.class.getName());

    public final static Integer ONE_RQUEST = 1;
    public final static Integer TWO_RQUEST = 2;

    @Component(id = "form", type = "Form", bindings = {"cancel=listener:moreChanges"})
    public abstract IComponent getFormComponent();

    @Component(id = "domainHeader",
               type = "rzmLib:DomainHeader",
               bindings = {"countryName=prop:countryName", "domainName=prop:domainName"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "splitRequest",
               type = "RadioGroup",
               bindings = {"selected=prop:splitRequest", "disabled=prop:mustSplit"})
    public abstract IComponent getSplitRequestComponent();

    @Component(id = "oneRequest", type = "Radio", bindings = {
        "value=ognl:@org.iana.rzm.web.user.pages.SeparateRequest@ONE_RQUEST"
        })
    public abstract IComponent getOneRequestComponent();

    @Component(id = "twoRequest", type = "Radio", bindings = {
        "value=ognl:@org.iana.rzm.web.user.pages.SeparateRequest@TWO_RQUEST"
        })
    public abstract IComponent getTwoRequestComponent();

    @Component(id = "makeChanges", type = "DirectLink", bindings = {"listener=listener:makeMoreChanges"})
    public abstract IComponent getMakeChangesComponent();

    @Component(id = "proceed", type = "LinkSubmit", bindings = {"action=listener:proceed"})
    public abstract IComponent getLinkSubmitComponent();

    @Component(id = "allowedToSubmit", type = "If", bindings = {"condition=prop:allowedToSubmit"})
    public abstract IComponent getAllowedToSubmitComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:transactionPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests"}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "pendingGlueRequest", type = "If", bindings = {"condition=prop:impactedPartyPending"})
    public abstract IComponent getIsGluePendingComponent();

    @Component(id = "pendingGlueMessage", type = "rzmLib:ShowPendingRequestsMessage", bindings = {
        "listener=listener:viewGlueRequests",
        "pendigRequestMessage=literal:This domain is part of a Glue change. Edits to Name Servers are disabled until the currently glue change is resolved "
        })
    public abstract IComponent getPendingGlueMessage();


    @InjectPage(Summary.PAGE_NAME)
    public abstract Summary getSummaryPage();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomainPage();

    @InjectPage(UserRequestsPerspective.PAGE_NAME)
    public abstract UserRequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract void setDomainId(long domainId);
    public abstract long getDomainId();

    @Persist("client")
    public abstract void setSplitRequest(int value);
    public abstract int getSplitRequest();

    @Persist("client")
    public abstract void setMustSplit(boolean value);
    public abstract boolean isMustSplit();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract void setDomainName(String name);
    public abstract String getDomainName();

    @InitialValue("literal:false")
    @Persist("client")
    public abstract void setTransactionPending(boolean value);
    public abstract boolean isTransactionPending();

    @InitialValue("literal:false")
    @Persist("client")
    public abstract void setImpactedPartyPending(boolean b);
    public abstract boolean isImpactedPartyPending();

    public abstract void setCountryName(String countryName);

    public boolean isNoTransaction() {
        return !isTransactionPending();
    }

    public boolean isAllowedToSubmit(){
        return !isTransactionPending() && !isImpactedPartyPending();
    }

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        if (getSplitRequest() == 0 || isMustSplit()) {
            setSplitRequest(TWO_RQUEST);
        }

        DomainVOWrapper domainVOWrapper = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(domainVOWrapper.getName());
        setCountryName(getUserServices().getCountryName(domainVOWrapper.getName()));
    }


    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(),
                getSplitRequest(),
                isTransactionPending(),
                isImpactedPartyPending(),
                domain};
        }
        return new Object[]{getDomainId(), getSplitRequest(), isTransactionPending(), isImpactedPartyPending()};


    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 4) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }
        setDomainId((Long) parameters[0]);
        setSplitRequest((Integer) parameters[1]);
        setTransactionPending(Boolean.valueOf(parameters[2].toString()));
        setImpactedPartyPending(Boolean.valueOf(parameters[3].toString()));
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length == 4) {
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError("Can not restore session");
            LOGGER.warn("NoObjectFoundException ", e);
        }
    }

    public void proceed() {
        try {
            int splitRequest = getSplitRequest();
            UserServices userServices = getUserServices();
            DomainVOWrapper domain = getModifiedDomain();

            List<TransactionVOWrapper> results = new ArrayList<TransactionVOWrapper>();
            if (splitRequest == TWO_RQUEST || isMustSplit()) {
                results.addAll(userServices.createTransactions(domain, getVisitState().getSubmitterEmail()));
            } else {
                results.add(userServices.createTransaction(domain, getVisitState().getSubmitterEmail()));
            }
            Summary page = getSummaryPage();
            page.setTikets(results);
            page.setDomainName(getDomainName());
            getVisitState().markAsNotVisited(getDomainId());
            getRequestCycle().activate(page);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (NoDomainModificationException e) {
            setErrorMessage(getMessageUtil().getDomainModificationErrorMessage(e.getDomainName()));
        } catch (DNSTechnicalCheckExceptionWrapper e) {
            setErrorMessage(e.getMessage());
        } catch (TransactionExistsException e) {
            setTransactionPending(true);
        } catch (NameServerChangeNotAllowedException e) {
            setImpactedPartyPending(true);
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
        } catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getAllNameServersChangeMessage());
        }
    }

    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
        return page;
    }

    public UserRequestsPerspective viewGlueRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new ImpactedPartyTransactionRetriver(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
        page.setImpactedParty(true);
        return page;
    }

    public ReviewDomain makeMoreChanges() {
        ReviewDomain reviewDomainPage = getReviewDomainPage();
        reviewDomainPage.setDomainId(getDomainId());
        return reviewDomainPage;
    }


}
