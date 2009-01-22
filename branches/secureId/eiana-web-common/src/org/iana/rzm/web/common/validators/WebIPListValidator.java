package org.iana.rzm.web.common.validators;

import org.apache.tapestry.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.form.validator.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.utils.*;

import java.util.*;

public class WebIPListValidator implements Validator {

    private IPListValidator validator = new IPListValidator();

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {

        if(object == null){
             throw new ValidatorException("Invalid IP Address ", ValidationConstraint.REQUIRED);
        }

        List<IPAddressVOWrapper> list = WebUtil.toVos(object.toString());

        if(!validator.isValid(list)){
            throw new ValidatorException("Invalid IP Address " + object.toString());
        }
    }

    public boolean getAcceptsNull() {
        return false;
    }

    public boolean isRequired() {
        return false;
    }

    public void renderContribution(IMarkupWriter writer,
                                   IRequestCycle cycle,
                                   FormComponentContributorContext context,
                                   IFormComponent field) {

    }
}
