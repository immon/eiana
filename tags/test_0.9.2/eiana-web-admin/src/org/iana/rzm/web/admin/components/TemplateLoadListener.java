package org.iana.rzm.web.admin.components;

import org.iana.rzm.web.admin.model.EmailTemplateVOWrapper;


public interface TemplateLoadListener {
    public void onLoad(EmailTemplateVOWrapper emailTemplateVOWrapper);
}
