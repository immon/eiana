package org.iana.rzm.web.tapestry.components.link;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.Component;

public abstract class OverviewLink extends BaseComponent {


    @Component(id="title", type="Insert", bindings = {"value=prop:title"})
    public abstract IComponent getTitleComponent();

    @Component(id="actionTitle", type="Insert", bindings = {"value=prop:actionTitle"})
    public abstract IComponent getActionTitleComponent();

    @Component(id="link", type="DirectLink", bindings = {
            "listener=listener:doLink",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getLinkComponent();

    public abstract String getActionTitle();
    public abstract String getTitle();
    public abstract IPage getPage();


    public void doLink(){
        getPage().getRequestCycle().activate(getPage());
    }

}
