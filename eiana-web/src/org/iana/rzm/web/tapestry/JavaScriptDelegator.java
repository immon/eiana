package org.iana.rzm.web.tapestry;

import org.apache.tapestry.*;

public class JavaScriptDelegator implements IRender {

    public void render(IMarkupWriter writer, IRequestCycle cycle) {
        IComponent border = cycle.getPage().getComponent("border");
        writeScript(writer, border.getAsset("script"));
    }

    private void writeScript(IMarkupWriter writer, IAsset asset) {
        writer.begin("script");
        writer.attribute("language", "javascript");
        writer.attribute("src", asset.buildURL());
        writer.end();
    }
}
