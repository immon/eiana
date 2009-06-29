package org.iana.rzm.web.tapestry.editors;

import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;


public interface AttributesEditor {
    public void revert();
    public void setErrorField(IFormComponent field, String message);
    public void preventResubmission();
    public IValidationDelegate getValidationDelegate();
}
