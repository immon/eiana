package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.admin.model.EmailTemplateVOWrapper;


public abstract class EmailTemplateViewer extends BaseComponent{

    @Component(id="subject", type="Insert", bindings = {"value=prop:subject"})
    public abstract IComponent getSubjectComponent();

    @Component(id="template", type="InsertText", bindings = {"value=prop:content"})
    public abstract IComponent getContentComponent();

    @Asset(value = "WEB-INF/admin/EmailTemplateViewer.html")
    public abstract IAsset get$template();

    @Parameter
    public abstract EmailTemplateVOWrapper getTemplate();


    public String getContent(){
        return getTemplate() == null ? null : getTemplate().getContent();
    }

    public String getSubject(){
        return getTemplate() == null ? null : getTemplate().getSubject();
    }

}
