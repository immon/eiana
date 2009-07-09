package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.init.ant.decorators.DomainDecorator;
import org.iana.rzm.init.ant.decorators.DomainRegistryDecorator;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.UserManager;
import pl.nask.xml.dynamic.DynaXMLParser;
import pl.nask.xml.dynamic.config.DPConfig;
import pl.nask.xml.dynamic.env.Environment;
import pl.nask.xml.dynamic.exceptions.DynaXMLException;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

public class InitRootTask extends HibernateTask {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        InitRootTask task = new InitRootTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }

    public List<DomainDecorator> getDomainsFromXML()
            throws DynaXMLException, FileNotFoundException, UnsupportedEncodingException {
        Environment env = DPConfig.getEnvironment("test-data.properties");
        DynaXMLParser parser = new DynaXMLParser();
        DomainRegistryDecorator drd =
                (DomainRegistryDecorator) parser.fromXML(createReader("test-data.xml"), env);
        return drd.getDomains();
    }


    public void doExecute(Session session) throws Exception {

        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");
        RZMUser user = createUser("root", "Simon", "Raveh", "names&numbers", "simon.raveh@icann.org", new AdminRole(AdminRole.AdminType.ROOT));
        userManager.create(user);
        user = createUser("naelaAdmin", "Naela", "Sarras", "naelaAdmin", "naela.sarras@icann.org", new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);
        user = createUser("doc", "doc", "doc", "doc", "iana.doc@gmail.com", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(user);
        user = createUser("verisign", "verisign", "verisign", "verisign", "iana.verisign@gmail.com", new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        userManager.create(user);

        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");


        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            Domain domain = domainDecorator.getDomain();
            domain.setEnableEmails(true);
            domainManager.create(domain);
        }
    }

    private RZMUser createUser(String loginName, String firstName, String lastName, String password, String email, Role role) {
        RZMUser user = new RZMUser();
        user.setLoginName(loginName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setPasswordExDate(new Timestamp(System.currentTimeMillis() - 1));
        user.setEmail(email);
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
