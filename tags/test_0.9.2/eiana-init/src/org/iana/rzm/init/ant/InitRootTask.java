package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.config.Config;
import org.iana.config.Parameter;
import org.iana.config.impl.SingleParameter;
import org.iana.rzm.domain.*;
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

    public void doExecute(Session session) throws Exception {
        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");
        RZMUser user = createUser("root", "root", "root", "names&numbers", "simon.raveh@icann.org", new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user =
            createUser("naelaAdmin",
                       "Naela",
                       "Sarras",
                       "naelaAdmin",
                       "naela.sarras@icann.org",
                       new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user =
            createUser("doc",
                       "doc",
                       "doc",
                       "doc",
                       "iana.doc@gmail.com",
                       new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(user);
        user =
            createUser("verisign",
                       "verisign",
                       "verisign",
                       "verisign",
                       "iana.verisign@gmail.com",
                       new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        userManager.create(user);


        //SystemRole systemRole = new SystemRole(SystemRole.SystemType.AC);
        //systemRole.setNotify(true);
        //systemRole.setAcceptFrom(true);
        //systemRole.setName("example");
        //user = createUser("naelas", "Naela", "Sarras", "naelas", "naela.sarras@icann.org", systemRole);
        //userManager.create(user);
        //
        //systemRole = new SystemRole(SystemRole.SystemType.AC);
        //systemRole.setNotify(true);
        //systemRole.setAcceptFrom(true);
        //systemRole.setName("int");
        //user = createUser("sraveh", "Simon", "Raveh", "sraveh", "sraveh@gmail.com", systemRole);
        //userManager.create(user);
        //
        //systemRole = new SystemRole(SystemRole.SystemType.AC);
        //systemRole.setNotify(true);
        //systemRole.setAcceptFrom(true);
        //systemRole.setName("int");
        //user = createUser("naelas", "Naela", "Sarras", "naelas", "naela.sarras@icann.org", systemRole);
        //userManager.create(user);

        DomainManager domainManager = (DomainManager) SpringInitContext.getContext().getBean("domainManager");
        //IanaTesters testers = new IanaTesters();
        //testers.doExecute(SpringInitContext.getContext());



        for (DomainDecorator domainDecorator : getDomainsFromXML()) {
            Domain domain = domainDecorator.getDomain();
//            List<IanaTester> list = testers.getTestersForDomain(domain.getName());
//            domain.setEnableEmails(list.size() > 0);

            //for (IanaTester ianaTester : list) {
            //    List<TesterRole> roles = ianaTester.getRoles();
            //    for (TesterRole role : roles) {
            //        if(role.getDomain().equals(domain.getName())){
            //            if(role.getType().equals("ac")){
            //                domain.getAdminContact().setEmail(role.getEmail());
            //            }else{
            //                domain.getTechContact().setEmail(role.getEmail());
            //            }
            //        }
            //    }
            //}

            domainManager.create(domain);
        }

        //createExampleDomain(domainManager);
        setupConfigParams(session);
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
        user.setPasswordExDate(new Timestamp(System.currentTimeMillis()-1));
        user.setEmail(email);
        user.addRole(role);
        return user;
    }

    private void setupConfigParams(Session session) {
        String subConfigName = "NotificationSenderBean.";
        SingleParameter singleParam;
        singleParam = new SingleParameter(subConfigName + "emailMailhost", "localhost");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter(subConfigName + "emailMailer", "[RZM]");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        singleParam = new SingleParameter(subConfigName + "emailFromAddress", "rzm@iana.org");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
        //singleParam = new SingleParameter(subConfigName + "emailUserName", "");
        //singleParam.setOwner(Config.DEFAULT_OWNER);
        //singleParam.setFromDate(System.currentTimeMillis());
        //singleParam.setToDate(System.currentTimeMillis() + ConfigParameter.DAY);
        //session.save(singleParam);
        //singleParam = new SingleParameter(subConfigName + "emailUserPassword", "");
        //singleParam.setOwner(Config.DEFAULT_OWNER);
        //singleParam.setFromDate(System.currentTimeMillis());
        //singleParam.setToDate(System.currentTimeMillis() + ConfigParameter.DAY);
        //session.save(singleParam);

        singleParam = new SingleParameter(subConfigName + "mailsmtpFrom", "rt-sender@rs.icann.org");
        singleParam.setOwner(Config.DEFAULT_OWNER);
        singleParam.setFromDate(System.currentTimeMillis());
        singleParam.setToDate(System.currentTimeMillis() + Parameter.DAY);
        session.save(singleParam);
    }

    private void createExampleDomain(DomainManager domainManager) {
        Domain d = new Domain("example");
        d.setAdminContact(setupContact(new Contact(), "Naela Sarras", "simon.raveh@icann.org", "US"));
        d.setTechContact(setupContact(new Contact(), "Naela Sarras", "simon.raveh@icann.org", "US"));
        d.setSupportingOrg(setupContact(new Contact(), "Naela Sarras", "simon.raveh@icann.org", "US"));
        Host ns1 = new Host("ns1.example.test.com");
        Host ns2 = new Host("ns2.example.test.com");
        ns1.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        ns1.addIPAddress(IPAddress.createIPv4Address("5.6.7.8"));
        ns2.addIPAddress(IPAddress.createIPv4Address("1.2.3.5"));
        ns2.addIPAddress(IPAddress.createIPv4Address("5.6.7.9"));
        d.addNameServer(ns1);
        d.addNameServer(ns2);
        d.setStatus(Domain.Status.ACTIVE);
        d.setWhoisServer("whois.example.com");
        d.setRegistryUrl("http://www.registry.example.com");
        d.setEnableEmails(true);
        domainManager.create(d);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        InitRootTask task = new InitRootTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }

    private Address setupAddress(Address address, String prefix, String countryCode) {
        address.setTextAddress(prefix + " text address");
        address.setCountryCode(countryCode);
        return address;
    }

    private Contact setupContact(Contact contact, String name, String email, String countryCode) {
        contact.setName(name);
        contact.setOrganization("TestOrg");
        contact.setAddress(setupAddress(new Address(), "contact", countryCode));
        contact.setEmail(email);
        contact.setFaxNumber("+1234567890");
        contact.setPhoneNumber("+1234567891");
        return contact;
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
