package org.iana.rzm.mail.test;

import org.iana.rzm.mail.parser.*;
import org.iana.templates.TemplatesService;
import org.iana.templates.TemplatesServiceBean;
import org.iana.templates.def.parser.TemplatesManager;
import org.iana.templates.def.parser.TemplatesManagerException;
import org.iana.templates.inst.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class MailParserImplTest {
    private static final Long CONFIRMATION_VALID_TRANSACTION_ID = 123L;
    private static final String CONFIRMATION_VALID_STATE_NAME = "PENDING_CONTACT_CONFIRMATION";
    private static final String CONFIRMATION_SUBJECT_TOKEN = "[RZM]";
    private static final String CONFIRMATION_VALID_DOMAIN_NAME = "confdomainname";
    private static final String CONFIRMATION_TOKEN = "1a2b3e4ff5a";
    private static final String CONFIRMATION_VALID_SUBJECT = "Re: " +
            CONFIRMATION_VALID_TRANSACTION_ID + " | " +
            CONFIRMATION_VALID_STATE_NAME + " | " +
            CONFIRMATION_SUBJECT_TOKEN + " | " +
            CONFIRMATION_VALID_DOMAIN_NAME + " | " +
            CONFIRMATION_TOKEN;
    private static final String CONFIRMATION_CONTENT_ACCEPT =
            "I ACCEPT\n" +
                    "> declaration declaration declaration declaration\n" +
                    "> declaration declaration declaration declaration\n" +
                    "> declaration declaration declaration declaration\n" +
                    "> declaration declaration declaration declaration\n";
    private static final String CONFIRMATION_CONTENT_DECLINE =
            "I DECLINE\r" +
                    "> declaration declaration declaration declaration\r" +
                    "> declaration declaration declaration declaration\r" +
                    "> declaration declaration declaration declaration\r" +
                    "> declaration declaration declaration declaration\r";
    private static final String CONFIRMATION_CONTENT_INVALID_1 =
            "> declaration declaration declaration declaration\n" +
                    "> declaration declaration declaration declaration\n" +
                    "> declaration declaration declaration declaration\n" +
                    "> declaration declaration declaration declaration\n";
    private static final String CONFIRMATION_CONTENT_INVALID_2 =
            "I ACCEPT\tI DECLINE\r\n" +
                    "> declaration declaration declaration declaration\r\n" +
                    "> declaration declaration declaration declaration\r\n" +
                    "> declaration declaration declaration declaration\r\n" +
                    "> declaration declaration declaration declaration\r\n";

    private MailParser mailParser;

    @BeforeClass
    public void init() throws TemplatesManagerException {
        TemplatesManager templatesManager = new TemplatesManager("mail-templates.xml");
        TemplatesService templatesService = new TemplatesServiceBean(templatesManager);
        mailParser = new MailParserImpl(templatesService);
    }

    @Test
    public void testParseConfirmationMailAccept() throws MailParserException {
        MailData result = mailParser.parse(CONFIRMATION_VALID_SUBJECT, CONFIRMATION_CONTENT_ACCEPT);
        assert result instanceof ConfirmationMailData;
        ConfirmationMailData cmd = (ConfirmationMailData) result;
        assert CONFIRMATION_VALID_TRANSACTION_ID.equals(cmd.getTransactionId());
        assert CONFIRMATION_VALID_STATE_NAME.equals(cmd.getStateName());
        assert CONFIRMATION_VALID_DOMAIN_NAME.equals(cmd.getDomainName());
        assert cmd.isAccepted();
    }

    @Test
    public void testParseConfirmationMailDecline() throws MailParserException {
        MailData result = mailParser.parse(CONFIRMATION_VALID_SUBJECT, CONFIRMATION_CONTENT_DECLINE);
        assert result instanceof ConfirmationMailData;
        ConfirmationMailData cmd = (ConfirmationMailData) result;
        assert CONFIRMATION_VALID_TRANSACTION_ID.equals(cmd.getTransactionId());
        assert CONFIRMATION_VALID_STATE_NAME.equals(cmd.getStateName());
        assert CONFIRMATION_VALID_DOMAIN_NAME.equals(cmd.getDomainName());
        assert !cmd.isAccepted();
    }

    private static final String CONFIRMATION_MAIL_INVALID_MESSAGE_1 = "both accept and decline are missing";

    @Test(expectedExceptions = MailParserException.class)
    public void testParseConfirmationMailInvalid1() throws MailParserException {
        try {
            mailParser.parse(CONFIRMATION_VALID_SUBJECT, CONFIRMATION_CONTENT_INVALID_1);
        } catch (MailParserException e) {
            assert CONFIRMATION_MAIL_INVALID_MESSAGE_1.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String CONFIRMATION_MAIL_INVALID_MESSAGE_2 = "both accept and decline are present";

    @Test(expectedExceptions = MailParserException.class)
    public void testParseConfirmationMailInvalid2() throws MailParserException {
        try {
            mailParser.parse(CONFIRMATION_VALID_SUBJECT, CONFIRMATION_CONTENT_INVALID_2);
        } catch (MailParserException e) {
            assert CONFIRMATION_MAIL_INVALID_MESSAGE_2.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private static final String TEMPLATE_VALID_FILE_NAME = "template.txt.asc";
    private static final String TEMPLATE_INVALID_FILE_NAME = "template-invalid.txt.asc";

    private static final String TEMPLATE_DOMAIN_NAME_FIELD_NAME = "name";
    private static final String TEMPLATE_DOMAIN_SUPP_ORG_SECTION_NAME = "supportingOrg";
    private static final String TEMPLATE_DOMAIN_ADMIN_CONTACT_SECTION_NAME = "adminContact";
    private static final String TEMPLATE_DOMAIN_TECH_CONTACT_SECTION_NAME = "techContact";
    private static final String TEMPLATE_DOMAIN_NAME_SERVERS_SECTION_NAME = "nameServer";
    private static final String TEMPLATE_DOMAIN_REGISTRY_URL_FIELD_NAME = "registryUrl";
    private static final String TEMPLATE_DOMAIN_WHOIS_SERVER_FIELD_NAME = "whoisServer";
    private static final String TEMPLATE_CONTACT_NAME_FIELD_NAME = "name";
    private static final String TEMPLATE_CONTACT_ORGANIZATION_FIELD_NAME = "organization";
    private static final String TEMPLATE_CONTACT_JOB_TITLE_FIELD_NAME = "jobTitle";
    private static final String TEMPLATE_CONTACT_ADDRESS_SECTION_NAME = "address";
    private static final String TEMPLATE_CONTACT_ADDRESS_TEXT_FIELD_NAME = "textAddress";
    private static final String TEMPLATE_CONTACT_ADDRESS_CC_FIELD_NAME = "countryCode";
    private static final String TEMPLATE_CONTACT_PHONE_FIELD_NAME = "phoneNumber";
    private static final String TEMPLATE_CONTACT_ALT_PHONE_FIELD_NAME = "altPhoneNumber";
    private static final String TEMPLATE_CONTACT_FAX_FIELD_NAME = "faxNumber";
    private static final String TEMPLATE_CONTACT_ALT_FAX_FIELD_NAME = "altFaxNumber";
    private static final String TEMPLATE_CONTACT_PUBLIC_EMAIL_FIELD_NAME = "publicEmail";
    private static final String TEMPLATE_CONTACT_PRIVATE_EMAIL_FIELD_NAME = "privateEmail";
    private static final String TEMPLATE_HOST_NAME_FIELD_NAME = "name";
    private static final String TEMPLATE_HOST_IPADDRESS_FIELD_NAME = "ipAddress";

    private static final String TEMPLATE_DOMAIN_NAME_VALUE = "SH";
    private static final String TEMPLATE_TECHC_NAME_VALUE = "Administrator";
    private static final String TEMPLATE_TECHC_ORGANIZATION_VALUE = "ICB Plc";
    private static final String TEMPLATE_TECHC_JOB_TITLE_VALUE = "Specialist";
    private static final String TEMPLATE_TECHC_TEXT_ADDRESS_VALUE = "11 Queens Road";
    private static final String TEMPLATE_TECHC_COUNTRY_CODE_VALUE = "UK";
    private static final String TEMPLATE_NAME_SERVER_1_REPLACE_NAME = "ns-ext.vix.com";
    private static final String TEMPLATE_NAME_SERVER_1_HOST_NAME = "ns-ext.vix.com";
    private static final String TEMPLATE_NAME_SERVER_1_IP_ADDRESS_1 = "204.152.184.64";
    private static final String TEMPLATE_NAME_SERVER_1_IP_ADDRESS_2 = "204.152.184.65";
    private static final String TEMPLATE_NAME_SERVER_2_HOST_NAME = "ns-1.vix.com";
    private static final String TEMPLATE_NAME_SERVER_2_IP_ADDRESS = "204.152.184.65";
    private static final String TEMPLATE_NAME_SERVER_3_REMOVE_NAME = "ns-2.vix.com";
    private static final String TEMPLATE_DOMAIN_REGISTRY_URL = "registry.url";
    private static final String TEMPLATE_DOMAIN_WHOIS_SERVER = "whois.url";

    private static final String TEMPLATE_VALID_SUBJECT = "Domain Modification Transaction (Unified Workflow)";

    @Test
    public void testParseTemplateMail() throws Exception {
        MailData data = mailParser.parse(TEMPLATE_VALID_SUBJECT, loadFromFile(TEMPLATE_VALID_FILE_NAME));
        assert data instanceof TemplateMailData;
        TemplateMailData templateData = (TemplateMailData) data;
        assert templateData.getTemplate() != null;
        SectionInst si = templateData.getTemplate();

        // domain name
        assert TEMPLATE_DOMAIN_NAME_VALUE.equals(si.getFieldInstance(TEMPLATE_DOMAIN_NAME_FIELD_NAME).getValue());

        // domain supporting organization section
        assert NoChange.getInstance().equals(si.getSectionInstance(TEMPLATE_DOMAIN_SUPP_ORG_SECTION_NAME).getModificator());

        // domain admin contact section - no change
        SectionInst domainAdminContact = si.getSectionInstance(TEMPLATE_DOMAIN_ADMIN_CONTACT_SECTION_NAME);
        assert NoChange.getInstance().equals(domainAdminContact.getModificator());

        // domain tech contact section
        SectionInst domainTechC = si.getSectionInstance(TEMPLATE_DOMAIN_TECH_CONTACT_SECTION_NAME);
        assert TEMPLATE_TECHC_NAME_VALUE.equals(domainTechC.getFieldInstance(TEMPLATE_CONTACT_NAME_FIELD_NAME).getValue());
        assert TEMPLATE_TECHC_ORGANIZATION_VALUE.equals(domainTechC.getFieldInstance(TEMPLATE_CONTACT_ORGANIZATION_FIELD_NAME).getValue());
        assert TEMPLATE_TECHC_JOB_TITLE_VALUE.equals(domainTechC.getFieldInstance(TEMPLATE_CONTACT_JOB_TITLE_FIELD_NAME).getValue());
        SectionInst domainTechCAddress = domainTechC.getSectionInstance(TEMPLATE_CONTACT_ADDRESS_SECTION_NAME);
        assert TEMPLATE_TECHC_TEXT_ADDRESS_VALUE.equals(domainTechCAddress.getFieldInstance(TEMPLATE_CONTACT_ADDRESS_TEXT_FIELD_NAME).getValue());
        assert TEMPLATE_TECHC_COUNTRY_CODE_VALUE.equals(domainTechCAddress.getFieldInstance(TEMPLATE_CONTACT_ADDRESS_CC_FIELD_NAME).getValue());
        FieldInst domainTechCPhone = domainTechC.getFieldInstance(TEMPLATE_CONTACT_PHONE_FIELD_NAME);
        assert NoChange.getInstance().equals(domainTechCPhone.getModificator());
        FieldInst domainTechCAltPhone = domainTechC.getFieldInstance(TEMPLATE_CONTACT_ALT_PHONE_FIELD_NAME);
        assert NoChange.getInstance().equals(domainTechCAltPhone.getModificator());
        FieldInst domainTechCFax = domainTechC.getFieldInstance(TEMPLATE_CONTACT_FAX_FIELD_NAME);
        assert NoChange.getInstance().equals(domainTechCFax.getModificator());
        FieldInst domainTechCAltFax = domainTechC.getFieldInstance(TEMPLATE_CONTACT_ALT_FAX_FIELD_NAME);
        assert NoChange.getInstance().equals(domainTechCAltFax.getModificator());
        FieldInst domainTechCPublicEmail = domainTechC.getFieldInstance(TEMPLATE_CONTACT_PUBLIC_EMAIL_FIELD_NAME);
        assert NoChange.getInstance().equals(domainTechCPublicEmail.getModificator());
        FieldInst domainTechCPrivateEmail = domainTechC.getFieldInstance(TEMPLATE_CONTACT_PRIVATE_EMAIL_FIELD_NAME);
        assert NoChange.getInstance().equals(domainTechCPrivateEmail.getModificator());

        // domain name server section
        ListInst domainNameServerList = si.getListInstance(TEMPLATE_DOMAIN_NAME_SERVERS_SECTION_NAME);
        assert domainNameServerList.getList().size() == 3;
        Iterator<ElementInst> domainNameServerIterator = domainNameServerList.getList().iterator();
        ElementInst inst = domainNameServerIterator.next();
        assert inst instanceof SectionInst;
        SectionInst domainNameServer = (SectionInst) inst;
        Modificator mod = domainNameServer.getModificator();
        assert mod != null;
        assert mod instanceof Replace;
        Replace rep = (Replace) mod;
        assert TEMPLATE_NAME_SERVER_1_REPLACE_NAME.equals(rep.getOldElementName());
        assert TEMPLATE_NAME_SERVER_1_HOST_NAME.equals(domainNameServer.getFieldInstance(TEMPLATE_HOST_NAME_FIELD_NAME).getValue());
        ListInst nsIpAddrList = domainNameServer.getListInstance(TEMPLATE_HOST_IPADDRESS_FIELD_NAME);
        assert nsIpAddrList.getList().size() == 2;
        Iterator<ElementInst> nsIpAddrIterator = nsIpAddrList.getList().iterator();
        inst = nsIpAddrIterator.next();
        assert inst instanceof FieldInst;
        FieldInst nsIpAddr = (FieldInst) inst;
        assert TEMPLATE_NAME_SERVER_1_IP_ADDRESS_1.equals(nsIpAddr.getValue());
        inst = nsIpAddrIterator.next();
        assert inst instanceof FieldInst;
        nsIpAddr = (FieldInst) inst;
        assert TEMPLATE_NAME_SERVER_1_IP_ADDRESS_2.equals(nsIpAddr.getValue());
        inst = domainNameServerIterator.next();
        assert inst instanceof SectionInst;
        domainNameServer = (SectionInst) inst;
        assert TEMPLATE_NAME_SERVER_2_HOST_NAME.equals(domainNameServer.getFieldInstance(TEMPLATE_HOST_NAME_FIELD_NAME).getValue());
        nsIpAddrList = domainNameServer.getListInstance(TEMPLATE_HOST_IPADDRESS_FIELD_NAME);
        assert nsIpAddrList.getList().size() == 1;
        inst = nsIpAddrList.getList().iterator().next();
        assert inst instanceof FieldInst;
        nsIpAddr = (FieldInst) inst;
        assert TEMPLATE_NAME_SERVER_2_IP_ADDRESS.equals(nsIpAddr.getValue());
        inst = domainNameServerIterator.next();
        assert inst instanceof SectionInst;
        domainNameServer = (SectionInst) inst;
        mod = domainNameServer.getModificator();
        assert mod != null;
        assert mod instanceof Remove;
        Remove rem = (Remove) mod;
        assert TEMPLATE_NAME_SERVER_3_REMOVE_NAME.equals(rem.getElementName());

        // domain registry url
        assert TEMPLATE_DOMAIN_REGISTRY_URL.equals(si.getFieldInstance(TEMPLATE_DOMAIN_REGISTRY_URL_FIELD_NAME).getValue());

        // domain whois server
        assert TEMPLATE_DOMAIN_WHOIS_SERVER.equals(si.getFieldInstance(TEMPLATE_DOMAIN_WHOIS_SERVER_FIELD_NAME).getValue());
    }

    private static final String TEMPLATE_MAIL_INVALID_MESSAGE = "Required element missing: [Supporting Organization]";

    @Test(expectedExceptions = MailParserTemplateException.class)
    public void testParseTemplateMailInvalid() throws Exception {
        try {
            mailParser.parse(TEMPLATE_VALID_SUBJECT, loadFromFile(TEMPLATE_INVALID_FILE_NAME));
        } catch (MailParserException e) {
            assert TEMPLATE_MAIL_INVALID_MESSAGE.equals(e.getMessage()) : "unexpected exception message: " + e.getMessage();
            throw e;
        }
    }

    private String loadFromFile(String fileName) throws IOException {
        DataInputStream dis = new DataInputStream(getClass().getResourceAsStream(fileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(dis, "US-ASCII"));
        StringBuffer buf = new StringBuffer();
        String line = reader.readLine();
        if (line != null) {
            buf.append(line);
            while ((line = reader.readLine()) != null) {
                buf.append("\n");
                buf.append(line);
            }
        }
        reader.close();
        dis.close();
        return buf.toString();
    }
}
