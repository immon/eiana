package org.iana.rzm.web.tapestry.components.subdomain;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

public abstract class SubDomain extends BaseComponent {

    @Component(id="whois", type="tapestry4lib:TrackableInsert",
               bindings = {
                   "value=prop:whoisServer",
                   "originalValue=prop:originalWhoisServer",
                   "modifiedStyle=literal:edited"
                   })
    public abstract IComponent getWhoisServerComponent();

    @Component(id="registryUrl", type="tapestry4lib:TrackableInsert",
               bindings = {
                   "value=prop:registryUrl",
                   "originalValue=prop:originalRegistryUrl",
                   "modifiedStyle=literal:edited"
                   })
    public abstract IComponent getRegistryUrlComponent();

    @Component(id="editible", type="If",  bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibileComponent();

      @Component(id = "edit", type = "DirectLink", bindings = {
        "listener=prop:listener", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    public abstract  void setRegistryUrl(String url);
    public abstract void setOriginalRegistryUrl(String url);
    public abstract void setWhoisServer(String server);
    public abstract void setOriginalWhoisServer(String server);
    public abstract IActionListener getListener();
    public abstract boolean isEditible();

    
}
                                            