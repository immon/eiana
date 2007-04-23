package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.rzm.domain.*;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.apache.tools.ant.BuildException;

import java.net.MalformedURLException;

/**
 * @author Jakub Laszkiewicz
 */
public class InitDomainsTask extends HibernateTask {
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

    public static Host setupHost(Host host) throws InvalidIPAddressException, InvalidNameException {
        host.setName(host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv4Address("5.6.7.8"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        host.addIPAddress(IPAddress.createIPv6Address("CDEF::1234:5678"));
        return host;
    }

    private Domain setupDomain(String name) throws MalformedURLException, InvalidNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        Domain domain = new Domain(name);
        domain.setRegistryUrl("http://www.registry." + name);
        domain.setSpecialInstructions(name + " special instructions");
        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(setupContact(new Contact(), "supporting-org", name, "US"));
        domain.setWhoisServer("whois." + name);
        domain.addAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupHost(new Host("ns1." + name)));
        domain.addNameServer(setupHost(new Host("ns2." + name)));
        domain.addTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }
    
    private static String[] domains = {"aero", "biz", "com", "coop", "info", "jobs", "museum", "name", "net", "org", "pro", "travel", "gov", "edu", "mil", "int", "ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "az", "ax", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cr", "cs", "cu", "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "eh", "er", "es", "et", "eu", "fi", "fj", "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "st", "sv", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "um", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "ye", "yt", "yu", "za", "zm", "zw"};

    public void doExecute(Session session) throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        for (int i = 1; i < domains.length; i++) {
            Domain domain = setupDomain(domains[i]);
            session.save(domain);
        }
    }

    public static void main(String[] args) {
        InitDomainsTask task = new InitDomainsTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
