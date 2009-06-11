package org.iana.rzm.init.ant;

import org.hibernate.*;
import org.iana.config.*;
import org.iana.config.impl.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.init.ant.decorators.*;
import org.iana.rzm.user.*;
import pl.nask.xml.dynamic.*;
import pl.nask.xml.dynamic.config.*;
import pl.nask.xml.dynamic.env.*;
import pl.nask.xml.dynamic.exceptions.*;

import java.io.*;
import java.util.*;

public class InitRootTask extends HibernateTask {

    public void doExecute(Session session) throws Exception {
        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");
        RZMUser user =
                createUser("root", "root", "root", "names&numbers", "simon.raveh@icann.org", new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user = createUser("naelaAdmin",
                        "Naela",
                        "Sarras",
                        "naelaAdmin",
                        "naela.sarras@icann.org",
                        new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user = createUser("doc",
                        "doc",
                        "doc",
                        "doc",
                        "iana.doc@gmail.com",
                        new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(user);
        user = createUser("verisign",
                        "verisign",
                        "verisign",
                        "verisign",
                        "iana.verisign@gmail.com",
                        new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        userManager.create(user);

        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");


        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            Domain domain = domainDecorator.getDomain();
            domain.setEnableEmails(true);
            domainManager.create(domain);
        }
    }

    private RZMUser createUser(String loginName,
                               String firstName,
                               String lastName,
                               String password,
                               String email,
                               Role role) {

        RZMUser user = new RZMUser();
        user.setLoginName(loginName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.addRole(role);
        return user;
    }



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
