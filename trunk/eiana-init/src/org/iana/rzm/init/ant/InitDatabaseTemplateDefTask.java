package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.pgp.PgpKey;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Piotr Tkaczyk
 */
public class InitDatabaseTemplateDefTask extends HibernateTask {

    public void doExecute(Session session) throws Exception {

        PgpKey defaultKey = new PgpKey("defaultKey",
                "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
                "Version: GnuPG v1.4.7 (MingW32)\n" +
                "\n" +
                "lQHhBEZEHakRBACVGdhNG6Mhyjv2oKvXA9Ds+NG+XrxO9tNYqA7qtJZusxzcI1Z+\n" +
                "oYTrK7qsig06ORiOxcYVhTIeTrRNFC42Mnw/NffepMIzyi7pM1UZpMjXc05e+UjU\n" +
                "eRoZHsFu/75iYWj9OBITqPxJfWDtnq9Ym/6jab0xpGXfWg89rJylm9l4fwCg7r2X\n" +
                "/9XMmCRDP37uTtPu4qHeAIMD/iAxrESBDihsmTyCxLwy94n1H6xGdN+SDku3qTCW\n" +
                "+GuP3hAkzYBpbLTptxwALwANMPK4dNWJStgM8dB+uwpwwlXHRk31zSXZo6sjPUQh\n" +
                "O23x3LVnfKJd73nSd4bGJ8C+x2m6xfbExcb8s/c25SBqJH3ccjOUuIBDhyTeuiFQ\n" +
                "K2lvA/4n4k6Q6fJd5Zjq+daL5PqL22Yn4+3x7BLIVCDp+OETY70pwUCR474yMxkN\n" +
                "8RgOQOSI0oB0me8y9DLrszFq4mbnD08AtJDiVkL6fx9OXSLBz3+7GDUSy5qhn5L9\n" +
                "RXq8WBHv+y21nzT7p2JwMxgxOhv1tQvsdpSUZufx+sGpx9eYwP4DAwKumZxN6Yj0\n" +
                "q2DA35gUYZrEFrVGH51Z9SjQ9Y5ScG7fX5nJWWqWe3fFuVj7bnG5V+MUXNKy5ZBB\n" +
                "0gCEsbQYVGVzdGVyIDx0ZXN0ZXJAaWFuYS5vcmc+iGAEExECACAFAkZEHakCGwMG\n" +
                "CwkIBwMCBBUCCAMEFgIDAQIeAQIXgAAKCRBHLW1HQP6WTH3IAKC0vv9ycrG3elzA\n" +
                "+QNU5Ou++JQMpQCguZwj5ockoa6W+UT3wruNSbUalNGdAmMERkQdqRAIAJJ1xfgU\n" +
                "n+d6C35CrJHdKgUuTsFq5E8zMuXmSG1FH5/OXDajGML/rVMA8IId2+4xnNfUFXjo\n" +
                "pAYw0E+j6N+XVpQ4lx4Fng1RohQpcM9cxDScenE35xge43Ucxw6NDO+Wl5Y3iyLm\n" +
                "lnBk9xYNxjRdnSj2aUBkQAZyzqhhyb51dsNj11duwtMS/8AVukJKl94p1ta5Bex2\n" +
                "ff+FxkHviOnGwT1qDxJyX0BOzrB85GjuNJlrjODHyHHoAJeygAgteoxoj2HiZUxS\n" +
                "6AJCBpHnBhoYLqwWJ3a8pEzyyat+BnyBieLiGLF2Rkl4xXMJJHNNGWVVNvX9mHzS\n" +
                "D6rM7RQBLR4j2LcAAwUH/27WbnGmlwYRwqM0s4SlnuPNYHiugeVrqQjfUj3Lc8qL\n" +
                "8lRK+XGv6Y3xj/MlnD4frBwRFuXyW+sDCQMdHieEbBrWQxhJPszaxpHqB+XgLvUb\n" +
                "f27YfjyqjGOR/SuUdhbcPfswSga2bUaHwzW6y+1wzszSDs1ZpV2LQy4hMSyKX5mL\n" +
                "l8MmNIBbOE6oLVMD0zgxLSDawpe942SxS1yZi79Ekrf9wIUOlZrJoEcOmk9BNUh7\n" +
                "ixhW+xVY1wwQQR64A+V/auetr2Tnq3FJqujivG4PgrgD5vtUvjVYsFPUjVWTFz0h\n" +
                "bV34f6C6Z074W/lPBfA8Zi/yyZ9ls0fF6AriKvZ5zo7+AwMCrpmcTemI9KtgWjnb\n" +
                "XAFJwalyKqNFJ/6FkwPC1lGnMSkYXUqubnf8fPO5D5NKgIYxeuMb99ZTb1nsewuD\n" +
                "c2RAF4cYNgWSrpZVSkx5YByA+P0Tt4hJBBgRAgAJBQJGRB2pAhsMAAoJEEctbUdA\n" +
                "/pZMNq4AnjbgTomHb/kE70c9kq1nGjrlOjsCAKDgHKLy1nseC6rU5wKTFkDIvwKn\n" +
                "Qw==\n" +
                "=sf9+\n" +
                "-----END PGP PRIVATE KEY BLOCK-----",
                "tester");

        session.save(defaultKey);



        TemplateDef templateDef = new TemplateDef();
        templateDef.setType("contact-confirmation-newTLD");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC_CONFIRM", "TC_CONFIRM")));
        templateDef.setSubject("[IANA #{ticket}] Your confirmation requested to delegate {domainName} domain (%{token})");
        templateDef.setContent(
                "Dear {name} {title},\n" +
                "\n" +
                "The Internet Assigned Numbers Authority (IANA), in its role as\n" +
                "manager of the DNS root zone, has received a request to delegate\n" +
                "the {domainName} domain from {submitter}.\n" +
                "\n" +
                "The proposed data associated with this domain is as follows:\n" +
                "\n" +
                "{changes}\n" +
                "\n" +
                "As a proposed new contact for this domain you must consent to this\n" +
                "change.\n" +
                "\n" +
                "Please review this change to ensure it is correct, and then indicate\n" +
                "your approval or disapproval by visiting the following link:\n" +
                "\n" +
                "{url}\n" +
                "\n" +
                "or you can simply reply to this email with the words \"I ACCEPT\" or\n" +
                "\"I DECLINE\" on the first line.\n" +
                "\n" +
                "For your reference, this request has been assigned ticket number\n" +
                "{ticket}.\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("contact-confirmation");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC_CONFIRM", "TC_CONFIRM")));
        templateDef.setSubject("[IANA #{ticket}] Your confirmation requested to alter {domainName} domain (%{token})");
        templateDef.setContent(
                "Dear {name} {title},\n" +
                "\n" +
                "The Internet Assigned Numbers Authority (IANA), in its role as\n" +
                "manager of the DNS root zone, has received a request to alter the\n" +
                "information associated with the {domainName} domain from {submitter}. The\n" +
                "changes requested are as follows:\n" +
                "\n" +
                "{changes}\n" +
                "\n" +
                "As a listed contact, or proposed new contact, for this domain you\n" +
                "must consent to this change.\n" +
                "\n" +
                "{newContactOnly}\n" +
                "Please review this change to ensure it is correct, and then indicate\n" +
                "your approval or disapproval by visiting the following link:\n" +
                "\n" +
                "{url}\n" +
                "\n" +
                "or you can simply reply to this email with the words \"I ACCEPT\" or\n" +
                "\"I DECLINE\" on the first line.\n" +
                "\n" +
                "For your reference, this request has been assigned ticket number\n" +
                "{ticket}. Current administrative and technical contacts for a domain\n" +
                "can review the status of this request at any time at our website at\n" +
                "{url}\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        //not used
        templateDef = new TemplateDef();
        templateDef.setType("technical-deficiencies");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SUBMITTER")));
        templateDef.setSubject("[IANA #{ticket}] Please remedy technical problems for {domainName}");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "with reference number {ticket}.\n" +
                "\n" +
                "We have detected that there are technical problems with the request\n" +
                "you have submitted. The following errors were identified:\n" +
                "\n" +
                "{errors}\n" +
                "\n" +
                "We are unable to proceed until either these errors are remedied,\n" +
                "or you provide an explanation why this is desired behaviour and\n" +
                "why the request can proceed.\n" +
                "\n" +
                "Please read our technical requirements at\n" +
                "    http://www.iana.org/procedures/nameserver-requirements.html\n" +
                "\n" +
                "You can retest your servers, or provide this reasoning, through\n" +
                "the website at the following URL:\n" +
                "\n" +
                "{url}\n" +
                "\n" +
                "You have 30 days to remedy this, or we will automatically close\n" +
                "your request without prejudice. After this time you are free to\n" +
                "submit your request again once you have fixed the technical\n" +
                "problems.\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("normal_redelegation-processing");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SO")));
        templateDef.setSubject("[IANA #{ticket}] Processing has commenced for {domainName} request");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "{ticket}.\n" +
                "\n" +
                "This request is now being manually processed. In the case of\n" +
                "changes to the Supporting Organisation, this is considered a\n" +
                "\"redelegation\" and can involve extensive investigation. In such\n" +
                "cases, IANA staff will be in contact with you independently to verify\n" +
                "your qualifications, and to seek any additional documentation that may\n" +
                "be needed.\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("completed-nschange");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SUBMITTER")));
        templateDef.setSubject("[IANA #{ticket}] Change to {domainName} domain completed");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "{ticket}, lodged on {requestDate}.\n" +
                "\n" +
                "This request has now been completed!\n" +
                "\n" +
                "Please verify that your request has been properly implemented, and\n" +
                "if you notice any errors please contact IANA Root Zone Management at\n" +
                "root-mgmt@iana.org.\n" +
                "\n" +
                "Please note: It can take up to a day for all public databases and\n" +
                "name servers to be fully updated.\n" +
                "\n" +
                "For your information, this is the chronology of the request:\n" +
                "\n" +
                "* Request was first received: {requestDate}\n" +
                "* All contact conformations were received: {confirmationDate}\n" +
                "* The request was sent for USDOC/VRSN implementation: {docVrsnDate}\n" +
                "* The request was implemented by VRSN: {implementationDate}\n" +
                "* Serial number change was effected in: {serialNumber}\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("completed");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SUBMITTER")));
        templateDef.setSubject("[IANA #{ticket}] Change to {domainName} domain completed");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "{ticket}, lodged on {requestDate}.\n" +
                "\n" +
                "This request has now been completed!\n" +
                "\n" +
                "Please verify that your request has been properly implemented, and\n" +
                "if you notice any errors please contact IANA Root Zone Management at\n" +
                "root-mgmt@iana.org.\n" +
                "\n" +
                "Please note: It can take up to a day for all public databases to\n" +
                "be fully updated.\n" +
                "\n" +
                "For your information, this is the chronology of the request:\n" +
                "\n" +
                "* Request was first received: {requestDate}\n" +
                "* All contact conformations were received: {confirmationDate}\n" +
                "* The request was sent for USDOC/VRSN implementation: {docVrsnDate}\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("withdrawn");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SUBMITTER")));
        templateDef.setSubject("[IANA #{ticket}] Change request withdrawn for {domainName} domain");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "with reference number {ticket}.\n" +
                "\n" +
                "We have been requested to withdraw this request, and therefore this\n" +
                "request has been closed without prejudice.\n" +
                "\n" +
                "{widthdrawnReason}\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("admin-closed");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SUBMITTER")));
        templateDef.setSubject("[IANA #{ticket}] Change request administratively closed for {domainName} domain");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "with reference number {ticket}.\n" +
                "\n" +
                "We have administratively closed this request without prejudice.\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("exception");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("IANA")));
        templateDef.setSubject("[IANA #{ticket}] Status update for {domainName} domain");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "with reference number {ticket}.\n" +
                "\n" +
                "We have moved your request into an \"exception\" state. This means\n" +
                "that for some reason our automated workflow system is not able to\n" +
                "properly process your request, and we have flagged it for manual\n" +
                "review by IANA staff.\n" +
                "\n" +
                "The reason for the exception is as follows:\n" +
                "\n" +
                "{reason}\n" +
                "\n" +
                "We will advise you promptly if any action is required on your\n" +
                "part. For the moment, this is just a status update for your\n" +
                "information only.\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("rejected");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC", "SUBMITTER")));
        templateDef.setSubject("[IANA #{ticket}] Change request rejected for {domainName} domain");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is an update regarding the IANA Root Zone Management request\n" +
                "with reference number {ticket}.\n" +
                "\n" +
                "One of the contact persons for this domain have rejected this change,\n" +
                "and therefore this request has been closed.\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("contact-confirmation-remainder");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC")));
        templateDef.setSubject("[IANA #{ticket}] REMINDER: Your confirmation requested to alter {domainName} domain (%{token})");
        templateDef.setContent(
                "Dear {name} {title},\n" +
                "\n" +
                "***\n" +
                "NOTE: THIS IS A REMINDER THAT ACTION IS REQUIRED BY YOU. If you\n" +
                "do not respond within {period} days of the original application, that\n" +
                "is by {requestDate}, we will mark you as unresponsive and possibly\n" +
                "administratively close this request. Please review the materials\n" +
                "below and advise us if you consent to this request. If you have\n" +
                "any questions please don't hesitate to contact us at\n" +
                "root-mgmt@iana.org.\n" +
                "***\n" +
                "\n" +
                "The Internet Assigned Numbers Authority (IANA), in its role as\n" +
                "manager of the DNS root zone, has received a request to alter the\n" +
                "information associated with the {domainName} domain. The changes\n" +
                "requested are as follows:\n" +
                "\n" +
                "{changes}\n" +
                "\n" +
                "As a {currentOrNewContact}, for this domain you must consent to this change.\n" +
                "\n" +
                "{newContactOnly}\n" +
                "Please review this change to ensure it is correct, and then indicate\n" +
                "your approval or disapproval by visiting the following link:\n" +
                "\n" +
                "    {url}\n" +
                "\n" +
                "For your reference, this request has been assigned ticket number\n" +
                "{ticket}. Current administrative and technical contacts for a domain\n" +
                "can review the status of this request at any time at our website at\n" +
                "\n" +
                "    {url}\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("impacted_parties-confirmation");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC_IMPACTED_PARTIES", "TC_IMPACTED_PARTIES")));
        templateDef.setSubject("[IANA #{ticket}] Your confirmation requested to update your nameserver(s) ({token})");
        templateDef.setContent(
                "Dear {name},\n" +
                "\n" +
                "The Internet Assigned Numbers Authority (IANA), in its role as\n" +
                "manager of the DNS root zone, has received a request to alter the\n" +
                "IP address of an authoritative nameserver. As this nameserver\n" +
                "is used by the {domainName} domain, it requires your consent\n" +
                "for this change to be implemented. It is likely this request has\n" +
                "been lodged either by the operator of the listed nameservers,\n" +
                "or by another top-level domain operator that shares the use\n" +
                "of the listed nameservers.\n" +
                "\n" +
                "The changes requested are as follows:\n" +
                "\n" +
                "{changes}\n" +
                "\n" +
                "Please review this change to ensure it is correct, and then indicate\n" +
                "your approval or disapproval by visiting the following link:\n" +
                "\n" +
                "    {url}\n" +
                "\n" +
                "or you can simply reply to this email with the words \"I ACCEPT\" or\n" +
                "\"I DECLINE\" on the first line.\n" +
                "\n" +
                "For your reference, this request has been assigned ticket number\n" +
                "{ticket}. Current administrative and technical contacts for a domain\n" +
                "can review the status of this request at any time at our website at\n" +
                "{url}\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("impacted_parties-confirmation-remainder");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC_IMPACTED_PARTIES", "TC_IMPACTED_PARTIES")));
        templateDef.setSubject("[IANA #{ticket}] REMINDER: Your confirmation requested to update your nameserver(s) (%{token})");
        templateDef.setContent(
                "Dear {name} {title},\n" +
                "\n" +
                "The Internet Assigned Numbers Authority (IANA), in its role as\n" +
                "manager of the DNS root zone, has received a request to alter the\n" +
                "information associated with the {domainName} domain. The changes\n" +
                "requested are as follows:\n" +
                "\n" +
                "{changes}\n" +
                "\n" +
                "We have marked in our database a \"special instruction\" that your\n" +
                "or your organisation must consent to this change before it can\n" +
                "proceed.\n" +
                "\n" +
                "Please review this change to ensure it is correct, and then indicate\n" +
                "your approval or disapproval by visiting the following link:\n" +
                "\n" +
                "    {url}\n" +
                "\n" +
                "For your reference, this request has been assigned ticket number\n" +
                "{ticket}. Current administrative and technical contacts for a domain\n" +
                "can review the status of this request at any time at our website at\n" +
                "\n" +
                "    {url}\n" +
                "\n" +
                "If you have any questions regarding this request, please reply to\n" +
                "this email and we will try to assist you. Please ensure you do not\n" +
                "modify the subject as we need the reference number intact to ensure\n" +
                "the speediest processing of your enquiry.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you are\n" +
                "listed as a party to this request.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("zone-insertion-alert");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("IANA")));
        templateDef.setSubject("Domain modification");
        templateDef.setContent(
                "Zone Insertion Alert for {domainName}. Only {period} days left.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("zone-publication-alert");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("IANA")));
        templateDef.setSubject("Domain modification");
        templateDef.setContent(
                "Zone Publication Alert for {domainName}. Only {period} days left.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("technical-check-period");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC")));
        templateDef.setSubject("Domain {domainName} technical checks");
        templateDef.setContent(
                "Technical checks for domain {domainName}:\n" +
                "{passedList}\n" +
                "{errorList}\n" +
                "You have {days} day(s) left to make everything working properly.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("technical-check");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("AC", "TC")));
        templateDef.setSubject("Domain {domainName} technical checks");
        templateDef.setContent(
                "Technical checks for domain {domainName}:\n" +
                "{passedList}\n" +
                "{errorList}");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("usdoc-confirmation");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("GOV_OVERSIGHT")));
        templateDef.setSubject("[Root change {ticket}] Database change to {domainName}");
        templateDef.setContent(
                "Dear NTIA:\n" +
                "\n" +
                "IANA has received the following root zone database modification request\n" +
                "that requires your authorisation for implementation. This aspect\n" +
                "of this request will be machine read and processed, and you should\n" +
                "indicate your disposition of this request by inserting either the\n" +
                "word YES or NO after the phrase \"Authorized:\" below.\n" +
                "\n" +
                "Once your response is received, it will be verified by IANA, and if\n" +
                "authorized, the change will be implemented.\n" +
                "\n" +
                "IANA has provided the following additional notes on this change:\n" +
                "\n" +
                "{notes}\n" +
                "\n" +
                "%IANACHECKBLURB%\n" +
                "\n" +
                "This message has been automatically generated. If you have any\n" +
                "questions or concerns, please email them to root-mgmt@iana.org\n" +
                "with a subject of \"[IANA #{ticket}]\".\n" +
                "\n" +
                "Root Zone Services\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "--\n" +
                "\n" +
                "Authorized:\n" +
                "\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW\n" +
                "{db-change}\n" +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE");
        templateDef.setSigned(true);
        templateDef.setKeyName("defaultKey");
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("usdoc-confirmation-nschange");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("GOV_OVERSIGHT")));
        templateDef.setSubject("[Root change {ticket}:{retry}] Name server change to {domainName}");
        templateDef.setContent(
                "Dear NTIA:\n" +
                "\n" +
                "IANA has received the following root zone modification request\n" +
                "that requires your authorisation for implementation. This aspect\n" +
                "of this request will be machine read and processed, and you should\n" +
                "indicate your disposition of this request by inserting either the\n" +
                "word YES or NO after the phrase \"Authorized:\" below.\n" +
                "\n" +
                "Once your response is received, it will be cross-verified by both\n" +
                "IANA and VeriSign's systems, and if authorized, VeriSign will\n" +
                "commence implementation of this change.\n" +
                "\n" +
                "IANA has provided the following additional notes on this change:\n" +
                "\n" +
                "{notes}\n" +
                "\n" +
                "%IANACHECKBLURB%\n" +
                "\n" +
                "This message has been automatically generated. If you have any\n" +
                "questions or concerns, please email them to root-mgmt@iana.org\n" +
                "with a subject of \"[IANA #{ticket}]\".\n" +
                "\n" +
                "Root Zone Services\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "--\n" +
                "\n" +
                "Authorized:\n" +
                "\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW\n" +
                "{change}\n" +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE");
        templateDef.setSigned(true);
        templateDef.setKeyName("defaultKey");
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("usdoc-confirmation-remainder");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("GOV_OVERSIGHT")));
        templateDef.setSubject("[IANA #{ticket}] NTIA authorisation reminder for {domainName}");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "This is a reminder that we have not received a response on the authorisation\n" +
                "request for {domainName} with ticket number {ticket} and\n" +
                "transaction ID {transactionId}.\n" +
                "\n" +
                "Root Zone Management\n" +
                "\n" +
                "NOTE: This is an automated message.");
        templateDef.setSigned(true);
        templateDef.setKeyName("defaultKey");
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("password-change");
        templateDef.setSubject("Password change");
        templateDef.setContent(
                "Hello,\n" +
                "\n" +
                "We have received a request to recover the password for this email address's\n" +
                "account on the IANA Root Zone Management Interface.\n" +
                "\n" +
                "Your username is \"{userName}\".\n" +
                "\n" +
                "To set a new password, please visit the following link:\n" +
                "\n" +
                "    {link}\n" +
                "\n" +
                "If you did not request this, you may safely ignore this email.\n" +
                "\n" +
                "With kindest regards,\n" +
                "\n" +
                "Root Zone Management\n" +
                "Internet Assigned Numbers Authority\n" +
                "\n" +
                "NOTE: This has been an automated message, sent to you as you as someone\n" +
                "requested the recover the password for this account via our website.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("epp-error");
        templateDef.setSubject("EPP Poll Exception");
        templateDef.setContent(
                "The following exception happened:\n" +
                "{exception}");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("special-review");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("IANA")));
        templateDef.setSubject("New transaction with special review domain");
        templateDef.setContent(
                "Current domain: {domainName} has special review flag set on.");
        templateDef.setSigned(false);
        session.save(templateDef);

        templateDef = new TemplateDef();
        templateDef.setType("manual-review");
        templateDef.setAddressees(new HashSet<String>(Arrays.asList("IANA")));
        templateDef.setSubject("Request {ticket} for domain {domainName} in manual review state");
        templateDef.setContent(
                "IANA staff:\n" +
                "\n" +
                "There is a new request created by {submitter} for domain {domainName}.\n" +
                "Domain has special review flag set {specialReviewFlag}.\n" +
                "Domain special instructions:\n" +
                "{specialInstructions}\n" +
                "\n" +
                "Request change data:\n" +
                "{changes}");
        templateDef.setSigned(false);
        session.save(templateDef);

    }

    public static void main(String[] args) {
        InitDatabaseTemplateDefTask task = new InitDatabaseTemplateDefTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
