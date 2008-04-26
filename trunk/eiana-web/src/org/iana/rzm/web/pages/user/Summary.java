package org.iana.rzm.web.pages.user;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class Summary extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "user/Summary";
    public static final Logger LOGGER = Logger.getLogger(Summary.class.getName());

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "domainHeader", type = "DomainHeader", bindings = {"countryName=prop:countryName", "domainName=prop:domainName"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "summary", type = "For", bindings = {"source=ognl:summaryList", "value=ognl:summaryValue"})
    public abstract IComponent getSummaryForComponent();

    @Component(id = "rtTicketNumber", type = "Insert", bindings = {"value=ognl:summaryValue.ticketNumber"})
    public abstract IComponent getRTTicketNumberComponent();

    @Component(id = "actionlist", type = "For", bindings = {"source=ognl:summaryValue.changes", "value=ognl:action"})
    public abstract IComponent getActionForComponent();

    @Component(id = "changeList", type = "For", bindings = {"source=ognl:action.changes", "value=ognl:change"})
    public abstract IComponent getChangeForComponent();

    @Component(id = "title", type = "Insert", bindings = {"value=ognl:action.title"})
    public abstract IComponent getTitleComponent();

    @Component(id = "message", type = "Insert", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id="backToOverview", type="DirectLink", bindings = {"listener=listener:backToOverview",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getOverviewComponent();

    @InjectPage("user/UserHome")
    public abstract UserHome getHome();

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @Persist("client:page")
    public abstract List<TransactionVOWrapper> getTikets();
    public abstract void setTikets(List<TransactionVOWrapper> transactions);

    @Persist("client:page")
    public abstract void setSummaryList(List<SummaryBean> list);

    @Persist("client:page")
    public abstract void setDomainName(String domainName);
    public abstract String getDomainName();

    public abstract void setCountryName(String name);
    public abstract String getCountryName();

    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public abstract int getIndex();

    public abstract SummaryBean getSummaryValue();
    public abstract void setSummaryValue(SummaryBean bean);

    @InitialValue("false")
    public abstract void setShowTiketingErrorMessage(boolean flag);

    public void pageBeginRender(PageEvent event) {
        setCountryName(getUserServices().getCountryName(getDomainName()));
        List<TransactionVOWrapper> tickets = getTikets();
        List<SummaryBean> list = new ArrayList<SummaryBean>();

        for (TransactionVOWrapper ticket : tickets) {
            if(ticket.getState().equals(TransactionStateVOWrapper.State.PENDING_CREATION)){
                    setShowTiketingErrorMessage(true);
            }
            long rtId = ticket.getRtId();
            list.add(new SummaryBean(rtId,ticket.getChanges()));
        }

        setSummaryList(list);

    }



    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0 || parameters.length < 2){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setTikets((List<TransactionVOWrapper>) parameters[0]);
        setDomainName(parameters[1].toString());
    }


    public UserHome backToOverview() {
        return getHome();
    }

    public String getMessage() {
        return getCounterBean().getCounter() + ". " + getMessageBuilder().message(getChange());
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getTikets(), getDomainName()};
    }
}

