package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.facade.admin.config.EmailTemplateNames;
import org.iana.rzm.web.admin.model.EmailTemplateVOWrapper;
import org.iana.rzm.web.admin.pages.AdminPage;
import org.iana.rzm.web.admin.services.AdminServices;


public abstract class EmailTemplateSelector extends BaseComponent implements PageBeginRenderListener {

    private static final IPropertySelectionModel TEMPLATES =
            new StringPropertySelectionModel(EmailTemplateNames.EMAIL_TEMPLATE_NAMES);

    @Component(id = "form", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "refresh=listener:selectTemplate",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getTemplateFormComponent();

    @Component(id = "templateLabel", type = "FieldLabel", bindings = {"field=component:templates"})
    public abstract IComponent getTemplatesLabelComponent();

    @Component(id = "templates", type = "PropertySelection", bindings = {"model=prop:model", "value=prop:templateName",
            "onchange=prop:javaScriptRefresh", "displayName=literal:Template:"})
    public abstract IComponent getTemplatesDropDownComponent();

    @Component(id = "edit", type = "LinkSubmit", bindings = {"action=listener:editTemplate"})
    public abstract IComponent getEditLinkComponent();

    @Asset(value = "WEB-INF/admin/EmailTemplateSelector.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @Parameter
    public abstract EmailTemplateEventListener getEmailTemplateEventListener();


    public abstract String getTemplateName();
    public abstract void setTemplateName(String name);

    public IPropertySelectionModel getModel() {
        return TEMPLATES;
    }

    public void pageBeginRender(PageEvent event){
        if(!event.getRequestCycle().isRewinding()){
            if(getTemplateName() == null){
                setTemplateName(EmailTemplateNames.EMAIL_TEMPLATE_NAMES[0]);
                selectTemplate();
            }
        }

    }

    public String getJavaScriptRefresh(){
        return "javascript:this.form.events.refresh();";
    }

    public void selectTemplate(){
        EmailTemplateVOWrapper template = getAdminServices().getEmailTemplate(getTemplateName());
        getEmailTemplateEventListener().onLoad(template);
    }

    public IValidationDelegate getValidationDelegate(){
        return ((AdminPage)getPage()).getValidationDelegate();
    }

    public void editTemplate(){
        getEmailTemplateEventListener().onEdit(getTemplateName());        
    }


}
