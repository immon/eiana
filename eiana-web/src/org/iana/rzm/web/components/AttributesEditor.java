package org.iana.rzm.web.components;

import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidationDelegate;


public interface AttributesEditor {
    public void revert();
    public void setErrorField(IFormComponent field, String message);
    public void preventResubmission();
    public IValidationDelegate getValidationDelegate();
}
