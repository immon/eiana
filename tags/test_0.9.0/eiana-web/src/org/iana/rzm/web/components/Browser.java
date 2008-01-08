package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;

@ComponentClass(allowInformalParameters = true, allowBody = true)
public abstract class Browser extends AbstractComponent implements PageBeginRenderListener {

    public static final int DEFAULT_PAGE_SIZE = 15;

    /**
     * The maximum number of items to display on a page.
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    @Parameter(required = true)
    public abstract EntityQuery getEntityQuery();

    @Parameter
    public abstract String getElement();

    @Parameter(required = true)
    public abstract void setValue(Object value);

    @Persist("client:page")
    public abstract int getResultCount();

    public abstract void setResultCount(int resultCount);

    @Persist("client:page")
    public abstract int getCurrentPage();

    public abstract void setCurrentPage(int currentPage);

    @Persist("client:page")
    public abstract int getPageCount();

    public abstract void setPageCount(int pageCount);

    @Persist
    public abstract PaginatedEntity[] getPageResults();

    public abstract void setPageResults(PaginatedEntity[] pageResults);

    public abstract void setElement(String element);

    //@Parameter(required = true)
    //public abstract IActionListener getListener();


    /**
     * Invoked by the container when the query (otherwise accessed via the query parameter) changes.
     * Re-caches the number of results and sets the current page back to 1.
     */

    public void initializeForResultCount(int resultCount) {
        setResultCount(resultCount);
        setCurrentPage(1);
        setPageCount(computePageCount());
    }


    /**
     * Invoked to change the displayed page number.
     *
     * @param page page to display, numbered from one. The currentPage property will be updated. The
     *             value is constrained to fit in the valid range of pages for the component.
     */

    public void jump(int page) {
        if (page < 2) {
            setCurrentPage(1);
            return;
        }

        int pageCount = getPageCount();
        if (page > getPageCount()) {
            setCurrentPage(pageCount);
            return;
        }

        setCurrentPage(page);
    }

    public boolean getDisableBack() {
        return getCurrentPage() <= 1;
    }

    public boolean getDisableNext() {
        return getCurrentPage() >= getPageCount();
    }

    public String getRange() {
        int currentPage = getCurrentPage();
        int resultCount = getResultCount();

        int low = (currentPage - 1) * pageSize + 1;
        int high = Math.min(currentPage * pageSize, resultCount);

        return low + " - " + high;
    }


    public void pageBeginRender(final PageEvent event) {
        int resultCount = getResultCount();
        int currentPage = getCurrentPage();

        final int low = (currentPage - 1) < 0 ? 0 : (currentPage - 1) * pageSize;
        final int high = Math.min(currentPage * pageSize, resultCount) - 1;

        if(high < 0){
            setPageResults(new PaginatedEntity[0]);
        }

        if (low > high)
            return;

        PaginatedEntity[] pageResults = new PaginatedEntity[0];
        try {
            pageResults = getEntityQuery().get(low, high - low + 1);
            setPageResults(pageResults);
        } catch (NoObjectFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        PaginatedEntity[] entities = getPageResults();
        int count = Tapestry.size(entities);
        String element = getElement();

        for (int i = 0; i < count; i++) {
            setValue(entities[i]);

            if (element != null) {
                writer.begin(element);
                renderInformalParameters(writer, cycle);
            }

            renderBody(writer, cycle);

            if (element != null)
                writer.end();
        }
    }

    protected void finishLoad() {
        setElement("tr");
    }

    private int computePageCount() {
        // For 0 ... pageSize elements, its just one page.

        int resultCount = getResultCount();

        if (resultCount <= pageSize)
            return 1;

        // We need the number of results divided by the results per page.

        int result = resultCount / pageSize;

        // If there's any left-over, then we need an additional page.

        if (resultCount % pageSize > 0)
            result++;

        return result;
    }


}
