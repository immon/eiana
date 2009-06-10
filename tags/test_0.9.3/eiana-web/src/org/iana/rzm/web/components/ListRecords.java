package org.iana.rzm.web.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.PaginatedEntity;
import org.iana.rzm.web.util.DateUtil;

import java.text.Format;

public abstract class ListRecords extends BaseComponent {

    @Component( id="usePagination", type="If", bindings = {"condition=ognl:records.resultCount > 10"})
    public abstract IComponent getUsePaginationComponent();

    @Component(id="pagination", type="BrowserPagination", bindings = {"browser=prop:records"})
    public abstract IComponent getPaginationComponent();

    @Component(id="noRecordsMsg", type="Insert", bindings = {"value=prop:noRecordsMessage"})
    public abstract IComponent getNoRecordmessageComponent();

    @Component( id="records", type="Browser", bindings = {
        "entityQuery=prop:entityQuery","value=prop:currentRecord","element=literal:tr"})
    public abstract IComponent getBrowserComponent();

    @Component(id="norecords", type="If", bindings = {"condition=prop:empty"})
    public abstract IComponent getNoRecordComponent();

    @Parameter(required = true)
    public abstract EntityQuery getEntityQuery();

    @Parameter(required = true)
    public abstract IActionListener getListener();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isUsePagination();

    @Parameter(required = false, defaultValue = "literal:There are no records to display")
    public abstract String getNoRecordsMessage();

    @InjectComponent("records")
    public abstract Browser getRecords();

    public abstract PaginatedEntity getCurrentRecord();


    public boolean isEmpty(){
        return getRecords().getResultCount() == 0;
    }

    public Format getformat() {
        return DateUtil.DEFAULT_DATE_FORMAT;
    }

}