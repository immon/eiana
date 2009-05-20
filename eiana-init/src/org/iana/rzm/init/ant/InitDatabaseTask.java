package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.config.Config;
import org.iana.config.Parameter;
import org.iana.config.impl.SetParameter;
import org.iana.config.impl.SingleParameter;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.domain.*;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;

import java.net.MalformedURLException;
import java.util.*;

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
        contact.setAddress(setupAddress(new Address(), "contact", countryCode));
        contact.setEmail(prefix + "@no-mail." + domainName);
        contact.setFaxNumber("+1234567890");
        contact.setPhoneNumber("+1234567891");
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
        domain.setAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupHost(new Host("ns1." + name)));
        domain.addNameServer(setupHost(new Host("ns2." + name)));
        domain.setTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }

    private RZMUser setupUser(RZMUser user, String name) {
        user.setEmail(name + "-@no-mail.org");
        user.setFirstName(name);
        user.setLastName(name);
        user.setLoginName(name);
        user.setOrganization(name);
        user.setPassword(name);
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
        user.setPassword(name);
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

        for (String domain : domains) {
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

        //Config
        String subConfigName = "NotificationSenderBean.";
        SingleParameter singleParam;
        singleParam = new SingleParameter(subConfigName + "emailMailhost", "localhostFromConfig");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter(subConfigName + "emailMailer", "[RZM]FromConfig");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter(subConfigName + "emailFromAddress", "rzm-test-from-cofig@iana.org");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter(subConfigName + "emailUserName", "userFromConfig");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter(subConfigName + "emailUserPassword", "passwordFromConfig");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter("usdocEmail", "usdoc@wrong.email.com.tv");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter("usdocPublicKey", "123afe32dc");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter("verisignEmail", "verisign@wrong.email.com.tv");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter("verisignPublicKey", "321afe221dc");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);

        Map<String, Set<String>> rootServers = new HashMap<String, Set<String>>();
        rootServers.put("A.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("198.41.0.4", "2001:503:BA3E:0:0:0:2:30")));
        rootServers.put("H.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("128.63.2.53", "2001:500:1:0:0:0:803F:235")));
        rootServers.put("C.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.33.4.12")));
        rootServers.put("G.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.112.36.4")));
        rootServers.put("F.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.5.5.241", "2001:500:2F:0:0:0:0:F")));
        rootServers.put("B.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.228.79.201")));
        rootServers.put("J.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.58.128.30", "2001:503:C27:0:0:0:2:30")));
        rootServers.put("K.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("193.0.14.129", "2001:7FD:0:0:0:0:0:1")));
        rootServers.put("L.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("199.7.83.42")));
        rootServers.put("M.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("202.12.27.33", "2001:DC3:0:0:0:0:0:35")));
        rootServers.put("I.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.36.148.17")));
        rootServers.put("E.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("192.203.230.10")));
        rootServers.put("D.ROOT-SERVERS.NET.", new HashSet<String>(Arrays.asList("128.8.10.90")));


        SetParameter setParam = new SetParameter("rootServerNames", rootServers.keySet());
        setParam.setOwner(Config.DEFAULT_OWNER);
        setParam.setFromDate(System.currentTimeMillis());
        setParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(setParam);

        for (String rootServerName : rootServers.keySet()) {
            SetParameter sParam = new SetParameter(rootServerName, rootServers.get(rootServerName));
            sParam.setFromDate(System.currentTimeMillis());
            sParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
            session.save(sParam);
        }

    }


    public static void main(String[] args) {
        InitDatabaseTask task = new InitDatabaseTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
