package org.iana.rzm.web.tapestry.components.dns;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;

import java.util.List;

public abstract class DNSTechnicalCheckErrorList extends BaseComponent {

    @Component(id = "errors", type = "For", bindings = {
        "element=literal:tr", "source=prop:errors", "value=prop:error"})
    public abstract IComponent getErrorsComponent();

    @Component(id = "error", type = "Insert", bindings = {"value=prop:error"})
    public abstract IComponent getHostnameComponent();

    public abstract List<String> getErrors();
    public abstract String getError();

}
