package org.iana.rzm.init.ant;

import org.hibernate.*;
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
        RZMUser user = new RZMUser();
        user.setLoginName("root");
        user.setFirstName("root");
        user.setLastName("root");
        user.setPassword("root");
        user.setEmail("root");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");
        userManager.create(user);

        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");

        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            String domain = domainDecorator.getDomain().getName();
            System.out.print("\n     ----- TLD: " + domain + " -----\n");
            domainManager.create(domainDecorator.getDomain());
        }
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
            (DomainRegistryDecorator) parser.fromXML(createReader("domain-whois-root-v2.xml"), env);
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
