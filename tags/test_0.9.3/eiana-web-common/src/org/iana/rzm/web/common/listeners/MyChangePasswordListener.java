package org.iana.rzm.web.common.listeners;

import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;

public interface MyChangePasswordListener {
    
    public void changePassword(String currentPassword, String newPassword, String confirmNewPassword);
    public IValidationDelegate getValidationDelegate();
    public void reportError(IFormComponent id, String message);
    public void cancel();
}
