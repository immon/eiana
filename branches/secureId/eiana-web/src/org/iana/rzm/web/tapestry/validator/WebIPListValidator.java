package org.iana.rzm.web.tapestry.validator;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.form.validator.Validator;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidatorException;
import org.iana.rzm.web.model.IPAddressVOWrapper;
import org.iana.rzm.web.util.WebUtil;
import org.iana.rzm.web.validator.IPListValidator;

import java.util.List;

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
