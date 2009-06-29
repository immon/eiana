package org.iana.rzm.web.common.validators;

import org.apache.tapestry.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.form.validator.*;
import org.apache.tapestry.valid.*;

import java.io.*;
import java.net.*;

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
