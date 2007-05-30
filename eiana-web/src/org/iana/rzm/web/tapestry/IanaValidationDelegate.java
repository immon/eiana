package org.iana.rzm.web.tapestry;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidator;
import org.apache.tapestry.valid.ValidationDelegate;

/**
 * Implementation of {@link org.apache.tapestry.valid.IValidationDelegate} which uses the correct
 * CSS class when rendering errors.
 *
 * @author Simon Raveh
 */

public class IanaValidationDelegate extends ValidationDelegate {

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
            writer.attribute("class", "errorMessageField");
    }

    public void writeSuffix(IMarkupWriter writer, IRequestCycle cycle, IFormComponent component,
                            IValidator validator) {
    }

}

