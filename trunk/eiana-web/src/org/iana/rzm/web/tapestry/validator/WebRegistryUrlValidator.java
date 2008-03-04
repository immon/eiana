package org.iana.rzm.web.tapestry.validator;

import org.apache.tapestry.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.form.validator.*;
import org.apache.tapestry.valid.*;

import java.io.*;
import java.net.*;

public class WebRegistryUrlValidator implements Validator {

    public void validate(IFormComponent field, ValidationMessages messages, Object object) throws ValidatorException {
        try {

            if (object == null) {
                return;
            }

            String regestryurl = object.toString();
            URL url = new URL(regestryurl);
            new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (MalformedURLException e) {
            throw new ValidatorException(e.getMessage(), ValidationConstraint.URL_FORMAT);
        } catch (IOException e) {
            throw new ValidatorException("Invalid URL " + object, ValidationConstraint.URL_FORMAT);
        }
    }

    public boolean getAcceptsNull() {
        return false;
    }

    public boolean isRequired() {
        return true;
    }

    public void renderContribution(IMarkupWriter writer,
                                   IRequestCycle cycle,
                                   FormComponentContributorContext context,
                                   IFormComponent field) {
    }
}
