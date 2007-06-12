package org.iana.rzm.web.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidatorException;
import org.iana.rzm.web.common.NameServerAttributesEditor;
import org.iana.rzm.web.model.NameServerValue;
import org.iana.rzm.web.tapestry.validator.WebDomainNameValidator;
import org.iana.rzm.web.tapestry.validator.WebIPListValidator;

import java.util.Iterator;
import java.util.List;

public abstract class NameServerListEditor extends BaseComponent {


    @Component(id = "form", type = "Form", bindings = {"success=listener:save", "delegate=prop:validationDelegate"})
    public abstract IComponent getFormComponent();

    @Component(id = "for", type = "For", bindings = {"source=prop:list", "value=prop:nameServer", "element=literal:tr"})
    public abstract IComponent getForComponent();

    @Component(id = "hostname", type = "TextField", bindings = {
            "value=prop:nameServer.hostName", "displayName=message:hostname-label", "validators=validators:required,domainName"})
    public abstract IComponent getHostNameTextComponent();

    @Component(id = "ips", type = "TextField", bindings = {
            "value=prop:nameServer.ips", "displayName=message:ips-label", "validators=validators:required,ipListValidator"})
    public abstract IComponent getIpTextComponent();

    @Component(id = "emptyhostname", type = "TextField", bindings = {
            "value=prop:newHostName", "displayName=message:hostname-label"})
    public abstract IComponent getNewHostNameTextComponent();

    @Component(id = "emptyips", type = "TextField", bindings = {"value=prop:newIpList"})
    public abstract IComponent getNewIPTextComponent();

    @Component(id = "deleteNameServer", type = "LinkSubmit", bindings = {
            "action=listener:deleteNameServer", "tag=literal:deleteNameServer", "selected=prop:listenerTag",
            "parameters=nameServer.id"
            })
    public abstract IComponent getDeleteComponent();

    @Component(id = "addNameServer", type = "LinkSubmit", bindings = {
            "action=listener:addNameServer", "tag=literal:addNameServer", "selected=prop:listenerTag"})
    public abstract IComponent getAddComponent();

    @Component(id="revert", type="DirectLink", bindings = {"listener=listener:revert",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"
            })
    public abstract IComponent getRevertComponent();

    @Component(id="save", type = "LinkSubmit")
    public abstract IComponent getSaveComponent();
    
    @Asset("images/add.png")
    public abstract IAsset getAdd();

    @Asset("images/delete.png")
    public abstract IAsset getDelete();

    @InjectComponent("emptyhostname")
    public abstract IFormComponent getNewHostNameField();

    @InjectComponent("emptyips")
    public abstract IFormComponent getNewIpsField();

    @Parameter(name = "editor", required = true)
    public abstract NameServerAttributesEditor getEditor();

    @Parameter(name = "list", required = true)
    public abstract List<NameServerValue> getList();

    public abstract void setList(List<NameServerValue> list);

    public abstract NameServerValue getNameServer();

    public abstract String getNewHostName();

    public abstract void setNewHostName(String hostname);

    public abstract String getNewIpList();

    public abstract void setNewIpList(String ips);

    public abstract String getListenerTag();

    public IValidationDelegate getValidationDelegate() {
        return getEditor().getValidationDelegate();
    }

    public void addNameServer() {

        String newHostName = getNewHostName();
        String newIpList = getNewIpList();

        WebDomainNameValidator webDomainNameValidator = new WebDomainNameValidator();
        WebIPListValidator     webIPListValidator = new WebIPListValidator();
        try {
            webDomainNameValidator.validate(getNewHostNameField(),null, newHostName);
        } catch (ValidatorException e) {
            getEditor().getValidationDelegate().record(e);
        }

        try {
            webIPListValidator.validate(getNewIpsField(), null, newIpList);
        } catch (ValidatorException e) {
            getEditor().getValidationDelegate().record(e);
        }

        List<NameServerValue> list = getList();

        for (NameServerValue nameServerValue : list) {
            if(nameServerValue.getHostName().equals(newHostName)){
                getEditor().getValidationDelegate().record(getNewHostNameField(),"Duplicate Host name");
            }
        }

        if (getEditor().getValidationDelegate().getHasErrors()) {
            return;
        }

        list.add(new NameServerValue(newHostName, newIpList));
        setList(list);
        setNewHostName(null);
        setNewIpList(null);
    }

    public void deleteNameServer(long id) {

        if (getValidationDelegate().getHasErrors()) {
            return;
        }

        List<NameServerValue> list = getList();
        for (Iterator<NameServerValue> iterator = list.iterator(); iterator.hasNext();) {
            NameServerValue value = iterator.next();
            if (value.getId() == id) {
                iterator.remove();
            }
        }
        setList(list);
    }

    public void save() {
        String listenerTag = getListenerTag();
        boolean listenerInvoked = listenerTag != null &&
                (listenerTag.equals("addNameServer") || listenerTag.equals("deleteNameServer"));

        if (listenerInvoked) {
            return;
        }

        if (getNewHostName() != null || getNewIpList() != null) {
            addNameServer();
        }

        getEditor().preventResubmission();
        if (getEditor().getValidationDelegate().getHasErrors()) {
            return;
        }


        getEditor().save(getList());
    }


    public void revert() {
        getEditor().getValidationDelegate().clearErrors();
        getEditor().revert();
    }

}
