package org.iana.rzm.web.pages.user;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.user.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class ReviewDomainChanges extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "user/ReviewDomainChanges";
    public static final Logger LOGGER = Logger.getLogger(ReviewDomainChanges.class.getName());

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "domainHeader", type = "DomainHeader", bindings = {"countryName=prop:countryName", "domainName=prop:domainName"})
    public abstract IComponent getDomainHeaderComponentComponent();


    @Component(id = "actionlist", type = "For", bindings = {"source=prop:actionList", "value=prop:action"})
    public abstract IComponent getActionForComponent();

    @Component(id = "changeList", type = "For", bindings = {"source=prop:action.changes", "value=prop:change"})
    public abstract IComponent getChangeForComponent();

    @Component(id = "title", type = "Insert", bindings = {"value=prop:changeTitle"})
    public abstract IComponent getTitleComponent();

    @Component(id = "message", type = "Insert", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id = "continueEdit", type = "LinkSubmit", bindings = {"listener=listener:continueEdit"})
    public abstract IComponent getContinueEditComponent();

    @Component(id = "proceed", type = "LinkSubmit", bindings = {"listener=listener:proceed"})
    public abstract IComponent getProceedComponent();

    @Component(id = "div", type = "Any", bindings = {"style=prop:style"})
    public abstract IComponent getStyleComponent();

    @Component(id = "form", type = "Form")
    public abstract IComponent getFormComponent();

    @Component(id = "submitter", type = "TextField", bindings = {
            "value=prop:submitterEmail",
            "displayName=literal:Email",
            "clientValidationEnabled=literal:false",
            "validators=validators:email"})
    public abstract IComponent getSubmitterFieldComponent();

    @Component(id="isNotNameServer", type = "If", bindings = {"condition=prop:notNameServer"})
    public abstract IComponent getIsNotNameServerComponent();

    @Component(id="allowedToSubmit", type = "If", bindings = {"condition=prop:allowedToSubmit"})
    public abstract IComponent getAllowedToSubmitComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:transactionPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
            bindings = {"listener=listener:viewPendingRequests"}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "pendingGlueRequest", type = "If", bindings = {"condition=prop:impactedPartyPending"})
    public abstract IComponent getIsGluePendingComponent();

      @Component(id = "pendingGlueMessage", type = "ShowPendingRequestsMessage", bindings = {
            "listener=listener:viewGlueRequests",
            "pendigRequestMessage=literal:This domain is part of a Glue change. Edits to Name Servers are disabled until the currently glue change is resolved "
            })
    public abstract IComponent getPendingGlueMessage();


    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @InjectPage("user/SeparateRequest")
    public abstract SeparateRequest getSeparateRequestPage();

    @InjectPage("user/Summary")
    public abstract Summary getSummaryPage();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    @InjectPage("user/UserRequestsPerspective")
    public abstract UserRequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long id);

    @Persist("client")
    public abstract List<ActionVOWrapper> getActionList();
    public abstract void setActionList(List<ActionVOWrapper> list);

    @Persist("client")
    @InitialValue("literal:false")
    public abstract void setSeparateRequest(boolean value);
    public abstract boolean isSeparateRequest();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @Persist("client")
    @InitialValue("literal:false")
    public abstract void setMustSplitRequest(boolean value);
    public abstract boolean isMustSplitRequest();

    @InitialValue("literal:false")
    public abstract void setTransactionPending(boolean value);
    public abstract boolean isTransactionPending();

    @InitialValue("literal:false")
    public abstract void setImpactedPartyPending(boolean b);
    public abstract boolean isImpactedPartyPending();

    public abstract ActionVOWrapper getAction();
    public abstract ChangeVOWrapper getChange();

    public abstract String getDomainName();
    public abstract String getSubmitterEmail();

    public abstract void setSubmitterEmail(String email);
    public abstract void setDomainName(String domainName);

    public abstract void setCountryName(String name);
    public abstract String getCountryName();

    public abstract void setNameServerChange(boolean nameServerChange);
    public abstract boolean isNameServerChange();

    public boolean isAllowedToSubmit(){
        return !isTransactionPending() && !isImpactedPartyPending();
    }

    public  boolean isNoTransaction(){
        return  !isTransactionPending();
    }

    public String getChangeTitle() {
        return getAction().getTitle();
    }

    public String getStyle() {
        return "margin: 0 0 20px 60px; width: 75%;";
    }

    public String getMessage() {
        return getCounter() + ". " + getMessageBuilder().message(getChange());
    }

    public int getCounter() {
        return getCounterBean().getCounter();
    }

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));
        DomainVOWrapper currentDomain = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(currentDomain.getName());
        setSubmitterEmail(getVisitState().getSubmitterEmail());
        try {
            SystemDomainVOWrapper domain = getUserServices().getDomain(currentDomain.getId());
            setTransactionPending(domain.isOperationPending());

            if (getActionList() == null) {
                TransactionActionsVOWrapper transactionActions = getUserServices().getChanges(currentDomain);
                setActionList(transactionActions.getChanges());
                setNameServerChange(transactionActions.isNameServerChange());
                setSeparateRequest(transactionActions.offerSeparateRequest());
                setMustSplitRequest(transactionActions.mustSplitrequest());
            }

            boolean impactedParty = false;
            if(!domain.isOperationPending()){
                int count =
                    getUserServices().getTransactionCount(CriteriaBuilder.impactedParty(Arrays.asList(domain.getName())));
                impactedParty = count >0 && isNameServerChange();
              }

              setImpactedPartyPending(impactedParty);


        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }



    public boolean isNotNameServer(){
        return !isNameServerChange();
    }


    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(), getActionList(), isSeparateRequest(), isMustSplitRequest(), getSubmitterEmail(), domain};
        }
        return new Object[]{getDomainId(), getActionList(), isSeparateRequest(), isMustSplitRequest(), getSubmitterEmail()};
    }


    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 5) {
            getExternalPageErrorHandler().handleExternalPageError(
                    getMessageUtil().getSessionRestorefailedMessage());
        }

        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setActionList((List<ActionVOWrapper>) parameters[1]);
        setSeparateRequest(Boolean.valueOf(parameters[2].toString()));
        setMustSplitRequest(Boolean.valueOf(parameters[3].toString()));
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length == 6) {
                restoreModifiedDomain((DomainVOWrapper) parameters[5]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(
                    getMessageUtil().getSessionRestorefailedMessage());
            LOGGER.warn("NoObjectFoundException ", e);
        }

        getVisitState().setSubmitterEmail(parameters[4] != null ? parameters[4].toString() : null);
    }


    public void proceed() {
        getVisitState().setSubmitterEmail(getSubmitterEmail());
        if (isSeparateRequest() || isMustSplitRequest()) {
            SeparateRequest separateRequestPage = getSeparateRequestPage();
            separateRequestPage.setDomainId(getDomainId());
            separateRequestPage.setDomainName(getVisitState().getCurrentDomain(getDomainId()).getName());
            separateRequestPage.setMustSplit(isMustSplitRequest());
            separateRequestPage.setImpactedPartyPending(isImpactedPartyPending());
            getRequestCycle().activate(separateRequestPage);
        } else {
            returnSummaryPage();
        }
    }

    public IPage continueEdit() {
        getVisitState().setSubmitterEmail(getSubmitterEmail());
        ReviewDomain reviewDomain = getReviewDomainPage();
        reviewDomain.setDomainId(getDomainId());
        return reviewDomain;
    }


    private void returnSummaryPage() {
        try {
            DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
            TransactionVOWrapper transaction = getUserServices().createTransaction(domain, getSubmitterEmail());
            Summary summaryPage = getSummaryPage();
            summaryPage.setTikets(Arrays.asList(transaction));
            summaryPage.setDomainName(domain.getName());
            getVisitState().markAsNotVisited(getDomainId());
            getRequestCycle().activate(summaryPage);
        } catch (NoObjectFoundException e) {
            log(LOGGER, "No Object Found Exception", Level.WARN);
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (NoDomainModificationException e) {
            setErrorMessage("You can not modified this Domain " + e.getDomainName() + " At This time");
        } catch (DNSTechnicalCheckExceptionWrapper e) {
            setErrorMessage(e.getMessage());
        } catch (TransactionExistsException e) {
            setTransactionPending(true);
        } catch (NameServerChangeNotAllowedException e) {
            setImpactedPartyPending(true);
        }
    }



    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getVisitState().getCurrentDomain(getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
        return page;
    }

    public UserRequestsPerspective viewGlueRequests(){
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new ImpactedPartyTransactionFetcher(Arrays.asList(getVisitState().getCurrentDomain(getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
        page.setImpactedParty(true);
        return page;
    }
}



