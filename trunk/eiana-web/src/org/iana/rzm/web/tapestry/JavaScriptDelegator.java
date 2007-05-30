package org.iana.rzm.web.tapestry;

import org.apache.tapestry.*;

public class JavaScriptDelegator implements IRender {

    public void render(IMarkupWriter writer, IRequestCycle cycle) {
        IComponent border = cycle.getPage().getComponent("border");
        IAsset asset = border.getAsset("script");
        writer.begin("script");
        writer.attribute("language", "javascript");
        writer.attribute("src", asset.buildURL());
        writer.end();
    }
}
