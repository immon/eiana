package org.iana.templates;

import org.iana.templates.def.parser.TemplatesManager;
import org.iana.templates.inst.SectionInst;
import org.iana.templates.inst.parser.TemplateParseException;
import org.iana.templates.inst.parser.TemplateParser;
import org.iana.templates.inst.parser.TemplateParserImpl;
import org.iana.templates.validator.ValidationException;
import org.iana.templates.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplatesServiceBean implements TemplatesService {
    private TemplatesManager templatesManager;
    private TemplateParser templateParser;
    private Validator validator;


    public TemplatesServiceBean(TemplatesManager templatesManager) {
        this.templatesManager = templatesManager;
    }

    public SectionInst parseTemplate(String str) throws TemplatesServiceException {
        try {
            SectionInst si = getTemplateParser().toTemplate(str);
            getValidator().validate(si, templatesManager.getTemplate(si.getDefinition().getLabel()));
            return si;
        } catch (org.iana.templates.inst.parser.NonexistentTemplateException e) {
            throw new NonexistentTemplateException(e.getMessage(), e);
        } catch (org.iana.templates.inst.parser.RequiredElementMissingException e) {
            throw new RequiredElementMissingException(e.getMessage(), e);
        } catch (org.iana.templates.inst.parser.UnexpectedElementException e) {
            throw new UnexpectedElementException(e.getMessage(), e);
        } catch (org.iana.templates.inst.parser.UnexpectedTokenException e) {
            throw new UnexpetctedTokenException(e.getMessage(), e);
        } catch (TemplateParseException e) {
            throw new TemplatesServiceException("unexpected error", e);
        } catch (org.iana.templates.validator.RegexFailedException e) {
            throw new WrongSyntaxException(e.getMessage(), e);
        } catch (org.iana.templates.validator.RequiredElementMissingException e) {
            throw new RequiredElementMissingException(e.getMessage(), e);
        } catch (org.iana.templates.validator.UnexpectedElementException e) {
            throw new UnexpectedElementException(e.getMessage(), e);
        } catch (org.iana.templates.validator.ValidationCompositeException e) {
            List<TemplatesServiceException> exceptions = new ArrayList<TemplatesServiceException>();
            for (ValidationException ve : e.getExceptions())
                exceptions.add(convertException(ve));
            throw new TemplateServiceCompositeException(exceptions);
        }
    }

    private TemplateParser getTemplateParser() {
        if (templateParser == null)
            templateParser = new TemplateParserImpl(templatesManager.getTemplates());
        return templateParser;
    }

    private Validator getValidator() {
        if (validator == null) validator = new Validator();
        return validator;
    }

    private TemplatesServiceException convertException(ValidationException e) throws TemplatesServiceException {
        if (e instanceof org.iana.templates.validator.RegexFailedException)
            return new WrongSyntaxException(e.getMessage(), e);
        else if (e instanceof org.iana.templates.validator.RequiredElementMissingException)
            return new WrongSyntaxException(e.getMessage(), e);
        else if (e instanceof org.iana.templates.validator.UnexpectedElementException)
            return new WrongSyntaxException(e.getMessage(), e);
        throw new TemplatesServiceException("unexpected error", e);
    }
}
