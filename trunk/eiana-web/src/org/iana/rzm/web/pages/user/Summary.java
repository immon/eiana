package org.iana.rzm.web.pages.user;

import org.apache.log4j.Logger;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.model.ActionVOWrapper;
import org.iana.rzm.web.model.ChangeMessageBuilder;
import org.iana.rzm.web.model.ChangeVOWrapper;
import org.iana.rzm.web.model.TransactionVOWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Summary extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "user/Summary";
    public static final Logger LOGGER = Logger.getLogger(Summary.class.getName());

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

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

    @Persist("client:page")
    public abstract List<TransactionVOWrapper> getTikets();

    @Persist("client:page")
    public abstract void setSummaryList(List<SummaryBean> list);

    public abstract void setDomainName(String domainName);
    public abstract String getDomainName();

    public abstract void setTikets(List<TransactionVOWrapper> transactions);

    public abstract void setCountryName(String name);

    public abstract String getCountryName();

    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public abstract int getIndex();

    public abstract SummaryBean getSummaryValue();
    public abstract void setSummaryValue(SummaryBean bean);

    public void pageBeginRender(PageEvent event) {

        setCountryName("(" + getUserServices().getCountryName(getDomainName()) + ")");

        List<TransactionVOWrapper> tickets = getTikets();
        List<SummaryBean> list = new ArrayList<SummaryBean>();

        for (TransactionVOWrapper ticket : tickets) {
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
        int counter = getIndex() + 1;
        return counter + ". " + getMessageBuilder().message(getChange());

    }

    protected Object[] getExternalParameters() {
        return new Object[]{getTikets(), getDomainName()};
    }

    private static class SummaryBean implements Serializable {

        private long rtId;
        private List<ActionVOWrapper> changes;

        public SummaryBean(long rtId, List<ActionVOWrapper> changes) {
            this.rtId = rtId;
            this.changes = changes;
        }
        
        public List<ActionVOWrapper> getChanges() {
            return changes;
        }

        public String getTicketNumber() {
            return "Ticket " + rtId;
        }
    }

}

