package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

@ComponentClass
public abstract class DomainHeader extends BaseComponent {

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:country"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "tooltip", type = "man:Tooltip",
               bindings = {"tooltipContent=prop:tooltipContent", "xoffset=literal:30", "yoffset=literal:-30"})
    public abstract IComponent getToolTipComponent();

    @Parameter(required = true)
    public abstract String getDomainName();

    @Parameter(required = true)
    public abstract String getCountryName();

    public String getTooltipContent(){
        return getDomainName().toLowerCase().startsWith("xn--") ? "<span class='tooltip'>" + getDomainName() + "</span>" : "";
    }


    public String getDomain(){
        return getDomainName().toLowerCase().startsWith("xn--") ? gnu.inet.encoding.IDNA.toUnicode(getDomainName()) : getDomainName();
    }

    public String getCountry(){
        return "(" + getCountryName() + ")";
    }
}
