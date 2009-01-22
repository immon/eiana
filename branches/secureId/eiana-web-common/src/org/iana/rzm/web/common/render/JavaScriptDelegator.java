package org.iana.rzm.web.common.render;

import org.apache.tapestry.*;

public class JavaScriptDelegator implements IRender {

    public void render(IMarkupWriter writer, IRequestCycle cycle) {
        IComponent border = cycle.getPage().getComponent("border");
        writer.begin("link");
        writer.attribute("REL","SHORTCUT ICON");
        IAsset icon = border.getAsset("siteIcon");
        writer.attribute("HREF", icon.buildURL());
        writeScript(writer, border.getAsset("script"));
//        writeScript(writer, border.getAsset("prototypeScript"));
    }

    private void writeScript(IMarkupWriter writer, IAsset asset) {
        writer.begin("script");
        writer.attribute("language", "javascript");
        writer.attribute("src", asset.buildURL());
        writer.end();
    }
}
