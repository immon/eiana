package org.iana.rzm.web.common.validators;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.form.validator.Validator;
import org.apache.tapestry.valid.ValidatorException;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class WebWhoisValidator implements Validator {

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {

        if(object == null){
            return;
        }

        String whois = object.toString();

        try {
             new Socket(whois,43);
        }catch(UnknownHostException e){
             throw new ValidatorException("Invalid Whois server " + object.toString());
        }
        catch (IOException e) {
            throw new ValidatorException("Invalid Whois server " + object.toString());
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
