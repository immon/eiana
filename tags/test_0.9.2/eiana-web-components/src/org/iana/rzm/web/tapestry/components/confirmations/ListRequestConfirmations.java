package org.iana.rzm.web.tapestry.components.confirmations;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.model.*;

import java.util.*;

public abstract class ListRequestConfirmations extends BaseComponent {
        
    @Component(id="confirmations",type="For", bindings = {"source=prop:confirmations", "value=prop:confirmation" , "element=literal:tr"})
    public abstract IComponent getConfirmationsComponent();

    @Component(id="checkboxImage", type = "Image", bindings = "image=prop:imageAsset")
    public abstract IComponent getCheckboxImageComponent();

    @Component(id="confirmation", type="Insert", bindings = {"value=prop:confirmationContact"})
    public abstract IComponent getConfirmationComponent();

    @Component(id="noconfirmations", type="If", bindings = {"condition=prop:empty"})
    public abstract IComponent getNoRecordComponent();
    

    @Asset("checkbox_on.png")
    public abstract IAsset getCheckboxOn();

    @Asset("checkbox_off.png")
    public abstract IAsset getCheckboxOff();

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
