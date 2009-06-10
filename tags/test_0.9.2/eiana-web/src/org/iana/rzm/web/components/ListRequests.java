package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.tapestry.*;

@ComponentClass(allowBody = true)
public abstract class ListRequests extends ListRecords implements Sortable {

    @Bean(NullSortFactory.class)
    public abstract SortFactory getNullSortFactory();

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:record.rtIdAsString"})
    public abstract IComponent getRtComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:record.domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:record.created"})
    public abstract IComponent getCreatedComponent();

    @Component(id = "state", type = "Insert", bindings = {"value= prop:record.currentStateAsString"})
    public abstract IComponent getStateComponent();

    @Component(id = "createdBy", type = "Insert", bindings = {"value=prop:record.createdBy"})
    public abstract IComponent getCreatedByComponent();

    @Component(id = "modified", type = "Insert", bindings = {"value=prop:record.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id = "viewRequest", type = "DirectLink", bindings = {
        "listener=prop:listener", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"
        })
    public abstract IComponent getListenerComponent();


    @Component(id = "domainLink", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:goToDomain", "parameters=prop:record.domainName"
        })
    public abstract IComponent getDomainLinkComponent();

    @Component(id = "showCancelRequest",
               type = "If",
               bindings = {"condition=prop:requestDeleteble", "element=literal:div"})
    public abstract IComponent getShowCancelRequestComponent();

    @Component(id = "cancelRequest", type = "DirectLink", bindings = {
        "listener=listener:cancelRequest", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelRequestComponent();

    @Component(id = "refHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Ref", "sortFactory=prop:deligator", "imageVisible=prop:refImageVisible"})
    public abstract IComponent getRefHeaderComponent();

    @Component(id = "domainHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Domain", "sortFactory=prop:deligator", "imageVisible=prop:domainImageVisible"})
    public abstract IComponent getDomainHeaderComponent();

    @Component(id = "createdHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Logged", "sortFactory=prop:deligator", "imageVisible=prop:loggedImageVisible"})
    public abstract IComponent getCreatedHeaderComponent();

    @Component(id = "createdByHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Created By", "sortFactory=prop:deligator", "imageVisible=prop:createdByImageVisible"})
    public abstract IComponent getCreatedByHeaderComponent();

    @Component(id = "stateHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Current Status", "sortFactory=prop:deligator", "imageVisible=prop:currentStatusImageVisible"})
    public abstract IComponent getStateHeaderComponent();

    @Component(id = "modifiedHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Last Change", "sortFactory=prop:deligator", "imageVisible=prop:modifiedImageVisible"})
    public abstract IComponent getModifiedHeaderComponent();

    @Parameter(required = false, defaultValue = "literal:View")
    public abstract String getActionTitle();

    @Parameter(required = true)
    public abstract LinkTraget getLinkTragetPage();

    @Parameter(required = true)
    public abstract String getCancelRequestPage();

    @Parameter(required = false, defaultValue = "prop:nullSortFactory")
    public abstract SortFactory getSortFactory();

    @Parameter(required = false, defaultValue = "literal:true")
    public abstract boolean isCancelRequestEnabled();
        
    @Persist
    public abstract void setCurrentSorting(SortOrder sortOrder);
    public abstract SortOrder getCurrentSorting();

    public void setSortOrder(SortOrder sortOrder){
        setCurrentSorting(sortOrder);
    }

    public SortOrder getSortOrder(){
        return getCurrentSorting();
    }
    

    public boolean isRefImageVisible() {
        return isImageVisibleFor("Ref");
    }

    public boolean isDomainImageVisible() {
        return isImageVisibleFor("Domain");
    }

    public boolean isLoggedImageVisible() {
        return isImageVisibleFor("Logged");
    }

    public boolean isCreatedByImageVisible() {
        return isImageVisibleFor("Created By");
    }

    public boolean isCurrentStatusImageVisible() {
        return isImageVisibleFor("Current Status");
    }

    public boolean isModifiedImageVisible() {
        return isImageVisibleFor("Last Change");
    }

    private boolean isImageVisibleFor(String header) {
        return getCurrentSorting() != null && getCurrentSorting().getFieldName().equals(header);
    }

    public TransactionVOWrapper getRecord() {
        return (TransactionVOWrapper) getCurrentRecord();
    }

    public SortFactory getDeligator() {
        return new SortFactoryDeligator(getSortFactory(), this);
    }

    public boolean isRequestDeleteble() {
        return ((TransactionVOWrapper) getCurrentRecord()).canCancel() && isCancelRequestEnabled();
    }


    public void goToDomain(String domainName) {
        Protected page  = (Protected) getPage();
        LinkTraget target = getLinkTragetPage();
        target.setIdentifier(domainName);
        target.setCallback(page.createCallback());
        getPage().getRequestCycle().activate(target);
    }

    public void cancelRequest(long requestId) {
        LinkTraget page = (LinkTraget) getPage().getRequestCycle().getPage(getCancelRequestPage());
        page.setIdentifier(requestId);
        getPage().getRequestCycle().activate(page);
    }

}