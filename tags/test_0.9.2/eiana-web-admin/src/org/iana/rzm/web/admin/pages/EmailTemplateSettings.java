package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.admin.components.EmailTemplateEventListener;
import org.iana.rzm.web.admin.components.EmailTemplateSelector;
import org.iana.rzm.web.admin.model.EmailTemplateVOWrapper;


public abstract class EmailTemplateSettings extends AdminPage implements PageBeginRenderListener, EmailTemplateEventListener {

    public static final String PAGE_NAME = "EmailTemplateSettings";

    @Component(id = "selector", type = "EmailTemplateSelector", bindings = {
            "emailTemplateEventListener=prop:emailTemplateEventListener"
            })
    public abstract IComponent getEmailTemplateSelectorComponent();

    @Component(id = "viewer", type = "EmailTemplateViewer", bindings = {"template=prop:template"})
    public abstract IComponent getEmailTemplateViewerComponent();

    @Component(id = "editor", type = "EmailTemplateEditor", bindings = {
            "templateConfig=prop:template",
            "emailTemplateEventListener=prop:emailTemplateEventListener"
            })
    public abstract IComponent getTemplateEditorComponent();

    @InjectComponent("selector")
    public abstract EmailTemplateSelector getEmailTemplateSelector();

    @Persist("client")
    public abstract void setTemplate(EmailTemplateVOWrapper templateVOWrapper);

    @Persist
    public abstract String getSelectedBlockId();
    public abstract void setSelectedBlockId(String id);

    private static final String[] BLOCKS = {
            "VIEW",
            "EDIT"
    };

    public void pageBeginRender(PageEvent event) {
        if(getSelectedBlockId() == null){
            setSelectedBlockId(BLOCKS[0]);
        }
    }

    public void onLoad(EmailTemplateVOWrapper emailTemplateVOWrapper) {
        setTemplate(emailTemplateVOWrapper);
    }

    public void onCancel(EmailTemplateVOWrapper vo) {
        viewTemplate();
        getEmailTemplateSelector().setTemplateName(vo.getName());
    }

    public void onEdit(String templateName) {
        doClick(BLOCKS[1]);
    }

    public void onSave(EmailTemplateVOWrapper vo){
        getAdminServices().saveTemplate(vo);
        setInfoMessage(getMessageUtil().getChangesSavedSuccessfullyMessage());
        viewTemplate();
        getEmailTemplateSelector().setTemplateName(vo.getName());
    }

    public EmailTemplateEventListener getEmailTemplateEventListener() {
        return this;
    }

    public Block getSelectedBlock() {
        String selectedId = blockId();
        return (Block) getComponent(selectedId);
    }

    public void doClick(String selectedId) {
        setSelectedBlockId(selectedId);
    }

    public boolean isViewerBlockSelected() {
        String blockId = blockId();
        return blockId.equals(BLOCKS[0]);
    }

    public boolean isEditBlockSelected() {
        String blockId = blockId();
        return blockId.equals(BLOCKS[1]);
    }

    public void viewTemplate() {
        doClick(BLOCKS[0]);
    }

    public void editTemplate() {
        doClick(BLOCKS[1]);
    }

    private String blockId() {
        String blockId = getSelectedBlockId();
        if (blockId == null) {
            blockId = BLOCKS[0];
        }
        return blockId;
    }

}
