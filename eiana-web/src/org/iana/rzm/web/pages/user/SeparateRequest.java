package org.iana.rzm.web.pages.user;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.user.*;

import java.util.*;

public abstract class SeparateRequest extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "user/SeparateRequest";
    public static final Logger LOGGER = Logger.getLogger(SeparateRequest.class.getName());

    public final static Integer ONE_RQUEST = 1;
    public final static Integer TWO_RQUEST = 2;

    @Component(id = "form", type = "Form", bindings = {"cancel=listener:moreChanges"})
    public abstract IComponent getFormComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryComponent();

    @Component(id = "splitRequest", type = "RadioGroup", bindings = {"selected=prop:splitRequest", "disabled=prop:mustSplit" })
    public abstract IComponent getSplitRequestComponent();

    @Component(id = "oneRequest", type = "Radio", bindings = {
            "value=ognl:@org.iana.rzm.web.pages.user.SeparateRequest@ONE_RQUEST"
            })
    public abstract IComponent getOneRequestComponent();

    @Component(id = "twoRequest", type = "Radio", bindings = {
            "value=ognl:@org.iana.rzm.web.pages.user.SeparateRequest@TWO_RQUEST"
            })
    public abstract IComponent getTwoRequestComponent();

    @Component(id = "makeChanges", type = "DirectLink", bindings = {"listener=listener:makeMoreChanges"})
    public abstract IComponent getMakeChangesComponent();

    @Component(id = "proceed", type = "LinkSubmit", bindings = {"action=listener:proceed"})
    public abstract IComponent getLinkSubmitComponent();

    @InjectPage("user/Summary")
    public abstract Summary getSummaryPage();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    @Persist("client:page")
    public abstract void setDomainId(long domainId);

    public abstract long getDomainId();

    @Persist("client:page")
    public abstract void setSplitRequest(int value);

    public abstract int getSplitRequest();

    @Persist("client:page")
    public abstract void setMustSplit(boolean value) ;
    public abstract boolean isMustSplit();

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract void setDomainName(String name);

    public abstract String getDomainName();

    public abstract void setCountryName(String countryName);

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getMmodifiedDomain());

        if (getSplitRequest() == 0 || isMustSplit()) {
            setSplitRequest(TWO_RQUEST);
        }

        DomainVOWrapper domainVOWrapper = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(domainVOWrapper.getName());
        setCountryName("(" + getUserServices().getCountryName(domainVOWrapper.getName()) + ")");
    }


    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(), getSplitRequest(), domain};
        }
        return new Object[]{getDomainId(), getSplitRequest()};


    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 2) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }
        setDomainId((Long) parameters[0]);
        setSplitRequest((Integer) parameters[1]);
        try {

            restoreCurrentDomain(getDomainId());
            if(parameters.length == 2){
                restoreModifiedDomain((DomainVOWrapper) parameters[2]);
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
            if (splitRequest == TWO_RQUEST || isMustSplit() ) {
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
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }catch(NoDomainModificationException e){
            setErrorMessage("You can not modified this Domain " + e.getDomainName() + " At This time");
        } catch (CreateTicketException e) {
            // todo: "create ticket" error
        }

    }

    public ReviewDomain makeMoreChanges() {
        ReviewDomain reviewDomainPage = getReviewDomainPage();
        reviewDomainPage.setDomainId(getDomainId());
        return reviewDomainPage;
    }


}
