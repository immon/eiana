package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

@ComponentClass
public abstract class SubDomain extends BaseComponent {

    @Component(id="whois", type="RzmInsert",
               bindings = {
                   "value=prop:whoisServer",
                   "originalValue=prop:originalWhoisServer",
                   "modifiedStyle=literal:edited"
                   })
    public abstract IComponent getWhoisServerComponent();

    @Component(id="registryUrl", type="RzmInsert",
               bindings = {
                   "value=prop:registryUrl",
                   "originalValue=prop:originalRegistryUrl",
                   "modifiedStyle=literal:edited"
                   })
    public abstract IComponent getRegistryUrlComponent();

    @Component(id="editible", type="If",  bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibileComponent();

      @Component(id = "edit", type = "DirectLink", bindings = {
        "listener=prop:listener", "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @Parameter(required = true)
    public abstract  void setRegistryUrl(String url);

    @Parameter(required = true)
    public abstract void setOriginalRegistryUrl(String url);

     @Parameter(required = true)
    public abstract void setWhoisServer(String server);

    @Parameter(required = true)
    public abstract void setOriginalWhoisServer(String server);

    @Parameter(required = true)
    public abstract IActionListener getListener();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isEditible();

    
}
                                            