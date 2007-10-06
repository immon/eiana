package org.iana.rzm.web.common;

import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.pages.*;

public interface MyChangePasswordListener {
    public void changePassword(String currentPassword, String newPassword, String confirmNewPassword);
    public IValidationDelegate getValidationDelegate();
    public MessageProperty getPage();
    void reportError(IFormComponent id, String message);
}
