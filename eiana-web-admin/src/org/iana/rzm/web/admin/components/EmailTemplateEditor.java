package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.admin.model.EmailTemplateVOWrapper;
import org.iana.rzm.web.admin.pages.AdminPage;


public abstract class EmailTemplateEditor extends BaseComponent {

    @Component(id = "title", type = "Insert", bindings = {"value=prop:name"})
    public abstract IComponent getTitleComponent();

    @Component(id = "editor", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "success=listener:save",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getEditTemplateComponent();

    @Component(id = "signed", type = "Checkbox", bindings = {"value=prop:signed", "displayName=literal:Signed:"})
    public abstract IComponent getSignedCheckBoxComponent();

    @Component(id = "signedLabel", type = "FieldLabel", bindings = {"field=component:signed"})
    public abstract IComponent getSignedLabelComponent();

    @Component(id = "subject", type = "TextField", bindings = {"value=prop:subject", "displayName=literal:Subject:",
            "validators=validators:required"})
    public abstract IComponent getSubjectComponent();

    @Component(id = "content", type = "TextArea", bindings = {"displayName=literal:Template content:", "value=prop:content",
            "validators=validators:required"})
    public abstract IComponent getContentComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getEditLinkComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Asset(value = "WEB-INF/admin/EmailTemplateEditor.html")
    public abstract IAsset get$template();

    @Parameter
    public abstract EmailTemplateVOWrapper getTemplateConfig();

    @Parameter
    public abstract EmailTemplateEventListener getEmailTemplateEventListener();

    public IValidationDelegate getValidationDelegate() {
        return ((AdminPage) getPage()).getValidationDelegate();
    }

    public String getName() {
        return getTemplateConfig().getName();
    }

    public String getSubject() {
        return getTemplateConfig().getSubject();
    }

    public void setSubject(String subject) {
        getTemplateConfig().setSubject(subject);
    }

    public String getContent() {
        return getTemplateConfig().getContent();
    }

    public void setContent(String content) {
        getTemplateConfig().setContent(content);
    }

    public boolean isSigned() {
        return getTemplateConfig().isSigned();
    }

    public void setSigned(boolean signed) {
        getTemplateConfig().setSigned(signed);
    }

    public void save() {
        if (getValidationDelegate().getHasErrors()) {
            return;
        }

        getEmailTemplateEventListener().onSave(getTemplateConfig());
    }

    public void revert() {
        getEmailTemplateEventListener().onCancel(getTemplateConfig());
    }
}
