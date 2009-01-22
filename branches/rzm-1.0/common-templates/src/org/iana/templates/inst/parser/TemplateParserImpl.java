package org.iana.templates.inst.parser;

import org.iana.templates.def.*;
import org.iana.templates.inst.*;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplateParserImpl implements TemplateParser {
    private Map<String, SectionDef> templateDefs;

    private static final String KEYWORD_NO_CHANGE = "no change";
    private static final String KEYWORD_REPLACE = "replace";
    private static final String KEYWORD_REMOVE = "remove";

    public TemplateParserImpl(Map<String, SectionDef> templateDefs) {
        this.templateDefs = templateDefs;
    }

    private String token;
    private StringTokenizer strTok;

    /**
     * Tokenizes str and sets first token.
     *
     * @param str string to tokenize.
     */
    private void tokenize(String str) {
        strTok = new StringTokenizer(str, "\n[]", true);
        nextToken();
    }

    private String getToken() {
        return token;
    }

    /**
     * @return current token and gets next token.
     */
    private String nextToken() {
        String prevToken = token;
        if (strTok.hasMoreTokens()) {
            token = strTok.nextToken().trim();
            while (token.length() == 0 && strTok.hasMoreTokens())
                token = strTok.nextToken().trim();
        } else {
            token = null;
        }
        return prevToken;
    }

    public SectionInst toTemplate(String str) throws TemplateParseException {
        SectionInst template;
        tokenize(str);
        if (getToken() == null)
            throw new RequiredElementMissingException("template name");
        SectionDef templateDef = templateDefs.get(getToken());
        if (templateDef == null)
            throw new NonexistentTemplateException(getToken());
        template = new SectionInst(templateDef, nextToken());
        template.setInstances(toElements(template));
        if (getToken() != null)
            throw new UnexpectedTokenException(getToken());
        return template;
    }

    private List<TemplateObjectInst> toElements(SectionInst sectionInst) throws TemplateParseException {
        Iterator defs = ((SectionDef) sectionInst.getDefinition()).getElementDefs().iterator();
        TemplateObjectDef tod;
        List<TemplateObjectInst> list = new ArrayList<TemplateObjectInst>();
        if (defs.hasNext()) {
            tod = (TemplateObjectDef) defs.next();
            boolean go = defs.hasNext();
            while (go && getToken() != null) {
                TemplateObjectInst toi = toInstance(tod);
                if (toi == null) {
                    if (go = defs.hasNext())
                        tod = (TemplateObjectDef) defs.next();
                } else list.add(toi);
            }
        }
        return list;
    }

    private TemplateObjectInst toInstance(TemplateObjectDef tod) throws TemplateParseException {
        if (tod instanceof FieldDef) {
            FieldDef fieldDef = (FieldDef) tod;
            String label = getToken();
            if (label.endsWith(":")) label = label.substring(0, label.length() - 1);
            if (fieldDef.getLabel().equals(label)) {
                if (!getToken().endsWith(":"))
                    throw new RequiredElementMissingException("field colon: " + label);
                return toField(fieldDef);
            }
        } else if (tod instanceof SectionDef) {
            SectionDef sectionDef = (SectionDef) tod;
            if (sectionDef.getLabel().equals(getToken()))
                return toSection(sectionDef);
        } else if (tod instanceof ListDef) {
            ListDef listDef = (ListDef) tod;
            String label = getToken();
            if (label.endsWith(":")) label = label.substring(0, label.length() - 1);
            if (listDef.getElementDef().getLabel().equals(label)) {
                if ((listDef.getElementDef() instanceof FieldDef) && !getToken().endsWith(":"))
                    throw new RequiredElementMissingException("field colon: " + label);
                return toList((ListDef) tod);
            }
        }
        return null;
    }

    private ElementInst toElementInst(TemplateObjectDef tod) throws TemplateParseException {
        TemplateObjectInst toi = toInstance(tod);
        if (toi == null) return null;
        if (!(toi instanceof ElementInst))
            throw new UnexpectedElementException(toi.getLabel());
        return (ElementInst) toi;
    }

    private ListInst toList(ListDef listDef) throws TemplateParseException {
        ElementDef elementDef = listDef.getElementDef();
        ElementInst elementInst;
        List<ElementInst> list = new ArrayList<ElementInst>();
        do {
            elementInst = toElementInst(elementDef);
            if (elementInst != null) list.add(elementInst);
        } while (elementInst != null);
        return list.isEmpty() ? null : new ListInst(listDef, list);
    }

    private FieldInst toField(FieldDef fieldDef) throws TemplateParseException {
        FieldInst fieldInst = new FieldInst(fieldDef, nextToken());
        if (getToken() == null)
            throw new RequiredElementMissingException("field value: " + fieldInst.getLabel());
        if (!"[".equals(nextToken()))
            throw new RequiredElementMissingException("field value left square bracket: " + fieldDef.getLabel());
        if (KEYWORD_NO_CHANGE.equals(getToken())) {
            fieldInst.setModificator(NoChange.getInstance());
            nextToken();
        } else if (!"]".equals(getToken())) {
            fieldInst.setValue(nextToken());
        }
        if (!"]".equals(nextToken()))
            throw new RequiredElementMissingException("field value right square bracket: " + fieldDef.getLabel());
        return fieldInst;
    }

    private SectionInst toSection(SectionDef sectionDef) throws TemplateParseException {
        SectionInst sectionInst = new SectionInst(sectionDef, nextToken());
        if (getToken() == null)
            throw new RequiredElementMissingException("body of section: " + sectionInst.getLabel());
        if ("[".equals(getToken())) {
            nextToken();
            if ("]".equals(getToken())) {
                nextToken();
                sectionInst.setInstances(toElements(sectionInst));
            } else if (KEYWORD_NO_CHANGE.equals(getToken())) {
                sectionInst.setModificator(NoChange.getInstance());
                nextToken();
                if (!"]".equals(nextToken()))
                    throw new RequiredElementMissingException("section right square bracket: " + sectionInst.getLabel());
            } else if (getToken().startsWith(KEYWORD_REMOVE)) {
                sectionInst.setModificator(new Remove(getReplaceObjectName(nextToken(), sectionInst.getLabel())));
                if (!"]".equals(nextToken()))
                    throw new RequiredElementMissingException("section right square bracket: " + sectionInst.getLabel());
            } else if (getToken().startsWith(KEYWORD_REPLACE)) {
                sectionInst.setModificator(new Replace(getReplaceObjectName(nextToken(), sectionInst.getLabel())));
                if (!"]".equals(nextToken()))
                    throw new RequiredElementMissingException("section right square bracket: " + sectionInst.getLabel());
                sectionInst.setInstances(toElements(sectionInst));
            } else throw new UnexpectedTokenException("unknown section modificator: " + getToken());
        } else sectionInst.setInstances(toElements(sectionInst));
        return sectionInst;
    }

    private String getReplaceObjectName(String s, String sectionLabel) throws TemplateParseException {
        StringTokenizer st = new StringTokenizer(s);
        if (st.hasMoreTokens()) {
            st.nextToken();
            if (st.hasMoreTokens())
                return st.nextToken();
            else
                throw new RequiredElementMissingException("section replaced object name: " + sectionLabel);
        }
        return null;
    }
}
