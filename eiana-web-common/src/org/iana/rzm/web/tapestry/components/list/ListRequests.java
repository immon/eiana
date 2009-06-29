package org.iana.rzm.web.tapestry.components.list;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.rzm.web.common.query.*;
import org.iana.web.tapestry.components.browser.*;

public abstract class ListRequests extends ListRecords implements Sortable {

    @Bean(NullSortFactory.class)
    public abstract SortFactory getNullSortFactory();

    @Component(id = "pagination", type = "tapestry4lib:BrowserPagination", bindings = {"browser=prop:records"})
    public abstract IComponent getPaginationComponent();

    @Component(id = "records", type = "tapestry4lib:Browser", bindings = {
        "entityQuery=prop:entityQuery", "value=prop:currentRecord", "element=literal:tr", "style=prop:currentRecordStyle"})                                                                                               
    public abstract IComponent getBrowserComponent();

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:record.rtIdAsString"})
    public abstract IComponent getRtComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:record.domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:record.created"})
    public abstract IComponent getCreatedComponent();

    @Component(id = "state", type = "Insert", bindings = {"value= prop:currentStateAsString"})
    public abstract IComponent getStateComponent();

    @Component(id = "createdBy", type = "Insert", bindings = {"value=prop:record.createdBy"})
    public abstract IComponent getCreatedByComponent();

    @Component(id = "modified", type = "Insert", bindings = {"value=prop:record.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id = "viewRequest", type = "DirectLink", bindings = {
        "listener=prop:listener", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"
        })
    public abstract IComponent getListenerComponent();

    @Component(id = "domainLink", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:goToDomain", "parameters=prop:record.domainName"
        })
    public abstract IComponent getDomainLinkComponent();



    @Component(id = "showCancelRequest",
               type = "If",
               bindings = {"condition=prop:requestDeleteble", "element=literal:div"})
    public abstract IComponent getShowCancelRequestComponent();

    @Component(id = "cancelRequest", type = "DirectLink", bindings = {
        "listener=listener:cancelRequest", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
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

    @Component(id="reviewImage", type="If", bindings = {"condition=prop:specialReview"})
    public abstract IComponent getReviewImageComponent();

    @Asset("orange_dot_sm.png")
    public abstract IAsset getOrangeDotImage();

    public abstract String getActionTitle();
    public abstract LinkTraget getLinkTragetPage();
    public abstract String getCancelRequestPage();
    public abstract SortFactory getSortFactory();
    public abstract boolean isCancelRequestEnabled();
    public abstract boolean isSpecialReviewImage();
        
    @Persist("client")
    public abstract void setCurrentSorting(SortOrder sortOrder);
    public abstract SortOrder getCurrentSorting();

    public String getCurrentStateAsString(){
        RzmPage page = (RzmPage) getPage();
        return page.isUserPage() ?
                getRecord().getCurentUserStateAsString() : 
                getRecord().getCurrentStateAsString();
    }

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

    public boolean isRecordSpecialReview(){
        return ((TransactionVOWrapper) getCurrentRecord()).isSpecialReview() && isSpecialReviewImage();
    }

    public String getCurrentRecordStyle(){
        return isRecordSpecialReview() ? "color:orange" : null;
    }


    public boolean isSpecialReview(){
        PaginatedEntity[] tvr = getRecords().getPageResults();
        for (PaginatedEntity entity : tvr) {
            if(((TransactionVOWrapper) entity).isSpecialReview()){
                return isSpecialReviewImage();
            }
        }
        return false;
    }


    public void goToDomain(String domainName) {
        ProtectedPage page  = (ProtectedPage) getPage();
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