package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;

public abstract class ImpactedPartiesListRequest extends ListRecords {

    @Bean(NullSortFactory.class)
    public abstract SortFactory getNullSortFactory();

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:record.rtIdAsString"})
    public abstract IComponent getRtComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:record.created"})
    public abstract IComponent getCreatedComponent();

    @Component(id = "state", type = "Insert", bindings = {"value= prop:record.currentStateAsString"})
    public abstract IComponent getStateComponent();

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

    @Component(id = "refHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Ref", "sortFactory=prop:deligator", "imageVisible=prop:refImageVisible"})
    public abstract IComponent getRefHeaderComponent();

    @Component(id = "createdHeader",
               type = "SortableTableHeader",
               bindings = {"header=literal:Logged", "sortFactory=prop:deligator", "imageVisible=prop:loggedImageVisible"})
    public abstract IComponent getCreatedHeaderComponent();

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
        

    @Parameter(required = false, defaultValue = "prop:nullSortFactory")
    public abstract SortFactory getSortFactory();

    @Persist("client:page")
    public abstract void setCurrentSorting(SortOrder sortOrder);

    public abstract SortOrder getCurrentSorting();

    public boolean isRefImageVisible() {
        return isImageVisibleFor("Ref");
    }

    public boolean isLoggedImageVisible() {
        return isImageVisibleFor("Logged");
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
        return new SortFactoryDeligator(getSortFactory());
    }

    private class SortFactoryDeligator implements SortFactory {
        private SortFactory factory;

        SortFactoryDeligator(SortFactory factory) {
            this.factory = factory;
        }

        public boolean isFieldSortable(String name) {
            return factory.isFieldSortable(name);
        }

        public void sort(String field, boolean accending) {
            setCurrentSorting(new SortOrder(field, accending));
            factory.sort(field, accending);
        }
    }


}
