package org.iana.rzm.web.pages.user;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.util.CounterBean;

import java.util.Arrays;
import java.util.List;

public abstract class ReviewDomainChanges extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "user/ReviewDomainChanges";
    public static final Logger LOGGER = Logger.getLogger(ReviewDomainChanges.class.getName());

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

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

    @Persist("client:form")
    public abstract long getDomainId();

    public abstract void setDomainId(long id);

    @Persist("client:form")
    public abstract List<ActionVOWrapper> getActionList();

    public abstract void setActionList(List<ActionVOWrapper> list);

    @Persist("client:form")
    @InitialValue("literal:false")
    public abstract void setSeparateRequest(boolean value);

    public abstract boolean isSeparateRequest();

    @Persist("client:form")
    public abstract DomainVOWrapper getModifiedDomain();

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @Persist("client:form")
    @InitialValue("literal:false")
    public abstract void setMustSplitRequest(boolean value);

    public abstract boolean isMustSplitRequest();

    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public abstract String getDomainName();

    public abstract String getSubmitterEmail();

    public abstract void setSubmitterEmail(String email);

    public abstract void setDomainName(String domainName);

    public abstract void setCountryName(String name);

    public abstract String getCountryName();

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
        setModifiedDomain(getVisitState().getMmodifiedDomain());
        DomainVOWrapper currentDomain = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(currentDomain.getName());
        setSubmitterEmail(getVisitState().getSubmitterEmail());
        try {
            if (getActionList() == null) {
                TransactionActionsVOWrapper transactionActions = getUserServices().getChanges(currentDomain);
                setActionList(transactionActions.getChanges());
                setSeparateRequest(transactionActions.offerSeparateRequest());
                setMustSplitRequest(transactionActions.mustSplitrequest());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
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

        getVisitState().setSubmitterEmail(parameters[4].toString());
    }


    public void proceed() {
        getVisitState().setSubmitterEmail(getSubmitterEmail());
        if (isSeparateRequest()) {
            SeparateRequest separateRequestPage = getSeparateRequestPage();
            separateRequestPage.setDomainId(getDomainId());
            separateRequestPage.setDomainName(getVisitState().getCurrentDomain(getDomainId()).getName());
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
        }
    }
}



