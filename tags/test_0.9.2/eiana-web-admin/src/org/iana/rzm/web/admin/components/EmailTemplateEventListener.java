package org.iana.rzm.web.admin.components;

import org.iana.rzm.web.admin.model.EmailTemplateVOWrapper;


public interface EmailTemplateEventListener {
    public void onEdit(String templateName);
    public void onSave(EmailTemplateVOWrapper vo);
    public void onLoad(EmailTemplateVOWrapper vo);
    public void onCancel(EmailTemplateVOWrapper vo);
}
