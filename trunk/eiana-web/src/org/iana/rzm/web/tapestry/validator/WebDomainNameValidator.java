package org.iana.rzm.web.tapestry.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.form.validator.Validator;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidatorException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.validators.NameValidator;

public class WebDomainNameValidator implements Validator {

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {
        String domainName = (String) object;
        if(StringUtils.isBlank(domainName)){
            throw new ValidatorException("Invalid Domain name ", ValidationConstraint.REQUIRED);
        }

        try{
            String name = object.toString();
            if(name.endsWith(".")){
                name = name.substring(0, name.length() - 1);
            }
            NameValidator.validateName(name);
        }catch(InvalidNameException e){
            throw new ValidatorException("Invalid Domain name " + e.getName() + " " + e.getReason());
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
