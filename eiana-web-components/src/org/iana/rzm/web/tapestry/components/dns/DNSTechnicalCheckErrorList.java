package org.iana.rzm.web.tapestry.components.dns;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;

import java.util.List;

public abstract class DNSTechnicalCheckErrorList extends BaseComponent {

    @Component(id = "errors", type = "For", bindings = {
        "element=literal:tr", "source=prop:errors", "value=prop:error", "index=prop:index"})
    public abstract IComponent getErrorsComponent();

    @Component(id = "error", type = "Insert", bindings = {"value=prop:errorMessage"})
    public abstract IComponent getHostnameComponent();

    public String getErrorMessage(){
        return String.valueOf(getErrorNumber()) + ". " + getError();
    }

    private int getErrorNumber() {
        return getIndex() + 1;
    }

    public abstract List<String> getErrors();
    public abstract String getError();
    public abstract int getIndex();
}
