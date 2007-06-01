package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.domain.*;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;

import java.net.MalformedURLException;

/**
 * @author Jakub Laszkiewicz
 */
public class InitDatabaseTask extends HibernateTask {
    private Address setupAddress(Address address, String prefix, String countryCode) {
        address.setTextAddress(prefix + " text address");
        address.setCountryCode(countryCode);
        return address;
    }

    private Contact setupContact(Contact contact, String prefix, String domainName, String countryCode) {
        contact.setName(prefix + " name");
        contact.addAddress(setupAddress(new Address(), "contact", countryCode));
        contact.addEmail(prefix + "@no-mail." + domainName);
        contact.addFaxNumber("+1234567890");
        contact.addPhoneNumber("+1234567891");
        return contact;
    }

    public static Host setupHost(Host host) throws InvalidIPAddressException {
        host.setName(host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv4Address("5.6.7.8"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        host.addIPAddress(IPAddress.createIPv6Address("CDEF::1234:5678"));
        return host;
    }

    private Domain setupDomain(String name) throws MalformedURLException, InvalidIPAddressException, NameServerAlreadyExistsException {
        Domain domain = new Domain(name);
        domain.setRegistryUrl("http://www.registry." + name);
        domain.setSpecialInstructions(name + " special instructions");
//        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(setupContact(new Contact(), "supporting-org", name, "US"));
        domain.setWhoisServer("whois." + name);
        domain.addAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupHost(new Host("ns1." + name)));
        domain.addNameServer(setupHost(new Host("ns2." + name)));
        domain.addTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }

    private RZMUser setupUser(RZMUser user, String name) {
        user.setEmail(name + "-@no-mail.org");
        user.setFirstName(name);
        user.setLastName(name);
        user.setLoginName(name);
        user.setOrganization(name);
        user.setPassword(new MD5Password(name));
        user.setSecurID(false);
        return user;
    }

    private SystemRole setupSystemRole(SystemRole role, String name, boolean acceptFrom,
                                       boolean mustAccept, boolean notify) {
        role.setName(name);
        role.setAcceptFrom(acceptFrom);
        role.setMustAccept(mustAccept);
        role.setNotify(notify);
        return role;
    }

    private RZMUser setupSystemUser(String name, SystemRole role) {
        RZMUser user = new RZMUser();
        user.setEmail(name + "@no-mail.org");
        user.setFirstName(name + " first name");
        user.setLastName(name + " last name");
        user.setLoginName(name);
        user.setOrganization(name + " organization");
        user.setPassword(new MD5Password(name + "password"));
        user.setSecurID(false);
        user.addRole(role);
        return user;
    }

    private static String[] domains = {"aero", "biz", "com", "coop", "info", "jobs", "museum", "name", "net", "org", "pro", "travel", "gov", "edu", "mil", "int", "ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "az", "ax", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cr", "cs", "cu", "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "eh", "er", "es", "et", "eu", "fi", "fj", "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "st", "sv", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "um", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "ye", "yt", "yu", "za", "zm", "zw"};


    public void doExecute(Session session) throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        RZMUser iana = setupUser(new RZMUser(), "iana");
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        session.save(iana);

        RZMUser govOversight = setupUser(new RZMUser(), "gov_oversight");
        govOversight.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        session.save(govOversight);

        RZMUser zonePublisher = setupUser(new RZMUser(), "zone_publisher");
        zonePublisher.addRole(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        session.save(zonePublisher);

        for (String domain: domains) {
            session.save(setupSystemUser(domain + "-ac1", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.AC), domain, true, true, true)));
            session.save(setupSystemUser(domain + "-ac2", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.AC), domain, true, false, true)));
            session.save(setupSystemUser(domain + "-ac3", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.AC), domain, false, false, true)));

            session.save(setupSystemUser(domain + "-so1", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.SO), domain, true, true, true)));
            session.save(setupSystemUser(domain + "-so2", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.SO), domain, true, false, true)));
            session.save(setupSystemUser(domain + "-so3", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.SO), domain, false, false, true)));

            session.save(setupSystemUser(domain + "-tc1", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.TC), domain, true, true, true)));
            session.save(setupSystemUser(domain + "-tc2", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.TC), domain, true, false, true)));
            session.save(setupSystemUser(domain + "-tc3", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.TC), domain, false, false, true)));

            session.save(setupDomain(domain));
        }

    }



    public static void main(String[] args) {
        InitDatabaseTask task = new InitDatabaseTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
