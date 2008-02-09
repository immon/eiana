package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public abstract class ListRequestConfirmations extends BaseComponent {

    @Asset(value = "WEB-INF/ListRequestConfirmations.html")
    public abstract IAsset get$template();

    @Component(id="confirmations",type="For", bindings = {"source=prop:confirmations", "value=prop:confirmation" , "element=literal:tr"})
    public abstract IComponent getConfirmationsComponent();

    @Asset("images/checkbox_on.png")
    public abstract IAsset getCheckboxOn();

    @Asset("images/checkbox_off.png")
    public abstract IAsset getCheckboxOff();

    @Component(id="checkboxImage", type = "Image", bindings = "image=prop:imageAsset")
    public abstract IComponent getCheckboxImageComponent();

    @Component(id="confirmation", type="Insert", bindings = {"value=prop:confirmationContact"})
    public abstract IComponent getConfirmationComponent();

    @Component(id="noconfirmations", type="If", bindings = {"condition=prop:empty"})
    public abstract IComponent getNoRecordComponent();

    @Parameter(required = true)
    public abstract List<ConfirmationVOWrapper>getConfirmations();

    public abstract ConfirmationVOWrapper getConfirmation();

    public boolean isEmpty(){
        return getConfirmations() == null || getConfirmations().size() == 0;
    }

    public IAsset getImageAsset(){
        return getConfirmation().isConfirmed() ? getCheckboxOn() : getCheckboxOff();
    }

    public String getConfirmationContact(){
        return getConfirmation().getContact();
    }


}
