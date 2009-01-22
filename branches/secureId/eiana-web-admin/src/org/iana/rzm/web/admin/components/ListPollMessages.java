package org.iana.rzm.web.admin.components;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.admin.model.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.tapestry.components.list.*;
import org.iana.web.tapestry.components.browser.*;

@ComponentClass
public abstract class ListPollMessages extends ListRecords implements Sortable {

    @Asset(value = "WEB-INF/admin/ListPollMessages.html")
    public abstract IAsset get$template();

    @Asset(value = "images/spacer.png")
    public abstract IAsset getSpacer();

    @Bean(NullSortFactory.class)
    public abstract SortFactory getNullSortFactory();

    @Component(id = "pagination", type = "tapestry4Commons:BrowserPagination", bindings = {"browser=prop:records"})
    public abstract IComponent getPaginationComponent();

    @Component(id = "records", type = "tapestry4Commons:Browser", bindings = {
        "entityQuery=prop:entityQuery", "value=prop:currentRecord", "element=literal:tr"})
    public abstract IComponent getBrowserComponent();

    @Component(id = "rtId", type = "Insert", bindings = {"value=prop:record.rtId"})
    public abstract IComponent getRtIdComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:record.domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "messageId", type = "Insert", bindings = {"value=prop:record.messageId"})
    public abstract IComponent getUserNameComponent();

    @Component(id = "messageInfo", type = "Insert", bindings = {"value=prop:messageInfo"})
    public abstract IComponent getDomainsComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:record.created"})
    public abstract IComponent getCreatedComponent();


    @Component(id = "viewMessage", type = "DirectLink", bindings = {
        "listener=prop:listener", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getListenerComponent();

    @Component(id = "deleteMessage", type = "DirectLink", bindings = {
        "listener=prop:deleteListener", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getDeleteListenerComponent();

    @Component(id = "requestlink", type = "DirectLink", bindings = {
        "listener=listener:viewRequest", "parameters=prop:record.rtId",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getLinkToRequestComponent();

    @Component(id = "refHeader",
               type = "rzmLib:SortableTableHeader",
               bindings = {"header=literal:Ref", "sortFactory=prop:deligator", "imageVisible=prop:refImageVisible"})
    public abstract IComponent getRefHeaderComponent();

    @Component(id = "createdHeader",
               type = "rzmLib:SortableTableHeader",
               bindings = {"header=literal:Logged", "sortFactory=prop:deligator", "imageVisible=prop:loggedImageVisible"})
    public abstract IComponent getCreatedHeaderComponent();


    @Parameter(required = true)
    public abstract IActionListener getDeleteListener();

    @Parameter(required = false, defaultValue = "prop:nullSortFactory")
    public abstract SortFactory getSortFactory();

    @InjectPage(RequestInformation.PAGE_NAME)
    public abstract RequestInformation getRequestInformationPage();

    @Persist()
    public abstract void setCurrentSorting(SortOrder sortOrder);
    public abstract SortOrder getCurrentSorting();

    public PollMessageVOWrapper getRecord() {
        return (PollMessageVOWrapper) getCurrentRecord();
    }

    public void setSortOrder(SortOrder sortOrder){
        setCurrentSorting(sortOrder);
    }

    public SortOrder getSortOrder(){
        return getCurrentSorting();
    }

    public SortFactory getDeligator() {
        return new SortFactoryDeligator(getSortFactory(), this);
    }

    public boolean isRefImageVisible() {
        return isImageVisibleFor("Ref");
    }

    public boolean isLoggedImageVisible() {
        return isImageVisibleFor("Logged");
    }

    public String getMessageInfo(){
        String message = getRecord().getMessage();
        if(StringUtils.isEmpty(message)){
            return message;
        }

        return message.length() > 10 ?  message.substring(0, 10) + "...." : message;
    }

    public void viewRequest(long rtId){
        LinkTraget target = getRequestInformationPage();
        AdminPage page = (AdminPage) getPage();
        TransactionVOWrapper request = page.getAdminServices().getTransactionByRtId(rtId);
        target.setIdentifier(request.getId());
        getPage().getRequestCycle().activate(target);
    }

    private boolean isImageVisibleFor(String header) {
        return getCurrentSorting() != null && getCurrentSorting().getFieldName().equals(header);
    }

}
