package org.iana.rzm.init.ant;

import org.iana.rzm.domain.*;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.init.ant.decorators.DomainRegistryDecorator;
import org.iana.rzm.init.ant.decorators.DomainDecorator;
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
        DomainRegistryDecorator drd = (DomainRegistryDecorator) parser.fromXML(createReader("test-data.xml"), env);
        return drd.getDomains();
    }
    public void doExecute(Session session) throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException, DynaXMLException, FileNotFoundException, UnsupportedEncodingException {
        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");
        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            System.out.print("\n     ----- TLD: " + domainDecorator.getDomain().getName() + " -----\n");
            domainManager.create(domainDecorator.getDomain());
        }
        System.out.print("\n ALL OK");
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        
        InitDatabaseFromXMLTask task = new InitDatabaseFromXMLTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
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
