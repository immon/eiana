package org.iana.rzm.web.components.user;

import org.apache.commons.lang.*;
import org.apache.hivemind.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.components.*;

import java.text.*;

@ComponentClass(allowBody = false)
public abstract class RzmInsert extends Insert {

    @Parameter(name = "originalValue", required = false, defaultValue = "prop:value")
    public abstract String getOriginalValue();

    @Parameter(name = "modifiedStyle", required = false, defaultValue = "literal:modified")
    public abstract String getModifiedStyle();

    @Parameter(name = "deletedStyle", required = false, defaultValue = "literal:strikethrough")
    public abstract String getDeletedStyle();

    @Parameter
    public abstract Object getValue();

    @Parameter
    public abstract Format getFormat();

    @Parameter(name = "class")
    public abstract String getStyleClass();

    @Parameter
    public abstract boolean getRaw();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        if (cycle.isRewinding())
            return;

        Object value = getValue();

        boolean hasChange = false;
        boolean deleted = false;

        if (value == null){
            if(StringUtils.isNotBlank(getOriginalValue()) ){
                deleted = true;
                value = getOriginalValue();
            }else{
                return;
            }
        }

        if(!deleted){
            hasChange = valueHsChanged(getValue(), getOriginalValue() == null ? "" : getOriginalValue());
        }

        if(!hasChange){
            value = getOriginalValue();
        }

        String insert;
        Format format = getFormat();

        if (format == null) {
            insert = value.toString();
        } else {
            try {
                insert = format.format(value);
            }
            catch (Exception ex) {
                throw new ApplicationRuntimeException("unable to format " + value, ex);
            }
        }

        String styleClass = getStyleClass();

        if (styleClass != null) {
            writer.begin("span");
            writer.attribute("class", styleClass);
            renderInformalParameters(writer, cycle);
        }


        if (hasChange || deleted) {
            writer.begin("span");
            writer.attribute("class", deleted ? getDeletedStyle():getModifiedStyle());
            renderInformalParameters(writer, cycle);
        }

        writer.print(insert, getRaw());

        if (hasChange || deleted) {
            writer.end();
        }

        if (styleClass != null)
            writer.end(); // <span>
    }

    private boolean valueHsChanged(Object value, Object originalValue) {
        String values = value.toString();
        values = values.replace("\n", " ").replace("\r", " ");
        String orgs = originalValue.toString();
        orgs = orgs.replace("\n", " ").replace("\r", " ");        
        return !values.trim().equals(orgs.trim());
    }

}
