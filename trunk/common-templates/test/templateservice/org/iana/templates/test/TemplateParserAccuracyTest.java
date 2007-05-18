package org.iana.templates.test;

import org.iana.templates.TemplatesService;
import org.iana.templates.TemplatesServiceBean;
import org.iana.templates.def.parser.TemplatesManager;
import org.iana.templates.inst.*;
import org.testng.annotations.Test;

import java.util.Iterator;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class TemplateParserAccuracyTest {
    private static final String TEMPLATE_FILE_NAME = "template.txt.asc";

    private static final String DOMAIN_NAME_FIELD_NAME = "name";
    private static final String DOMAIN_SUPP_ORG_SECTION_NAME = "supportingOrg";
    private static final String DOMAIN_ADMIN_CONTACT_SECTION_NAME = "adminContact";
    private static final String DOMAIN_TECH_CONTACT_SECTION_NAME = "techContact";
    private static final String DOMAIN_NAME_SERVERS_SECTION_NAME = "nameServer";
    private static final String DOMAIN_REGISTRY_URL_FIELD_NAME = "registryUrl";
    private static final String DOMAIN_WHOIS_SERVER_FIELD_NAME = "whoisServer";
    private static final String CONTACT_NAME_FIELD_NAME = "name";
    private static final String CONTACT_ORGANIZATION_FIELD_NAME = "organization";
    private static final String CONTACT_ADDRESS_SECTION_NAME = "address";
    private static final String CONTACT_ADDRESS_TEXT_FIELD_NAME = "textAddress";
    private static final String CONTACT_ADDRESS_CC_FIELD_NAME = "countryCode";
    private static final String CONTACT_PHONE_FIELD_NAME = "phone";
    private static final String CONTACT_FAX_FIELD_NAME = "fax";
    private static final String CONTACT_EMAIL_FIELD_NAME = "email";
    private static final String HOST_NAME_FIELD_NAME = "name";
    private static final String HOST_IPADDRESS_FIELD_NAME = "ipAddress";

    private static final String DOMAIN_NAME_VALUE = "SH";
    private static final String TECHC_NAME_VALUE = "Administrator";
    private static final String TECHC_ORGANIZATION_VALUE = "ICB Plc";
    private static final String TECHC_TEXT_ADDRESS_1_VALUE = "9 Queens Road";
    private static final String TECHC_COUNTRY_CODE_1_VALUE = "UK";
    private static final String TECHC_TEXT_ADDRESS_2_VALUE = "11 Queens Road";
    private static final String TECHC_COUNTRY_CODE_2_VALUE = "UK";
    private static final String NAME_SERVER_1_REPLACE_NAME = "ns-ext.vix.com";
    private static final String NAME_SERVER_1_HOST_NAME = "ns-ext.vix.com";
    private static final String NAME_SERVER_1_IP_ADDRESS_1 = "204.152.184.64";
    private static final String NAME_SERVER_1_IP_ADDRESS_2 = "204.152.184.65";
    private static final String NAME_SERVER_2_HOST_NAME = "ns-1.vix.com";
    private static final String NAME_SERVER_2_IP_ADDRESS = "204.152.184.65";
    private static final String NAME_SERVER_3_REMOVE_NAME = "ns-2.vix.com";
    private static final String DOMAIN_REGISTRY_URL = "registry.url";
    private static final String DOMAIN_WHOIS_SERVER = "whois.url";

    public void test() throws Exception {
        TemplatesManager templatesManager = new TemplatesManager(TemplateParserTestUtil.TEMPLATE_DEF_FILE_NAME);
        TemplatesService templatesService = new TemplatesServiceBean(templatesManager);
        SectionInst si = templatesService.parseTemplate(TemplateParserTestUtil.loadFromFile(TEMPLATE_FILE_NAME));

        // domain name
        assert DOMAIN_NAME_VALUE.equals(si.getFieldInstance(DOMAIN_NAME_FIELD_NAME).getValue());

        // domain supporting organization section
        assert NoChange.getInstance().equals(si.getSectionInstance(DOMAIN_SUPP_ORG_SECTION_NAME).getModificator());

        // domain admin contact section - no change
        ListInst domainAdminContactList = si.getListInstance(DOMAIN_ADMIN_CONTACT_SECTION_NAME);
        assert domainAdminContactList.getList().size() == 1;
        ElementInst inst = domainAdminContactList.getList().iterator().next();
        assert inst instanceof SectionInst;
        SectionInst domainAdminContact = (SectionInst) inst;
        assert NoChange.getInstance().equals(domainAdminContact.getModificator());

        // domain tech contact section
        ListInst domainTechCList = si.getListInstance(DOMAIN_TECH_CONTACT_SECTION_NAME);
        assert domainTechCList.getList().size() == 1;
        inst = domainTechCList.getList().iterator().next();
        assert inst instanceof SectionInst;
        SectionInst domainTechC = (SectionInst) inst;
        assert TECHC_NAME_VALUE.equals(domainTechC.getFieldInstance(CONTACT_NAME_FIELD_NAME).getValue());
        assert TECHC_ORGANIZATION_VALUE.equals(domainTechC.getFieldInstance(CONTACT_ORGANIZATION_FIELD_NAME).getValue());
        ListInst domainTechCAddressList = domainTechC.getListInstance(CONTACT_ADDRESS_SECTION_NAME);
        assert domainTechCAddressList.getList().size() == 2;
        Iterator<ElementInst> domainTechCAddressIterator = domainTechCAddressList.getList().iterator();
        inst = domainTechCAddressIterator.next();
        assert inst instanceof SectionInst;
        SectionInst domainTechCAddress = (SectionInst) inst;
        assert TECHC_TEXT_ADDRESS_1_VALUE.equals(domainTechCAddress.getFieldInstance(CONTACT_ADDRESS_TEXT_FIELD_NAME).getValue());
        assert TECHC_COUNTRY_CODE_1_VALUE.equals(domainTechCAddress.getFieldInstance(CONTACT_ADDRESS_CC_FIELD_NAME).getValue());
        inst = domainTechCAddressIterator.next();
        assert inst instanceof SectionInst;
        domainTechCAddress = (SectionInst) inst;
        assert TECHC_TEXT_ADDRESS_2_VALUE.equals(domainTechCAddress.getFieldInstance(CONTACT_ADDRESS_TEXT_FIELD_NAME).getValue());
        assert TECHC_COUNTRY_CODE_2_VALUE.equals(domainTechCAddress.getFieldInstance(CONTACT_ADDRESS_CC_FIELD_NAME).getValue());
        ListInst domainTechCPhoneList = domainTechC.getListInstance(CONTACT_PHONE_FIELD_NAME);
        assert domainTechCPhoneList.getList().size() == 1;
        inst = domainTechCPhoneList.getList().iterator().next();
        assert inst instanceof FieldInst;
        FieldInst domainTechCPhone = (FieldInst) inst;
        assert NoChange.getInstance().equals(domainTechCPhone.getModificator());
        ListInst domainTechCFaxList = domainTechC.getListInstance(CONTACT_FAX_FIELD_NAME);
        assert domainTechCFaxList.getList().size() == 1;
        inst = domainTechCFaxList.getList().iterator().next();
        assert inst instanceof FieldInst;
        FieldInst domainTechCFax = (FieldInst) inst;
        assert NoChange.getInstance().equals(domainTechCFax.getModificator());
        ListInst domainTechCEmailList = domainTechC.getListInstance(CONTACT_EMAIL_FIELD_NAME);
        assert domainTechCEmailList.getList().size() == 1;
        inst = domainTechCEmailList.getList().iterator().next();
        assert inst instanceof FieldInst;
        FieldInst domainTechCEmail = (FieldInst) inst;
        assert NoChange.getInstance().equals(domainTechCEmail.getModificator());

        // domain name server section
        ListInst domainNameServerList = si.getListInstance(DOMAIN_NAME_SERVERS_SECTION_NAME);
        assert domainNameServerList.getList().size() == 3;
        Iterator<ElementInst> domainNameServerIterator = domainNameServerList.getList().iterator();
        inst = domainNameServerIterator.next();
        assert inst instanceof SectionInst;
        SectionInst domainNameServer = (SectionInst) inst;
        Modificator mod = domainNameServer.getModificator();
        assert mod != null;
        assert mod instanceof Replace;
        Replace rep = (Replace) mod;
        assert NAME_SERVER_1_REPLACE_NAME.equals(rep.getOldElementName());
        assert NAME_SERVER_1_HOST_NAME.equals(domainNameServer.getFieldInstance(HOST_NAME_FIELD_NAME).getValue());
        ListInst nsIpAddrList = domainNameServer.getListInstance(HOST_IPADDRESS_FIELD_NAME);
        assert nsIpAddrList.getList().size() == 2;
        Iterator<ElementInst> nsIpAddrIterator = nsIpAddrList.getList().iterator();
        inst = nsIpAddrIterator.next();
        assert inst instanceof FieldInst;
        FieldInst nsIpAddr = (FieldInst) inst;
        assert NAME_SERVER_1_IP_ADDRESS_1.equals(nsIpAddr.getValue());
        inst = nsIpAddrIterator.next();
        assert inst instanceof FieldInst;
        nsIpAddr = (FieldInst) inst;
        assert NAME_SERVER_1_IP_ADDRESS_2.equals(nsIpAddr.getValue());
        inst = domainNameServerIterator.next();
        assert inst instanceof SectionInst;
        domainNameServer = (SectionInst) inst;
        assert NAME_SERVER_2_HOST_NAME.equals(domainNameServer.getFieldInstance(HOST_NAME_FIELD_NAME).getValue());
        nsIpAddrList = domainNameServer.getListInstance(HOST_IPADDRESS_FIELD_NAME);
        assert nsIpAddrList.getList().size() == 1;
        inst = nsIpAddrList.getList().iterator().next();
        assert inst instanceof FieldInst;
        nsIpAddr = (FieldInst) inst;
        assert NAME_SERVER_2_IP_ADDRESS.equals(nsIpAddr.getValue());
        inst = domainNameServerIterator.next();
        assert inst instanceof SectionInst;
        domainNameServer = (SectionInst) inst;
        mod = domainNameServer.getModificator();
        assert mod != null;
        assert mod instanceof Remove;
        Remove rem = (Remove) mod;
        assert NAME_SERVER_3_REMOVE_NAME.equals(rem.getElementName());

        // domain registry url
        assert DOMAIN_REGISTRY_URL.equals(si.getFieldInstance(DOMAIN_REGISTRY_URL_FIELD_NAME).getValue());

        // domain whois server
        assert DOMAIN_WHOIS_SERVER.equals(si.getFieldInstance(DOMAIN_WHOIS_SERVER_FIELD_NAME).getValue());
    }
}
