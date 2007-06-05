package org.iana.templates.test;

import org.iana.templates.*;
import org.iana.templates.def.parser.TemplatesManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class TemplateParserFailureTest {
    private static final String TEMPLATE_MISSING_FIELD_FILE_NAME = "template-missing-field.txt.asc";
    private static final String TEMPLATE_MISSING_NAME_FILE_NAME = "template-missing-name.txt.asc";
    private static final String TEMPLATE_MISSING_SECTION_FILE_NAME = "template-missing-section.txt.asc";
    private static final String TEMPLATE_MISSING_SUBSECTION_FILE_NAME = "template-missing-subsection.txt.asc";
    private static final String TEMPLATE_MISSING_VALUE_FILE_NAME = "template-missing-value.txt.asc";
    private static final String TEMPLATE_UNEXPECTED_FIELD_FILE_NAME = "template-unexpected-field.txt.asc";
    private static final String TEMPLATE_UNEXPECTED_SECTION_FILE_NAME = "template-unexpected-section.txt.asc";
    private static final String TEMPLATE_UNEXPECTED_SUBSECTION_FILE_NAME = "template-unexpected-subsection.txt.asc";
    private static final String TEMPLATE_UNEXPECTED_VALUE_FILE_NAME = "template-unexpected-value.txt.asc";
    private static final String TEMPLATE_BROKEN_FIELD_MODIFIER_FILE_NAME = "template-broken-field-modifier.txt.asc";
    private static final String TEMPLATE_BROKEN_SECTION_MODIFIER_FILE_NAME = "template-broken-section-modifier.txt.asc";
    private static final String TEMPLATE_UNKNOWN_MODIFIER_FILE_NAME = "template-unknown-modifier.txt.asc";

    private TemplatesService templatesService;

    @BeforeClass
    public void init() throws Exception {
        TemplatesManager templatesManager = new TemplatesManager(TemplateParserTestUtil.TEMPLATE_DEF_FILE_NAME);
        templatesService = new TemplatesServiceBean(templatesManager);
    }

    private static final String MISSING_FIELD_MESSAGE = "Required element missing: [Domain Name]";

    @Test(expectedExceptions = TemplateServiceCompositeException.class)
    public void testMissingField() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_MISSING_FIELD_FILE_NAME));
        } catch (TemplateServiceCompositeException e) {
            assert e.getExceptions().size() == 1 : "unexpected number of subexceptions: " + e.getExceptions().size();
            assert e.getExceptions().iterator().next() instanceof WrongSyntaxException :
                    "unexpected subexception: " + e.getExceptions().iterator().next().getClass();
            assert MISSING_FIELD_MESSAGE.equals(e.getExceptions().iterator().next().getMessage()) :
                    "unexpected exception message: " + e.getExceptions().iterator().next().getMessage();
            throw e;
        }
    }

    private static final String MISSING_NAME_MESSAGE = "Template: [Domain Name:] does not exist";

    @Test(expectedExceptions = NonexistentTemplateException.class)
    public void testMissingName() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_MISSING_NAME_FILE_NAME));
        } catch (NonexistentTemplateException e) {
            assert MISSING_NAME_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String MISSING_SECTION_MESSAGE = "Required element missing: [Supporting Organization]";

    @Test(expectedExceptions = TemplateServiceCompositeException.class)
    public void testMissingSection() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_MISSING_SECTION_FILE_NAME));
        } catch (TemplateServiceCompositeException e) {
            assert e.getExceptions().size() == 1 : "unexpected number of subexceptions: " + e.getExceptions().size();
            assert e.getExceptions().iterator().next() instanceof WrongSyntaxException :
                    "unexpected subexception: " + e.getExceptions().iterator().next().getClass();
            assert MISSING_SECTION_MESSAGE.equals(e.getExceptions().iterator().next().getMessage()) :
                    "unexpected exception message: " + e.getExceptions().iterator().next().getMessage();
            throw e;
        }
    }

    private static final String MISSING_SUBSECTION_MESSAGE = "Required element missing: [Address]";

    @Test(expectedExceptions = TemplateServiceCompositeException.class)
    public void testMissingSubsection() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_MISSING_SUBSECTION_FILE_NAME));
        } catch (TemplateServiceCompositeException e) {
            assert e.getExceptions().size() == 1 : "unexpected number of subexceptions: " + e.getExceptions().size();
            assert e.getExceptions().iterator().next() instanceof WrongSyntaxException :
                    "unexpected subexception: " + e.getExceptions().iterator().next().getClass();
            assert MISSING_SUBSECTION_MESSAGE.equals(e.getExceptions().iterator().next().getMessage()) :
                    "unexpected exception message: " + e.getExceptions().iterator().next().getMessage();
            throw e;
        }
    }

    private static final String MISSING_VALUE_MESSAGE = "Required element missing: [field value left square bracket: Fax Number]";

    @Test(expectedExceptions = RequiredElementMissingException.class)
    public void testMissingValue() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_MISSING_VALUE_FILE_NAME));
        } catch (RequiredElementMissingException e) {
            assert MISSING_VALUE_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String UNEXPECTED_FIELD_MESSAGE = "Unexpected token encountered: [Unexpected Field:]";

    @Test(expectedExceptions = UnexpetctedTokenException.class)
    public void testUnexpectedField() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_UNEXPECTED_FIELD_FILE_NAME));
        } catch (UnexpetctedTokenException e) {
            assert UNEXPECTED_FIELD_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String UNEXPECTED_SECTION_MESSAGE = "Unexpected token encountered: [Unexpected Section]";

    @Test(expectedExceptions = UnexpetctedTokenException.class)
    public void testUnexpectedSection() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_UNEXPECTED_SECTION_FILE_NAME));
        } catch (TemplatesServiceException e) {
            assert UNEXPECTED_SECTION_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String UNEXPECTED_SUBSECTION_MESSAGE = "Unexpected token encountered: [Unexpected Subsection]";

    @Test(expectedExceptions = UnexpetctedTokenException.class)
    public void testUnexpectedSubsection() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_UNEXPECTED_SUBSECTION_FILE_NAME));
        } catch (UnexpetctedTokenException e) {
            assert UNEXPECTED_SUBSECTION_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String UNEXPECTED_VALUE_MESSAGE = "Unexpected token encountered: [Unexpected:]";

    @Test(expectedExceptions = UnexpetctedTokenException.class)
    public void testUnexpectedValue() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_UNEXPECTED_VALUE_FILE_NAME));
        } catch (UnexpetctedTokenException e) {
            assert UNEXPECTED_VALUE_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String BROKEN_FIELD_MODIFIER_MESSAGE = "Required element missing: [field value right square bracket: Public Email Address]";

    @Test(expectedExceptions = RequiredElementMissingException.class)
    public void testBrokenFieldModifier() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_BROKEN_FIELD_MODIFIER_FILE_NAME));
        } catch (TemplatesServiceException e) {
            assert BROKEN_FIELD_MODIFIER_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String BROKEN_SECTION_MODIFIER_MESSAGE = "Required element missing: [section right square bracket: Name Server]";

    @Test(expectedExceptions = RequiredElementMissingException.class)
    public void testBrokenSectionModifier() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_BROKEN_SECTION_MODIFIER_FILE_NAME));
        } catch (RequiredElementMissingException e) {
            assert BROKEN_SECTION_MODIFIER_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String UNKNOWN_MODIFIER_MESSAGE = "Unexpected token encountered: [unknown section modificator: unknown ns-ext.vix.com]";

    @Test(expectedExceptions = UnexpetctedTokenException.class)
    public void testUnknownModifier() throws Exception {
        try {
            templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_UNKNOWN_MODIFIER_FILE_NAME));
        } catch (UnexpetctedTokenException e) {
            assert UNKNOWN_MODIFIER_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }
}
