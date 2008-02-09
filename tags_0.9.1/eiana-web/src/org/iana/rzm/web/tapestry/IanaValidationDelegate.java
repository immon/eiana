package org.iana.rzm.web.tapestry;

import org.apache.tapestry.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;

/**
 * Implementation of {@link org.apache.tapestry.valid.IValidationDelegate} which uses the correct
 * CSS class when rendering errors.
 *
 * @author Simon Raveh
 */

public class IanaValidationDelegate extends ValidationDelegate {

    private String errorFieldClass;
    private String deligateErrorFieldClass;

    public IanaValidationDelegate() {
        errorFieldClass = "errorMessageField";
    }

    public void writeLabelPrefix(IFormComponent component, IMarkupWriter writer, IRequestCycle cycle) {
        if (isInError(component)) {
            writer.begin("span");
            writer.attribute("class", "invalid-field");
        }
    }

    public void writeLabelSuffix(IFormComponent component, IMarkupWriter writer, IRequestCycle cycle) {
        if (isInError(component))
            writer.end();
    }

    public void writeAttributes(IMarkupWriter writer, IRequestCycle cycle,
                                IFormComponent component, IValidator validator) {
        if (isInError())
            if (deligateErrorFieldClass != null) {
                writer.attribute("class", deligateErrorFieldClass);
                deligateErrorFieldClass = null;
            } else {
                writer.attribute("class", "errorMessageField");
            }
    }

    public void writeSuffix(IMarkupWriter writer, IRequestCycle cycle, IFormComponent component,
                            IValidator validator) {
    }

    public void setDeligateErrorFieldClass(String style) {
        deligateErrorFieldClass = style;

    }

}

