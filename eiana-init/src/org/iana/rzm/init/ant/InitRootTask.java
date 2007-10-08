package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.init.ant.decorators.DomainDecorator;
import org.iana.rzm.init.ant.decorators.DomainRegistryDecorator;
import org.iana.rzm.domain.NameServerAlreadyExistsException;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.validator.InvalidDomainNameException;

import java.util.List;
import java.util.Locale;
import java.io.*;
import java.net.MalformedURLException;

import pl.nask.xml.dynamic.exceptions.DynaXMLException;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.DynaXMLParser;

public class InitRootTask extends HibernateTask {

    public void doExecute(Session session) throws Exception {
        RZMUser user = new RZMUser();
        user.setLoginName("root");
        user.setFirstName("root");
        user.setLastName("root");
        user.setPassword("root");
        user.setEmail("root");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        session.save(user);

        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");

        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            String domain = domainDecorator.getDomain().getName();
            System.out.print("\n     ----- TLD: " + domain + " -----\n");

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

            domainManager.create(domainDecorator.getDomain());
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);

        InitRootTask task = new InitRootTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }


    public List<DomainDecorator> getDomainsFromXML() throws DynaXMLException, FileNotFoundException, UnsupportedEncodingException {
        Environment env = DPConfig.getEnvironment("test-data.properties");
        DynaXMLParser parser = new DynaXMLParser();
        DomainRegistryDecorator drd = (DomainRegistryDecorator) parser.fromXML(createReader("domain-whois-root-v2.xml"), env);
        return drd.getDomains();
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

}
