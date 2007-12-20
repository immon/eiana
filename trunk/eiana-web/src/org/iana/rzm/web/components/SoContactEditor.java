package org.iana.rzm.web.components;

import org.apache.commons.lang.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public abstract class SoContactEditor extends ContactEditor {

    protected void validateInput() {
        Map<String, String> contactAttributes = getContactAttributes();
        validateReqiredField(contactAttributes, ContactVOWrapper.ORGANISATION, getOrganisationField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ADDRESS, getAddressField());
        validateCountryCode(contactAttributes.get(ContactVOWrapper.COUNTRY));

        if (isUsePrivateEmail()) {
            validateEmail(getPrivateEmail(), getPrivateEmailField(), ContactVOWrapper.PRIVATE_EMAIL);
        }

        if (getRole()) {
            if (StringUtils.isBlank(contactAttributes.get(ContactVOWrapper.JOB_TITLE))) {
                getEditor().setErrorField(getJobTitleField(), "Role accounts must have a Job Title");
            }
        }
    }

    public void save() {
        getEditor().preventResubmission();
        Map<String, String> attributes = getContactAttributes();
        attributes.put(ContactVOWrapper.NAME, attributes.get(ContactVOWrapper.ORGANISATION));

        if (isValidationRequired()) {
            validateInput();
            if (getEditor().getValidationDelegate().getHasErrors()) {
                return;
            }
        }

        getEditor().save(getContactAttributes());
    }

}
