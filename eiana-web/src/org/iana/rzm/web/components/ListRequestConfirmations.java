package org.iana.rzm.web.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.model.Confirmation;

import java.util.List;

public abstract class ListRequestConfirmations extends BaseComponent {

    @Asset(value = "WEB-INF/ListRequestConfirmations.html")
    public abstract IAsset get$template();

    @Component(id="confirmations",type="For", bindings = {"source=prop:confirmations", "value=prop:confirmation"})
    public abstract IComponent getConfirmationsComponent();

    @Asset("images/checkbox_on.png")
    public abstract IAsset getCheckboxOn();

    @Asset("images/checkbox_off.png")
    public abstract IAsset getCheckboxOff();

    @Component(id="checkboxImage", type = "Image", bindings = "image=prop:imageAsset")
    public abstract IComponent getCheckboxImageComponent();

    @Component(id="confirmation", type="Insert", bindings = {"value=prop:confirmationContact"})
    public abstract IComponent getConfirmationComponent();

    @Parameter(required = true)
    public abstract List<Confirmation>getConfirmations();

    public abstract Confirmation getConfirmation();

    public IAsset getImageAsset(){
        return getConfirmation().isConfirmed() ? getCheckboxOn() : getCheckboxOff();
    }

    public String getConfirmationContact(){
        return getConfirmation().getContact();
    }


}
