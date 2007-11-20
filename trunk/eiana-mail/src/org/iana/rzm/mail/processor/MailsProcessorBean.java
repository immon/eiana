package org.iana.rzm.mail.processor;

import org.apache.log4j.Logger;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.parser.*;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.UserManager;
import org.iana.templates.inst.ElementInst;
import org.iana.templates.inst.FieldInst;
import org.iana.templates.inst.ListInst;
import org.iana.templates.inst.SectionInst;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * todo: adjust to a new contact structure!
 *
 * @author Jakub Laszkiewicz
 */
public class MailsProcessorBean implements MailsProcessor {
    private static final String RESPONSE_PREFIX = "Re: ";

    private MailParser parser;
    private AuthenticationService authSvc;
    private TransactionService transSvc;
    private SystemDomainService domSvc;
    private NotificationSender notSdr;
    private NotificationManager notMgr;
    private UserManager usrMgr;


    public MailsProcessorBean(MailParser parser, AuthenticationService authSvc,
                              TransactionService transSvc, SystemDomainService domSvc,
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
        boolean isSigned = false;
        try {
            try {
                mailData = parser.parse(subject, PGPUtils.getSignedMessageContent(content));
                isSigned = true;
            } catch (PGPUtilsException e) {
                mailData = parser.parse(subject, content);
            }
            if (mailData instanceof ConfirmationMailData) {
                ConfirmationMailData confData = (ConfirmationMailData) mailData;
                if (isSigned) {
                    user = authSvc.authenticate(new MailAuth(email, null));
                    RZMUser rzmUser = usrMgr.get(user.getUserName());
                    if (!isGovOversight(rzmUser)) {
                        createEmailNotification(email, subject, content, "Authentication failed.");
                        return;
                    }
                    transSvc.setUser(user);
                    domSvc.setUser(user);
                    processConfirmation(confData, email, rzmUser);
                } else {
                    transSvc.setUser(user);
                    domSvc.setUser(user);
                    processConfirmation(confData, email);
                }
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

    public void process(MimeMessage message) throws MailsProcessorException {
        try {
            String subject = message.getSubject();
            InternetAddress from = new InternetAddress("" + message.getFrom()[0], false);
            if ((message.getContent() instanceof String)) {
                String content = (String) message.getContent();
                process(from.getAddress(), subject, content);
            } else {
                createEmailNotification(from.getAddress(), subject, null, "Not supported message format. Please send plain text message.");
                Logger.getLogger(getClass()).warn("Nontext message: [from: " + from + ", subject: " +
                        subject + ", content type: " + message.getContentType() + "]");
            }
        } catch (MessagingException e) {
            Logger.getLogger(getClass()).error(e);
        } catch (IOException e) {
            Logger.getLogger(getClass()).error(e);
        }
    }

    private void processConfirmation(ConfirmationMailData data, String email) {
        processConfirmation(data, email, null);
    }

    private void processConfirmation(ConfirmationMailData data, String email, RZMUser user) {
        try {
            List<TransactionVO> found = transSvc.getByTicketID(data.getTransactionId());
            if (found == null || found.isEmpty()) {
                createEmailNotification(email, data,
                        "Transaction id not found: " + data.getTransactionId());
                return;
            }
            if (found.size() > 1) {
                createEmailNotification(email, data,
                        "Transaction id not unique: " + data.getTransactionId());
                return;
            }
            TransactionVO trans = found.get(0);
            if (!data.getStateName().equals(trans.getState().getName().toString())) {
                createEmailNotification(email, data,
                        "wrong transaction state = " + data.getStateName() +
                                ", expected: " + trans.getState().getName());
                return;
            }
            if (user == null) {
                if (!TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION.equals(trans.getState().getName())) {
                    createEmailNotification(email, data,
                            "confirmation using token is allowed only in PENDING_CONTACT_CONFIRMATION state");
                    return;
                }
                if (data.getToken() == null) {
                    createEmailNotification(email, data, "token is missing");
                    return;
                }
                if (data.isAccepted())
                    transSvc.acceptTransaction(trans.getTransactionID(), data.getToken());
                else
                    transSvc.rejectTransaction(trans.getTransactionID(), data.getToken());
            } else {
                if (data.isAccepted())
                    transSvc.acceptTransaction(trans.getTransactionID());
                else
                    transSvc.rejectTransaction(trans.getTransactionID());
            }
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
        String quotedContent = originalContent != null ? quote(originalContent) + "\n" : "";
        Content content = new TextContent(RESPONSE_PREFIX + originalSubject,
                quotedContent + message);
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

    private IDomainVO updateDomain(SectionInst template) throws InfrastructureException, NoObjectFoundException, MailsProcessorException, InvalidCountryCodeException, InvalidEmailException {
        String domainName = getFieldValue(template.getFieldInstance(DOMAIN_NAME_FIELD_NAME));
        IDomainVO domain = domSvc.getDomain(domainName);

        SectionInst suppOrgSect = template.getSectionInstance(DOMAIN_SUPP_ORG_SECTION_NAME);
        domain.setSupportingOrg(toContact(suppOrgSect, domain.getSupportingOrg()));

        SectionInst adminContactSect = template.getSectionInstance(DOMAIN_ADMIN_CONTACT_SECTION_NAME);
        domain.setAdminContact(toContact(adminContactSect, domain.getAdminContact()));

        SectionInst techContactSect = template.getSectionInstance(DOMAIN_TECH_CONTACT_SECTION_NAME);
        domain.setTechContact(toContact(techContactSect, domain.getTechContact()));

        domain.setNameServers(updateHosts(domain.getNameServers(),
                template.getListInstance(DOMAIN_NAME_SERVERS_SECTION_NAME)));

        domain.setRegistryUrl(getFieldValue(template.getFieldInstance(DOMAIN_REGISTRY_URL_FIELD_NAME),
                domain.getRegistryUrl()));

        domain.setWhoisServer(new Name(getFieldValue(template.getFieldInstance(DOMAIN_WHOIS_SERVER_FIELD_NAME),
                domain.getWhoisServer().getName())));

        return domain;
    }

    private List<HostVO> updateHosts(List<HostVO> hosts, ListInst hostList) throws MailsProcessorException {
        for (ElementInst element : hostList.getList()) {
            SectionInst section = (SectionInst) element;
            if (section.isNotChanged()) continue;
            if (section.isRemoved()) {
                HostVO key = new HostVO();
                key.setName(section.getRemovedElementName());
                Collections.sort(hosts, hostComparator);
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
                Collections.sort(hosts, hostComparator);
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
    private static final String CONTACT_JOB_TITLE_FIELD_NAME = "jobTitle";
    private static final String CONTACT_ADDRESS_SECTION_NAME = "address";
    private static final String CONTACT_PHONE_FIELD_NAME = "phoneNumber";
    private static final String CONTACT_ALT_PHONE_FIELD_NAME = "altPhoneNumber";
    private static final String CONTACT_FAX_FIELD_NAME = "faxNumber";
    private static final String CONTACT_ALT_FAX_FIELD_NAME = "altFaxNumber";
    private static final String CONTACT_PUBLIC_EMAIL_FIELD_NAME = "publicEmail";
    private static final String CONTACT_PRIVATE_EMAIL_FIELD_NAME = "privateEmail";

    private ContactVO toContact(SectionInst section, ContactVO contact) throws InvalidCountryCodeException, InvalidEmailException {
        if (section.isNotChanged()) return contact;
        if (contact == null) contact = new ContactVO();
        contact.setName(getFieldValue(section.getFieldInstance(CONTACT_NAME_FIELD_NAME)));
        contact.setOrganization(getFieldValue(section.getFieldInstance(CONTACT_ORGANIZATION_FIELD_NAME)));
        contact.setJobTitle(getFieldValue(section.getFieldInstance(CONTACT_JOB_TITLE_FIELD_NAME)));
        contact.setAddress(toAddress(section.getSectionInstance(CONTACT_ADDRESS_SECTION_NAME),
                contact.getAddress()));
        contact.setPhoneNumber(getFieldValue(section.getFieldInstance(CONTACT_PHONE_FIELD_NAME),
                contact.getPhoneNumber()));
        contact.setAltPhoneNumber(getFieldValue(section.getFieldInstance(CONTACT_ALT_PHONE_FIELD_NAME),
                contact.getPhoneNumber()));
        contact.setFaxNumber(getFieldValue(section.getFieldInstance(CONTACT_FAX_FIELD_NAME),
                contact.getFaxNumber()));
        contact.setAltFaxNumber(getFieldValue(section.getFieldInstance(CONTACT_ALT_FAX_FIELD_NAME),
                contact.getFaxNumber()));
        contact.setPublicEmail(getFieldValue(section.getFieldInstance(CONTACT_PUBLIC_EMAIL_FIELD_NAME),
                contact.getEmail()));
        contact.setPrivateEmail(getFieldValue(section.getFieldInstance(CONTACT_PRIVATE_EMAIL_FIELD_NAME),
                contact.getEmail()));
        return contact;
    }

    private static final String CONTACT_ADDRESS_TEXT_FIELD_NAME = "textAddress";
    private static final String CONTACT_ADDRESS_CC_FIELD_NAME = "countryCode";

    private AddressVO toAddress(SectionInst section, AddressVO address) throws InvalidCountryCodeException {
        if (section.isNotChanged()) return address;
        if (address == null) address = new AddressVO();
        address.setTextAddress(getFieldValue(section.getFieldInstance(CONTACT_ADDRESS_TEXT_FIELD_NAME)));
        address.setCountryCode(getFieldValue(section.getFieldInstance(CONTACT_ADDRESS_CC_FIELD_NAME)));
        return address;
    }

    private static final String HOST_NAME_FIELD_NAME = "name";
    private static final String HOST_IPADDRESS_FIELD_NAME = "ipAddress";

    private HostVO toHost(SectionInst section, HostVO host) {
        if (host == null) host = new HostVO();
        host.setName(getFieldValue(section.getFieldInstance(HOST_NAME_FIELD_NAME)));
        for (ElementInst element : section.getListInstance(HOST_IPADDRESS_FIELD_NAME).getList())
            host.setAddress(toIpAddress((FieldInst) element));
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

    private boolean isGovOversight(RZMUser user) {
        for (Role role : user.getRoles())
            if (AdminRole.AdminType.GOV_OVERSIGHT.equals(role.getType()))
                return true;
        return false;
    }
}
