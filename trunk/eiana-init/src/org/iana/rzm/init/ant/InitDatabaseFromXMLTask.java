package org.iana.rzm.init.ant;

import org.iana.rzm.domain.*;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.init.ant.decorators.DomainRegistryDecorator;
import org.iana.rzm.init.ant.decorators.DomainDecorator;
import org.iana.rzm.user.*;
import org.iana.dns.validator.InvalidDomainNameException;
import org.hibernate.Session;

import java.net.MalformedURLException;
import java.io.*;
import java.util.Locale;
import java.util.List;

import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

/**
 * @author: Piotr Tkaczyk
 */
public class InitDatabaseFromXMLTask extends HibernateTask {

    public List<DomainDecorator> getDomainsFromXML() throws DynaXMLException, FileNotFoundException, UnsupportedEncodingException {
        Environment env = DPConfig.getEnvironment("test-data.properties");
        DynaXMLParser parser = new DynaXMLParser();
        DomainRegistryDecorator drd = (DomainRegistryDecorator) parser.fromXML(createReader("domain-whois-root.xml"), env);
        return drd.getDomains();
    }

    public void doExecute(Session session) throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException, DynaXMLException, FileNotFoundException, UnsupportedEncodingException {
        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");
        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");

        RZMUser iana = setupUser(new RZMUser(), "iana");
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(iana);

        RZMUser govOversight = setupUser(new RZMUser(), "gov_oversight");
        govOversight.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(govOversight);

        RZMUser zonePublisher = setupUser(new RZMUser(), "zone_publisher");
        zonePublisher.addRole(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        userManager.create(zonePublisher);

        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            String domain = domainDecorator.getDomain().getName();
            System.out.print("\n     ----- TLD: " + domain + " -----\n");

            userManager.create(setupSystemUser(domain + "-ac1", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.AC), domain, true, true, true)));
            userManager.create(setupSystemUser(domain + "-ac2", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.AC), domain, true, false, true)));
            userManager.create(setupSystemUser(domain + "-ac3", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.AC), domain, false, false, true)));

            userManager.create(setupSystemUser(domain + "-so1", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.SO), domain, true, true, true)));
            userManager.create(setupSystemUser(domain + "-so2", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.SO), domain, true, false, true)));
            userManager.create(setupSystemUser(domain + "-so3", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.SO), domain, false, false, true)));

            userManager.create(setupSystemUser(domain + "-tc1", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.TC), domain, true, true, true)));
            userManager.create(setupSystemUser(domain + "-tc2", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.TC), domain, true, false, true)));
            userManager.create(setupSystemUser(domain + "-tc3", setupSystemRole(
                    new SystemRole(SystemRole.SystemType.TC), domain, false, false, true)));

            domainManager.create(domainDecorator.getDomain());
        }
        createUser();
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
                                       boolean mustAccept, boolean notify) throws InvalidDomainNameException {
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
        user.setPassword(name + "password");
        user.setSecurID(false);
        user.addRole(role);
        return user;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);

        InitDatabaseFromXMLTask task = new InitDatabaseFromXMLTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
        System.out.print("\n\n ALL OK \n\n");
    }

    private Reader createReader(String filename) throws UnsupportedEncodingException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (in != null) {
            return new InputStreamReader(in, "UTF-8");
        }
        try {
            return new FileReader(filename);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Unable to open: " + filename);
        }
    }

   private void createUser() {
        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");
        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");
        RZMUser user = new RZMUser();
        user.setFirstName("Simon");
        user.setLastName("Raveh");
        user.setEmail("simon.raveh@icann.org");
        user.setLoginName("simon");
        user.setPassword("simon");
        for (Domain domain : domainManager.findAll()) {
            String domainName = domain.getName();
            user.addRole(new SystemRole(SystemRole.SystemType.AC, domainName, true, false));
            user.addRole(new SystemRole(SystemRole.SystemType.TC, domainName, true, false));
            user.addRole(new SystemRole(SystemRole.SystemType.SO, domainName, true, false));
        }
        userManager.create(user);
    }}
