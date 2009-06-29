package org.iana.rzm.web.tapestry.components.domain;

import gnu.inet.encoding.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

public abstract class DomainHeader extends BaseComponent {

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:country"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "tooltip", type = "manlib:Tooltip",
               bindings = {"tooltipContent=prop:tooltipContent", "xoffset=literal:30", "yoffset=literal:-30"})
    public abstract IComponent getToolTipComponent();

    public abstract String getDomainName();
    public abstract String getCountryName();

    public String getTooltipContent(){
        return getDomainName().toLowerCase().startsWith("xn--") ? "<span class='tooltip'>" + getDomainName() + "</span>" : "";
    }


    public String getDomain(){
        return getDomainName().toLowerCase().startsWith("xn--") ? IDNA.toUnicode(getDomainName()) : getDomainName();
    }

    public String getCountry(){
        return "(" + getCountryName() + ")";
    }
}
