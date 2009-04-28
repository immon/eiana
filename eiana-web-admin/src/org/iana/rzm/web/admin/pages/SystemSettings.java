package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.admin.model.*;

public abstract class SystemSettings extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "SystemSettings";

    @Component(id = "email", type = "SelectionLink", bindings = {"spanStyle=prop:emailSpanStyle",
            "linkStyle=prop:emailStyle", "linkText=literal:Email Settings", "listener=listener:showEmailSettings", "useDivStyle=literal:true"})
    public abstract IComponent getSystemUsersComponent();

    @Component(id = "verisign", type = "SelectionLink", bindings = {"spanStyle=prop:verisignSpanStyle",
            "linkStyle=prop:verisignStyle", "linkText=literal:Verisign Settings", "listener=listener:showVerisignSettings", "useDivStyle=literal:true"})
    public abstract IComponent getVerisignComponent();

    @Component(id = "doc", type = "SelectionLink", bindings = {"spanStyle=prop:docSpanStyle",
            "linkStyle=prop:docStyle", "linkText=literal:DoC Settings", "listener=listener:showDocSettings", "useDivStyle=literal:true"})
    public abstract IComponent getDocComponent();

    @Component(id = "ianapgp", type = "SelectionLink", bindings = {"spanStyle=prop:pgpSpanStyle",
            "linkStyle=prop:pgpStyle", "linkText=literal:IANA PGP Settings", "listener=listener:showPgpSettings", "useDivStyle=literal:true"})
    public abstract IComponent getAdminUsersComponent();

    @Component(id = "smtp", type = "SmtpSettings", bindings = {"smtpSettings=prop:smtpSettings",
            "listener=listener:editSmtpSettings"})
    public abstract IComponent getSmtpSettingsComponent();

    @Component(id = "pop", type = "Pop3Settings", bindings = {"pop3Settings=prop:pop3Settings",
            "listener=listener:editPopSettings"})
    public abstract IComponent getPop3SettingsComponent();

    @Component(id = "verisignConfig", type = "VerisignDoCSettingsEditor", bindings = {"config=prop:versignConfig", "title=literal:Verisign Settings"})
    public abstract IComponent getVerisignSettingsComponent();

    @Component(id = "docConfig", type = "VerisignDoCSettingsEditor", bindings = {"config=prop:docConfig", "title=literal:DoC Settings"})
    public abstract IComponent getDoCSettingsComponent();

    @Component(id = "pgpConfig", type = "IANAPgpEditor", bindings = {"config=prop:pgpConfig"})
    public abstract IComponent getPgpConfigComponent();

    @Component(id="smtpEditor", type="SmtpSettingsEditor", bindings = {"config=prop:smtpSettings", "back=listener:showEmailSettings"})
    public abstract IComponent getSmtpEditorComponent();

    @Component(id="popEditor", type="POP3SettingsEditor", bindings = {"config=prop:pop3Settings", "back=listener:showEmailSettings"})
    public abstract IComponent getPopEditorComponent();


    private static final String[] BLOCKS = {
            "EMAIL",
            "IANAPGP",
            "VERISIGN",
            "DoC",
            "SMTPEdit",
            "POPEdit"
    };

    @Persist
    public abstract String getSelectedBlockId();
    public abstract void setSelectedBlockId(String id);

    @Persist("client")
    public abstract SmtpConfigVOWrapper getSmtpSettings();
    public abstract void setSmtpSettings(SmtpConfigVOWrapper config);

    @Persist("client")
    public abstract POP3ConfigVOWrapper getPop3Settings();
    public abstract void setPop3Settings(POP3ConfigVOWrapper config);

    public void pageBeginRender(PageEvent event){

        if(getSmtpSettings() == null){
            setSmtpSettings(getAdminServices().getSmtpConfigSettings());
        }

        if(getPop3Settings() == null){
            setPop3Settings(getAdminServices().getPop3ConfigSettings());            
        }
    }

    public void editSmtpSettings(){
        doClick(BLOCKS[4]);
    }

    public void editPopSettings(){
        doClick(BLOCKS[5]);
    }

    public VersignConfigVOWrpper getVersignConfig() {
        return getAdminServices().getVerisignConfigSettings();
    }

    public USDoCConfigVOWrapper getDocConfig() {
        return getAdminServices().getDoCConfigSettings();
    }

    public PgpConfigVOWrapper getPgpConfig() {
        return getAdminServices().getPgpConfigSettings();
    }


    public Block getSelectedBlock() {
        String selectedId = blockId();
        return (Block) getComponent(selectedId);
    }

    public boolean isEmailBlockSelected() {
        String blockId = blockId();
        return blockId.equals(BLOCKS[0]) || blockId.equals(BLOCKS[4]) || blockId.equals(BLOCKS[5]);
    }

    public boolean isPgpBlockSelected() {
        String blockId = blockId();
        return blockId.equals(BLOCKS[1]);
    }

    public boolean isVerisignBlockSelected() {
        String blockId = blockId();
        return blockId.equals(BLOCKS[2]);
    }

    public boolean isDocBlockSelected() {
        String blockId = blockId();
        return blockId.equals(BLOCKS[3]);
    }

    private String blockId() {
        String blockId = getSelectedBlockId();
        if (blockId == null) {
            blockId = BLOCKS[0];
        }
        return blockId;
    }

    public void showEmailSettings() {
        doClick(BLOCKS[0]);
    }

    public void showPgpSettings() {
        doClick(BLOCKS[1]);
    }

    public void showVerisignSettings() {
        doClick(BLOCKS[2]);
    }

    public void showDocSettings() {
        doClick(BLOCKS[3]);
    }

    public void doClick(String selectedId) {
        setSelectedBlockId(selectedId);
    }

    public String getEmailSpanStyle() {
        if (isEmailBlockSelected()) {
            return "leftBlack";
        }

        return "leftGrey";
    }

    public String getEmailStyle() {
        if (isEmailBlockSelected()) {
            return "buttonBlack";
        }

        return "buttonGrey";
    }

    public String getPgpSpanStyle() {
        if (isPgpBlockSelected()) {
            return "leftBlack";
        }
        return "leftGrey";
    }

    public String getPgpStyle() {
        if (isPgpBlockSelected()) {
            return "buttonBlack";
        }
        return "buttonGrey";
    }

    public String getVerisignSpanStyle() {
        if (isVerisignBlockSelected()) {
            return "leftBlack";
        }

        return "leftGrey";
    }

    public String getVerisignStyle() {
        if (isVerisignBlockSelected()) {
            return "buttonBlack";
        }

        return "buttonGrey";
    }

    public String getDocSpanStyle() {
        if (isDocBlockSelected()) {
            return "leftBlack";
        }

        return "leftGrey";
    }

    public String getDocStyle() {
        if (isDocBlockSelected()) {
            return "buttonBlack";
        }

        return "buttonGrey";
    }


}
