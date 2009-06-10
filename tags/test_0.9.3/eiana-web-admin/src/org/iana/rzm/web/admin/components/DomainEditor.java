package org.iana.rzm.web.admin.components;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.codevalues.*;
import org.iana.commons.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.pages.editors.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.services.*;
import org.iana.rzm.web.common.utils.*;

import java.io.*;
import java.util.*;

public abstract class DomainEditor extends BaseComponent implements PageBeginRenderListener {

    public static DomainStatusModel STATUS_MODEL = new DomainStatusModel();

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

    @Component(id = "ianaCode", type = "TextField", bindings = {
            "displayName=message:ianaCode-label", "value=prop:domain.ianaCode"})
        public abstract IComponent getIanaCodeComponent();


    @Component(id = "types", type = "PropertySelection", bindings = {
        "displayName=message:type-label", "model=prop:model", "value=prop:type",
        "validators=validators:required"
        })
    public abstract IComponent getTypeComponent();

    @Component(id = "statuses", type = "PropertySelection", bindings = {
        "displayName=message:status-label", "model=ognl:@org.iana.rzm.web.admin.components.DomainEditor@STATUS_MODEL", "value=prop:domain.status",
        "validators=validators:required"
        })
    public abstract IComponent getStatusComponent();

    @Component(id = "subDomain", type = "rzmLib:SubDomain", bindings = {
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

    @Component(id = "listNameServers", type = "rzmLib:ListNameServers", bindings = {
        "nameServers=prop:nameServers",
        "domainId=prop:domain.id",
        "listener=prop:nameServerListener",
        "editible=literal:true"
        })
    public abstract IComponent getListNameServerComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id = "sendEmail", type = "Checkbox", bindings = {
        "displayName=message:sendemail-label", "value=prop:domain.sendEmail"})
    public abstract IComponent getSendEmailComponent();

    @Component(id = "emailLabel", type = "FieldLabel", bindings = {"field=component:sendEmail"})
    public abstract IComponent getSendEmailLabelComponent();

     @Component(id = "specialReview", type = "Checkbox", bindings = {
        "displayName=message:specialReview-label", "value=prop:domain.specialReview"})
    public abstract IComponent getSpecialReviewComponent();

    @Component(id = "specialReviewLabel", type = "FieldLabel", bindings = {"field=component:specialReview"})
    public abstract IComponent getSpecialReviewLabelComponent();

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

    @InjectPage(GeneralError.PAGE_NAME)
    public abstract GeneralError getErrorPage();

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

    @Persist("client")
    public abstract List<Value> getDomainTypes();

    public abstract void setDomainTypes(List<Value> values);

    public long getDomainId() {
        return getDomain().getId();
    }

    public IPropertySelectionModel getModel() {
        return new TypeModel(getDomainTypes());
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

    public Value getType() {
        final String type = getDomain().getType();

        Value value = ListUtil.find(getDomainTypes(), new ListUtil.Predicate<Value>() {
            public boolean evaluate(Value object) {
                return object.getValueId().equals(type);
            }
        });

        if (value == null) {
            return null;
        }

        return value;
    }

    public void setType(Value value) {
        if (value != null) {
            getDomain().setType(value.getValueId());
        }
    }

    public void pageBeginRender(PageEvent event) {
        setDomainTypes(new ArrayList<Value>(getAdminServices().getDomainTypes()));
        try {
            if (isEdit()) {
                setOriginalDomain(getAdminServices().getDomain(getDomainId()));
            } else {
                setOriginalDomain(new SystemDomainVOWrapper());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
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
            visit.markDomainDirty(id, DomainChangeType.sudomain);
        }

        if (!equal(org.getWhoisServer(), domain.getWhoisServer())) {
            visit.markDomainDirty(id, DomainChangeType.sudomain);
        }

        if (!equal(org.getDescription(), domain.getDescription())) {
            visit.markDomainDirty(id, DomainChangeType.admin);
        }

        if (!equal(org.getType(), domain.getType())) {
            visit.markDomainDirty(id,DomainChangeType.admin);
        }

        if (!equal(org.getSpecialInstructions(), domain.getSpecialInstructions())) {
            visit.markDomainDirty(id,DomainChangeType.admin);
        }

        if((org.isSpecialReview() != domain.isSpecialReview())){
            visit.markDomainDirty(id,DomainChangeType.admin);
        }

        if (org.isSendEmail() != domain.isSendEmail()) {
            visit.markDomainDirty(id, DomainChangeType.admin);
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
        private List<Value> types;

        public TypeModel(List<Value> types) {
            this.types = types;
        }

        public int getOptionCount() {
            return types.size();
        }

        public Object getOption(int index) {
            return types.get(index);
        }

        public String getLabel(int index) {
            return types.get(index).getValueName();
        }

        public String getValue(int index) {
            return types.get(index).getValueId();
        }

        public Object translateValue(String value) {
            for (Value code : types) {
                if (code.getValueId().equals(value)) {
                    return code;
                }
            }

            throw new IllegalArgumentException("Can not find value " + value);
        }
    }

    private static class DomainStatusModel implements IPropertySelectionModel, Serializable {

        private DomainVOWrapper.Status[] statuses;

        public DomainStatusModel() {
            statuses = DomainVOWrapper.Status.values();
        }

        public int getOptionCount() {
            return statuses.length;
        }

        public Object getOption(int index) {
            return statuses[index];
        }

        public String getLabel(int index) {
            return statuses[index].getDisplayName();
        }

        public String getValue(int index) {
            return statuses[index].name();
        }

        public Object translateValue(String value) {
            return DomainVOWrapper.Status.valueOf(value);
        }
    }


}