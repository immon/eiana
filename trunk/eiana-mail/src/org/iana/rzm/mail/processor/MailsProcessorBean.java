package org.iana.rzm.mail.processor;

import org.apache.log4j.Logger;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.facade.system.trans.SystemTransactionService;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.mail.parser.*;
import org.iana.rzm.user.UserManager;
import org.iana.templates.inst.ElementInst;
import org.iana.templates.inst.FieldInst;
import org.iana.templates.inst.ListInst;
import org.iana.templates.inst.SectionInst;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class MailsProcessorBean implements MailsProcessor {
    private static final String RESPONSE_PREFIX = "Re: ";

    private MailParser parser;
    private AuthenticationService authSvc;
    private SystemTransactionService transSvc;
    private SystemDomainService domSvc;
    private NotificationSender notSdr;
    private NotificationManager notMgr;
    private UserManager usrMgr;


    public MailsProcessorBean(MailParser parser, AuthenticationService authSvc,
                              SystemTransactionService transSvc, SystemDomainService domSvc,
                              NotificationSender notSdr, NotificationManager notMgr,
                              UserManager usrMgr) {
        this.parser = parser;
        this.authSvc = authSvc;
        this.transSvc = transSvc;
        this.domSvc = domSvc;
        this.notSdr = notSdr;
        this.notMgr = notMgr;
        this.usrMgr = usrMgr;
    }

    public void process(String email, String subject, String content) throws MailsProcessorException {
        AuthenticatedUser user = null;
        MailData mailData = null;
        try {
            mailData = parser.parse(subject, content);
//            mailData = parser.parse(subject, PGPUtils.getSignedMessageContent(content));
            if (mailData instanceof ConfirmationMailData) {
                ConfirmationMailData confData = (ConfirmationMailData) mailData;
                // user = authSvc.authenticate(new MailAuth(email, confData.getDomainName()));
                // user = null;
                transSvc.setUser(user);
                domSvc.setUser(user);
                processConfirmation(confData, email);
            } else if (mailData instanceof TemplateMailData) {
                user = authSvc.authenticate(new MailAuth(email));
                transSvc.setUser(user);
                domSvc.setUser(user);
                processTemplate((TemplateMailData) mailData, email);
            } else {
                createEmailNotification(email, subject, content,
                        "Error occured while processing your request.");
            }
//        } catch (PGPUtilsException e) {
//            createEmailNotification(user == null ? email : email, mailData,
//                    "Error occured while processing your request.");
//            Logger.getLogger(getClass()).error(e);
        } catch (AuthenticationFailedException e) {
            createEmailNotification(email, subject, content, "Authentication failed.");
            Logger.getLogger(getClass()).error(e);
        } catch (AuthenticationRequiredException e) {
            createEmailNotification(email, subject, content, "Authentication failed.");
            Logger.getLogger(getClass()).error(e);
        } catch (MailParserException e) {
            createEmailNotification(email, subject, content, "Mail content parse error: \n" + e.getMessage());
            Logger.getLogger(getClass()).error(e);
        }
    }

    private void processConfirmation(ConfirmationMailData data, String email) {
        try {
            TransactionVO trans = transSvc.getTransaction(data.getTransactionId());
            if (trans == null) {
                createEmailNotification(email, data,
                        "Transaction id not found: " + data.getTransactionId());
                return;
            }
            if (!data.getStateName().equals(trans.getState().getName().toString())) {
                createEmailNotification(email, data,
                        "wrong transaction state = " + data.getStateName() +
                                ", expected: " + trans.getState().getName());
                return;
            }
            if (data.isAccepted())
                transSvc.acceptTransaction(trans.getTransactionID(), data.getToken());
            else
                transSvc.rejectTransaction(trans.getTransactionID(), data.getToken());
            createEmailNotification(email, data, "Your request was successfully processed.");
        } catch (InfrastructureException e) {
            createEmailNotification(email, data, "Error occured while processing your request.");
            Logger.getLogger(getClass()).error(e);
        } catch (NoObjectFoundException e) {
            createEmailNotification(email, data, "Nonexistent ticket id.");
            Logger.getLogger(getClass()).error(e);
        }
    }

    private String createMessageTemplateProcessed(List<TransactionVO> transactions) {
        StringBuffer sb = new StringBuffer();
        sb.append("Your request was successfully processed. Following transactions were created:\n");
        for (TransactionVO trans : transactions) {
            sb.append("ticket id: ").append(trans.getTicketID()).append(", ");
            sb.append("name: ").append(trans.getName()).append(", ");
            sb.append("domain name: ").append(trans.getDomainName()).append("\n");
        }
        return sb.toString();
    }

    private void processTemplate(TemplateMailData data, String email) {
        try {
            IDomainVO domain = updateDomain(data.getTemplate());
            List<TransactionVO> transactions = transSvc.createTransactions(domain, false);
            if (transactions.isEmpty())
                createEmailNotification(email, data, "Domain data are the same as in the message.");
            else
                createEmailNotification(email, data, createMessageTemplateProcessed(transactions));
        } catch (InfrastructureException e) {
            createEmailNotification(email, data, "Error occured while processing your request.");
            Logger.getLogger(getClass()).error(e);
        } catch (NoObjectFoundException e) {
            createEmailNotification(email, data, "Domain name not found.");
            Logger.getLogger(getClass()).error(e);
        } catch (MailsProcessorException e) {
            createEmailNotification(email, data, e.getMessage());
            Logger.getLogger(getClass()).error(e);
        } catch (NoDomainModificationException e) {
            createEmailNotification(email, data, "Domain data are the same as in the message.");
            Logger.getLogger(getClass()).error(e);
        } catch (Throwable e) {
            createEmailNotification(email, data, e.getMessage());
            Logger.getLogger(getClass()).error(e);
        }
    }

    private void createEmailNotification(String email, MailData data, String message) {
        createEmailNotification(email, data.getOriginalSubject(), data.getOriginalBody(), message);
    }

    private void createEmailNotification(String email, String originalSubject, String originalContent, String message) {
        createNotification(new EmailAddressee(email, email), originalSubject, originalContent, message);
    }

    private void createNotification(Addressee addressee, String originalSubject, String originalContent, String message) {
        Content content = new TextContent(RESPONSE_PREFIX + originalSubject,
                quote(originalContent) + "\n" + message);
        Notification notification = new Notification();
        notification.addAddressee(addressee);
        notification.setContent(content);
        try {
            notSdr.send(notification.getAddressee(), notification.getContent());
        } catch (NotificationException e) {
            notification.incSentFailures();
            notMgr.create(notification);
        }
    }

    private String quote(String s) {
        StringTokenizer st = new StringTokenizer(s, "\n");
        String result = "";
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            line = quoteLine(line);
            result += line + "\n";
        }
        return result;
    }

    private String quoteLine(String line) {
        if (line == null) return null;
        return "> " + line;
    }

    private static final String DOMAIN_NAME_FIELD_NAME = "name";
    private static final String DOMAIN_SUPP_ORG_SECTION_NAME = "supportingOrg";
    private static final String DOMAIN_ADMIN_CONTACT_SECTION_NAME = "adminContact";
    private static final String DOMAIN_TECH_CONTACT_SECTION_NAME = "techContact";
    private static final String DOMAIN_NAME_SERVERS_SECTION_NAME = "nameServer";
    private static final String DOMAIN_REGISTRY_URL_FIELD_NAME = "registryUrl";
    private static final String DOMAIN_WHOIS_SERVER_FIELD_NAME = "whoisServer";

    private IDomainVO updateDomain(SectionInst template) throws InfrastructureException, NoObjectFoundException, MailsProcessorException {
        String domainName = getFieldValue(template.getFieldInstance(DOMAIN_NAME_FIELD_NAME));
        IDomainVO domain = domSvc.getDomain(domainName);

        SectionInst suppOrgSect = template.getSectionInstance(DOMAIN_SUPP_ORG_SECTION_NAME);
        domain.setSupportingOrg(toContact(suppOrgSect, domain.getSupportingOrg()));

        domain.setAdminContacts(updateContacts(domain.getAdminContacts(),
                template.getListInstance(DOMAIN_ADMIN_CONTACT_SECTION_NAME)));
        domain.setTechContacts(updateContacts(domain.getTechContacts(),
                template.getListInstance(DOMAIN_TECH_CONTACT_SECTION_NAME)));
        domain.setNameServers(updateHosts(domain.getNameServers(),
                template.getListInstance(DOMAIN_NAME_SERVERS_SECTION_NAME)));

        domain.setRegistryUrl(getFieldValue(template.getFieldInstance(DOMAIN_REGISTRY_URL_FIELD_NAME),
                domain.getRegistryUrl()));
        domain.setWhoisServer(new Name(getFieldValue(template.getFieldInstance(DOMAIN_WHOIS_SERVER_FIELD_NAME),
                domain.getWhoisServer().getName())));

        return domain;
    }

    private List<ContactVO> updateContacts(List<ContactVO> contacts, ListInst contactList) throws MailsProcessorException {
        for (ElementInst element : contactList.getList()) {
            SectionInst section = (SectionInst) element;
            if (section.isNotChanged()) continue;
            if (section.isRemoved()) {
                ContactVO key = new ContactVO();
                key.setName(section.getRemovedElementName());
                // todo: na liscie nieposortowanej binary search nie dziala
                int i = Collections.binarySearch(contacts, key, contactComparator);
                if (i < 0)
                    throw new MailsProcessorException("Contact to remove does not exist: " +
                            key.getName());
                contacts.remove(i);
                continue;
            }
            if (section.isReplaced()) {
                ContactVO key = new ContactVO();
                key.setName(section.getReplacedElementName());
                // todo: na liscie nieposortowanej binary search nie dziala
                int i = Collections.binarySearch(contacts, key, contactComparator);
                if (i < 0)
                    throw new MailsProcessorException("Contact to replace does not exist: " +
                            key.getName());
                contacts.remove(i);
            }
            contacts.add(toContact(section, new ContactVO()));
        }
        return contacts;
    }

    private List<HostVO> updateHosts(List<HostVO> hosts, ListInst hostList) throws MailsProcessorException {
        for (ElementInst element : hostList.getList()) {
            SectionInst section = (SectionInst) element;
            if (section.isNotChanged()) continue;
            if (section.isRemoved()) {
                HostVO key = new HostVO();
                key.setName(section.getRemovedElementName());
                int i = Collections.binarySearch(hosts, key, hostComparator);
                if (i < 0)
                    throw new MailsProcessorException("Host to remove does not exist: " +
                            key.getName());
                hosts.remove(i);
                continue;
            }
            if (section.isReplaced()) {
                HostVO key = new HostVO();
                key.setName(section.getReplacedElementName());
                int i = Collections.binarySearch(hosts, key, hostComparator);
                if (i < 0)
                    throw new MailsProcessorException("Host to replace does not exist: " +
                            key.getName());
                hosts.remove(i);
            }
            hosts.add(toHost(section, new HostVO()));
        }
        return hosts;
    }

    private static final String CONTACT_NAME_FIELD_NAME = "name";
    private static final String CONTACT_ORGANIZATION_FIELD_NAME = "organization";
    private static final String CONTACT_ADDRESS_SECTION_NAME = "address";
    private static final String CONTACT_PHONE_FIELD_NAME = "phone";
    private static final String CONTACT_FAX_FIELD_NAME = "fax";
    private static final String CONTACT_EMAIL_FIELD_NAME = "email";

    private ContactVO toContact(SectionInst section, ContactVO contact) {
        if (section.isNotChanged()) return contact;
        if (contact == null) contact = new ContactVO();
        contact.setName(getFieldValue(section.getFieldInstance(CONTACT_NAME_FIELD_NAME)));
        contact.setOrganization(getFieldValue(section.getFieldInstance(CONTACT_ORGANIZATION_FIELD_NAME)));
        contact.setAddresses(toAddressList(section.getListInstance(CONTACT_ADDRESS_SECTION_NAME),
                contact.getAddresses()));
        contact.setPhoneNumbers(toStringList(section.getListInstance(CONTACT_PHONE_FIELD_NAME),
                contact.getPhoneNumbers()));
        contact.setFaxNumbers(toStringList(section.getListInstance(CONTACT_FAX_FIELD_NAME),
                contact.getFaxNumbers()));
        contact.setEmails(toStringList(section.getListInstance(CONTACT_EMAIL_FIELD_NAME),
                contact.getEmails()));
        return contact;
    }

    private static final String CONTACT_ADDRESS_TEXT_FIELD_NAME = "textAddress";
    private static final String CONTACT_ADDRESS_CC_FIELD_NAME = "countryCode";

    private List<AddressVO> toAddressList(ListInst list, List<AddressVO> currentAddressList) {
        List<AddressVO> addressList = new ArrayList<AddressVO>();
        for (ElementInst element : list.getList()) {
            SectionInst section = (SectionInst) element;
            if (section.isNotChanged())
                return currentAddressList;
            AddressVO address = new AddressVO();
            address.setTextAddress(getFieldValue(section.getFieldInstance(CONTACT_ADDRESS_TEXT_FIELD_NAME)));
            address.setCountryCode(getFieldValue(section.getFieldInstance(CONTACT_ADDRESS_CC_FIELD_NAME)));
            addressList.add(address);
        }
        return addressList;
    }

    private List<String> toStringList(ListInst list, List<String> currentList) {
        List<String> resultList = new ArrayList<String>();
        for (ElementInst element : list.getList()) {
            FieldInst field = (FieldInst) element;
            if (field.isNotChanged())
                return currentList;
            resultList.add(getFieldValue(field));
        }
        return resultList;
    }

    private static final String HOST_NAME_FIELD_NAME = "name";
    private static final String HOST_IPADDRESS_FIELD_NAME = "ipAddress";

    private HostVO toHost(SectionInst section, HostVO host) {
        if (host == null) host = new HostVO();
        host.setName(getFieldValue(section.getFieldInstance(HOST_NAME_FIELD_NAME)));
        for (ElementInst element : section.getListInstance(HOST_IPADDRESS_FIELD_NAME).getList())
            host.addAddress(toIpAddress((FieldInst) element));
        return host;
    }

    private IPAddressVO toIpAddress(FieldInst field) {
        IPAddressVO ipAddress = new IPAddressVO();
        ipAddress.setAddress(getFieldValue(field));
        ipAddress.setType(ipAddress.getAddress().contains(":") ? IPAddressVO.Type.IPv6 : IPAddressVO.Type.IPv4);
        return ipAddress;
    }

    private String getFieldValue(FieldInst field) {
        return field == null ? null : field.getValue();
    }

    private String getFieldValue(FieldInst field, String value) {
        if (field == null) return null;
        if (field.isNotChanged()) return value;
        if (field.isRemoved()) return null;
        return field.getValue();
    }

    private static ContactVOComparator contactComparator = new ContactVOComparator();

    static class ContactVOComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (o1 == o2) return 0;
            if (o1 == null) return -1;
            ContactVO c1 = (ContactVO) o1;
            ContactVO c2 = (ContactVO) o2;
            if (c2.getName() != null)
                return c2.getName().compareTo(c1.getName());
            else if (c1.getName() != null) return 1;
            else return 0;
        }
    }

    private static HostVOComparator hostComparator = new HostVOComparator();

    static class HostVOComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (o1 == o2) return 0;
            if (o1 == null) return -1;
            HostVO h1 = (HostVO) o1;
            HostVO h2 = (HostVO) o2;
            if (h2.getName() != null)
                return h2.getName().compareTo(h1.getName());
            else if (h1.getName() != null) return 1;
            else return 0;
        }
    }
}
