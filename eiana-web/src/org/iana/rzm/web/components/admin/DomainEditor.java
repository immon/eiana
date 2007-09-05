package org.iana.rzm.web.components.admin;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.util.*;

import java.io.*;
import java.util.*;

public abstract class DomainEditor extends BaseComponent implements PageBeginRenderListener {

    public static final TypeModel TYPES = new TypeModel();

    @Component(id = "editDomain", type = "Form", bindings = {
        "clientValidationEnabled=literal:false",
        "success=listener:save",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditDomainComponent();

    @Component(id = "domain", type = "TextField", bindings = {"value=prop:domain.name",
        "disabled=prop:edit", "displayName=message:domain-label"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "description", type = "TextArea", bindings = {
        "displayName=message:description-label", "value=prop:domain.description"})
    public abstract IComponent getDescriptionComponent();

    @Component(id = "types", type = "PropertySelection", bindings = {
        "displayName=message:type-label", "model=prop:model", "value=prop:domain.type",
        "validators=validators:required"
        })
    public abstract IComponent getTypeComponent();

     @Component(id = "subDomain", type = "SubDomain", bindings = {
            "registryUrl=prop:domain.registryUrl",
            "originalRegistryUrl=prop:originalDomain.registryUrl",
            "whoisServer=prop:domain.whoisServer",
            "originalWhoisServer=prop:originalDomain.whoisServer",
            "listener=prop:subDomainListener",
            "editible=literal:true"
            })
    public abstract IComponent getSubDomainComponent();

    @Component(id = "specialInstructions", type = "TextArea", bindings = {
        "displayName=message:specialInstructions-label", "value=prop:domain.specialInstructions"})
    public abstract IComponent getSpecialInstructionsComponent();

    @Component(id = "so", type = "DomainContactEditor",
               bindings = {
                   "contactType=literal:Sponsoring Organization",
                   "domainId=prop:domainId",
                   "originalContact=prop:soOriginalContact",
                   "contact=prop:soContact",
                   "contactListener=prop:contactListener"
                   }

    )
    public abstract IComponent getSoContactComponent();

    @Component(id = "ac", type = "DomainContactEditor",
               bindings = {
                   "contactType=literal:Administrative",
                   "domainId=prop:domainId",
                   "originalContact=prop:acOriginalContact",
                   "contact=prop:acContact",
                   "contactListener=prop:contactListener"
                   }
    )
    public abstract IComponent getAcContactComponent();

    @Component(id = "tc", type = "DomainContactEditor",
               bindings = {
                   "contactType=literal:Technical",
                   "domainId=prop:domainId",
                   "originalContact=prop:tcOriginalContact",
                   "contact=prop:tcContact",
                   "contactListener=prop:contactListener"
                   }
    )
    public abstract IComponent getTcContactComponent();

    @Component(id = "listNameServers", type = "ListNameServers", bindings = {
        "nameServers=prop:nameServers",
        "domainId=prop:domain.id",
        "listener=prop:nameServerListener",
        "editible=literal:true"
        })
    public abstract IComponent getListNameServerComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Asset(value = "WEB-INF/admin/DomainEditor.html")
    public abstract IAsset get$template();

    @Parameter(required = true)
    public abstract SystemDomainVOWrapper getDomain();

    @Parameter(required = true)
    public abstract DomainAttributeEditor getEditor();

    @Parameter(required = true)
    public abstract IActionListener getContactListener();

    @Parameter(required = true)
    public abstract IActionListener getNameServerListener();

    @Parameter(required = true)
    public abstract IActionListener getSubDomainListener();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isEdit();

    @InjectPage("admin/AdminGeneralError")
    public abstract AdminGeneralError getErrorPage();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectObject("service:rzm.AccessDeniedHandler")
    public abstract AccessDeniedHandler getAccessDeniedHandler();

    @InjectState("visit")
    public abstract Visit getVisitState();

    public abstract void setOriginalDomain(SystemDomainVOWrapper domain);

    public abstract SystemDomainVOWrapper getOriginalDomain();

    public long getDomainId() {
        return getDomain().getId();
    }

    public IPropertySelectionModel getModel() {
        return TYPES;
    }

    public ContactVOWrapper getSoOriginalContact() {
        return getOriginalDomain().getSupportingOrganization();
    }

    public ContactVOWrapper getSoContact() {
        return getDomain().getSupportingOrganization();
    }

    public ContactVOWrapper getAcOriginalContact() {
        return getOriginalDomain().getAdminContact();
    }

    public ContactVOWrapper getAcContact() {
        return getDomain().getAdminContact();
    }

    public ContactVOWrapper getTcOriginalContact() {
        return getOriginalDomain().getTechnicalContact();
    }

    public ContactVOWrapper getTcContact() {
        return getDomain().getTechnicalContact();
    }

    public void pageBeginRender(PageEvent event) {
        try {
            if (isEdit()) {
                setOriginalDomain(getAdminServices().getDomain(getDomainId()));
            } else {
                setOriginalDomain(new SystemDomainVOWrapper());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public List<NameServerValue> getNameServers() {
        List<NameServerVOWrapper> originals = getOriginalDomain().getNameServers();
        List<NameServerVOWrapper> current = new ArrayList<NameServerVOWrapper>(getDomain().getNameServers());
        return WebUtil.buildNameServerList(originals, current);
    }


    public void save() {

        DomainVOWrapper org = getOriginalDomain();
        SystemDomainVOWrapper domain = getDomain();
        long id = getDomainId();
        Visit visit = getVisitState();

        if (!equal(org.getRegistryUrl(), domain.getRegistryUrl())) {
            visit.markDomainDirty(id);
        }

        if (!equal(org.getWhoisServer(), domain.getWhoisServer())) {
            visit.markDomainDirty(id);
        }

        if (!equal(org.getDescription(), domain.getDescription())) {
            visit.markDomainDirty(id);
        }

        if (!equal(org.getTypeAsString(), domain.getTypeAsString())) {
            visit.markDomainDirty(id);
        }

        if (!equal(org.getSpecialInstructions(), domain.getSpecialInstructions())) {
            visit.markDomainDirty(id);
        }


        getEditor().save();
    }

    private boolean equal(String originalValue, String newValue) {
        return StringUtils.equalsIgnoreCase(originalValue, newValue);
    }

    public void revert() {
        getEditor().revert();
    }

    public IValidationDelegate getValidationDelegate() {
        return getEditor().getValidationDelegate();
    }

    private static class TypeModel implements IPropertySelectionModel, Serializable {

        private DomainVOWrapper.Type[] types;

        public TypeModel() {
            types = DomainVOWrapper.Type.values();
        }

        public int getOptionCount() {
            return types.length;
        }

        public Object getOption(int index) {
            return types[index];
        }

        public String getLabel(int index) {
            return types[index].getDisplayName();
        }

        public String getValue(int index) {
            return String.valueOf(index);
        }

        public Object translateValue(String value) {
            int i = Integer.parseInt(value);
            return types[i];
        }
    }

}