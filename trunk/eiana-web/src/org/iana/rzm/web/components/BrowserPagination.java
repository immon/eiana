package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.components.*;
import org.apache.tapestry.link.*;


@ComponentClass(allowBody = true, allowInformalParameters = true)
public abstract class BrowserPagination extends BaseComponent {

    @Parameter(required = true)
    public abstract Browser getBrowser();

    //@Component(id = "range", type = "Insert", bindings = {"value=prop:browser.range"})
    //public abstract Insert getRangeComponent();

    @Component(id = "currentPage", type = "Insert", bindings = {"value=prop:browser.currentPage"})
    public abstract Insert getCurrentPageComponent();

    @Component(id = "pageCount", type = "Insert", bindings = {"value=prop:browser.pageCount"})
    public abstract Insert getPageCountComponent();

    @Component(id = "previous", type = "DirectLink", bindings = {
        "listener=listeners.selectBrowserPage",
        "parameters=ognl:@@max(browser.currentPage - 1, 1)",
        "disabled=prop:browser.disableBack",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"

        })
    public abstract DirectLink getPreviosComponent();

    @Component(id = "next", type = "DirectLink", bindings = {
        "listener=listeners.selectBrowserPage",
        "parameters=ognl:@@min(browser.currentPage + 1, browser.pageCount)",
        "disabled=prop:browser.disableNext",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"
        })
    public abstract DirectLink getNextComponent();

    @Asset("images/arrow_next.png")
    public abstract IAsset getNextArrow();

    @Asset("images/arrow_prev.png")
    public abstract IAsset getPrevArrow();

    public boolean getShowPreviousLink() {
        return getBrowser().getDisableBack();
    }

    public boolean getShowNextLink() {
        return getBrowser().getDisableNext();
    }

    public void selectBrowserPage(int page) {
        Browser browser = getBrowser();
        if (browser == null)
            throw Tapestry.createRequiredParameterException(this, "browser");

        browser.jump(page);
    }
}
