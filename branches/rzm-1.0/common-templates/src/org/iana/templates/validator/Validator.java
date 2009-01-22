package org.iana.templates.validator;

import org.iana.templates.def.*;
import org.iana.templates.inst.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * It validates a <code>TemplateObjectInst</code> against a <code>TemplateObjectDef</code>.
 *
 * @author Jakub Laszkiewicz
 */
public class Validator implements TemplateObjectDefVisitor {
    Stack<TemplateObjectInst> instances;
    List<ValidationException> errors;

    public void validate(TemplateObjectInst toi, TemplateObjectDef tod) {
        instances = new Stack<TemplateObjectInst>();
        errors = new ArrayList<ValidationException>();

        doValidation(toi, tod);
        if (!errors.isEmpty()) throw new ValidationCompositeException(errors);
    }

    void doValidation(TemplateObjectInst toi, TemplateObjectDef tod) {
        if (toi == null) {
            requiredMissing(tod, true);
            return;
        }

        if (!toi.isInstanceOf(tod)) fatal(new UnexpectedElementException(toi.getLabel()));

        instances.push(toi);
        try {
            tod.accept(this);
        } finally {
            instances.pop();
        }
    }

    public void visitSectionDef(final SectionDef sd) {
        SectionInst si = (SectionInst) instances.peek();

        if (si.getModificator() == NoChange.getInstance()) return;

        Iterator sdi = sd.getElementDefs().iterator();
        Iterator svi = si.getInstances().iterator();
        while (sdi.hasNext()) {
            TemplateObjectDef tod = (TemplateObjectDef) sdi.next();
            if (!svi.hasNext()) {
                Validator.this.doValidation(null, tod);
            } else {
                TemplateObjectInst toi = (TemplateObjectInst) svi.next();
                for (; !toi.isInstanceOf(tod) && sdi.hasNext(); tod = (TemplateObjectDef) sdi.next()) {
                    doValidation(null, tod);
                }
                if (!toi.isInstanceOf(tod))
                    fatal(new UnexpectedElementException(toi.getLabel()));
                else
                    doValidation(toi, tod);
            }
        }
        while (svi.hasNext()) error(new UnexpectedElementException(((TemplateObjectInst) svi.next()).getLabel()));
    }

    public void visitFieldDef(final FieldDef fd) {
        FieldInst fi = (FieldInst) instances.peek();

        if (fi.getModificator() == NoChange.getInstance()) return;

        if (requiredMissing(fd, fi.isEmpty())) return;

        if (fd.getRegex() != null && !Pattern.matches(fd.getRegex(), fi.getValue()))
            error(new RegexFailedException(fi.getLabel(), fi.getValue(), fd.getRegex()));
    }

    public void visitListDef(ListDef ld) {
        ListInst li = (ListInst) instances.peek();

        if (requiredMissing(ld, li.isEmpty())) return;

        for (Object o : li.getList()) {
            ElementInst ei = (ElementInst) o;
            if (!ei.isRemoved()) doValidation(ei, ld.getElementDef());
        }
    }

    // helpers

    void fatal(ValidationException ve) {
        throw ve;
    }

    void error(ValidationException ve) {
        errors.add(ve);
    }

    boolean requiredMissing(TemplateObjectDef tod, boolean missing) {
        boolean ret = missing && tod.isRequired();
        if (ret) error(new RequiredElementMissingException(tod.getLabel()));
        return ret;
    }

}
