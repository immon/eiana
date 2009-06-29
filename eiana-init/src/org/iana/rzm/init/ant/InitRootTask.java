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
import java.util.List;
import java.util.Locale;

public class InitRootTask extends HibernateTask {

    public void doExecute(Session session) throws Exception {
        UserManager userManager = (UserManager) SpringInitContext.getContext().getBean("userManager");
        RZMUser user =
                createUser("root", "Simon", "Raveh", "names&numbers", "simon.raveh@icann.org", new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user = createUser("naelaAdmin",
                        "Naela",
                        "Sarras",
                        "naelaAdmin",
                        "naela.sarras@icann.org",
                        new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user = createUser("kimAdmin",
                        "Kim",
                        "Davies",
                        "kimAdmin",
                        "kim.davies@icann.org",
                        new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        user = createUser("usdoc",
                        "USDoC",
                        "USDoC",
                        "usdoc",
                        "zonemod-test@ntia.doc.gov",
                        new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        user.setPublicKey("-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "Version: GnuPG v1.4.7 (Darwin)\n" +
                "\n" +
                "mQENBEo3wdcBCACff9C30kpCPqRROAACTFirICyyK+YgWu7gzr2tVk5tVNFXWZtT\n" +
                "sxXwBgk/yYcjViUQ2pvqXd2j8W4jMdZ59ZULSRxmPNb8doC3k6ND2rHMK8RdfpoG\n" +
                "1j1q70Pzu3dByDyVBbHiTjNUy4/flP9THq2SiINkHUaQPMmoa+vsg1/Y6PpRmcfr\n" +
                "xt9XKkaNQUn6HD3wWoL8nMOzh/ugfi8dxglKVwb7CrysgvnwFSlfWP+tFZUB/HER\n" +
                "TOhAk/qR0uHu3zJir2Bw880LCcLudsux9WQxGmf3pL6AVSK8TPYiyJoIVSNHR7Ya\n" +
                "gyOXARJvIdqs7OWDtsDMj8d7N1A07yE8f1zdABEBAAG0KFpvbmVtb2QtVGVzdCA8\n" +
                "em9uZW1vZC10ZXN0QG50aWEuZG9jLmdvdj6JAXQEEAECAF4FAko3wdcFCQWjmoAw\n" +
                "FIAAAAAAIAAHcHJlZmVycmVkLWVtYWlsLWVuY29kaW5nQHBncC5jb21wZ3BtaW1l\n" +
                "BwsJCAcDAgoCGQEFGwMAAAADFgIBBR4BAAAABRUCCAkKAAoJEPEdIrlp+HB+3VIH\n" +
                "/09eRxLZYd457B1HMRubDojV7rBjJUzIotKFEQhwqihQbidfoUtr9DZDICw6gIC+\n" +
                "STXnGP+cLqYNog/Otzncb2A4BlNm0pecTFM/LWau3AfUMlMhcV/RYxnh92qAelBt\n" +
                "ua60PrLwfxJIaSD88Vu9qbI19xaLaw0imNEqxLKJliV4P9cmsGEc1T89mZkMIf7L\n" +
                "OPMOAEJCSeHoFih8aQcXJTZq+HJuD6mnIUdbIog0rbXeiWrUwGy427s1T0B4zNDM\n" +
                "WYltPFn/tG6R7QM+xAbJzv2YQFt8Ljpef351ChYUUDUuZMDKqqGKLRpjaVa6kKEc\n" +
                "grL0VGITGNmC4rKNNmGa3HeIRgQQEQIABgUCSjfDMAAKCRDcB4+iybQoSOhrAKCY\n" +
                "qnlJ8YnFIZj6DDdEhyNrbn18xQCgvVUdPpGJN39yD7NUZklogyqHksy5AQ0ESjfB\n" +
                "1wEIAMlzhdOXno59oFOL55cIhsQy26zfpb9qVswaoEbT6WFjMmipNaE/L1goP/r0\n" +
                "W7/jjelehumzyWjXAd12796z3Sq+rPk14jt+ZB5wiKfk3ocdrYq0oZkBkLVatH3E\n" +
                "zM+AIrf8lVR8OhHhjomeMhRdyvj41mcVztHosRlDe+XjCsHzA0QYhxB+D3zgZx7z\n" +
                "IE2vp3NHAnE+EzwzNm75jUk2BB4T3og2tn1sU44nqpx4hiBk7DkhDYJTRAd3ydoe\n" +
                "hyNbDNnAElWI21OshWLkR8BMqylJvTQQrfAx2cykieEm5o9ElawveWRNgrGO2yxM\n" +
                "ybUp6Q7cBv5faMrgwJeopJ53J38AEQEAAYkCRwQYAQIBMQUCSjfB2AUJBaOagAUb\n" +
                "DAAAAMBdIAQZAQgABgUCSjfB1wAKCRD5PEWsIxiv6yqpB/9Z+4NqntrWvagKstV4\n" +
                "XGpvEhpvj2pwCUK8scluuu6r88AGHUHo89bxd15L2z6bv3rR5VKba+UHDreEU19M\n" +
                "PKmkZKS6oDDc242LUEninPCArP5dz/ZJ9VDPz/pPwxIYwKMnkZfr8QYI29kj8NIY\n" +
                "BF7H2UNXk/5b4NzeWK9MoEAPdm5gBIPS/yZrdzStzsXyx1MdfPwHU7yj1uC9+F2I\n" +
                "zowwS/bqCm4sNrzptj6E1d0bOzGoNhVOFq9bOHZsyZzPd1CYxTNg66nyEuWgBp+B\n" +
                "tPySxHlDSWdci8eSEljpNr2RaOGXPBUzuSxiAq2Dbb3u6A8kBi33Am/09e4mmj+P\n" +
                "jSD1AAoJEPEdIrlp+HB+Y9kH/3euZg1SKGYPctHvcH6HDJfXumYrWUGDqrdYEEnD\n" +
                "N2Gv3/mReFwM5D2id95XGEfnIx7fL5CeGwZsctUDcnuxSIOAmrdJBB0FsoNIVHm+\n" +
                "M3ui2JRNHVgNgsxDRvm1GbQhFDz5iAqR69dBfsMlSDjcCsSmiERlD+KCLSCfoiHv\n" +
                "7bZ2DxPeo1tNff7eFXGtg0pZKAmwqfvj6w6UhwQ8x4s6CAWYYJ1uAgSlaja7T8Vx\n" +
                "YaNEY4+NkovL3xDJ5/tqV7Bn9MtczmtNOWu3u6ATmB5EKsFn6sxDp0BPQgi6mWym\n" +
                "5oavO3Ee+dzifrKgVG8YLTo838R8RMR280SXIntYXUKm+48=\n" +
                "=Kypo\n" +
                "-----END PGP PUBLIC KEY BLOCK-----");

        userManager.create(user);
        user = createUser("verisign",
                        "VeriSign",
                        "VeriSign",
                        "verisign",
                        "root-zone-cr@verisign.com",
                        new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));

        user.setPublicKey("-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "Version: GnuPG v1.4.7 (Darwin)\n" +
                "\n" +
                "mQENBEoyV5QBCADOnNGc8RK7DsruOgXoHjVzq9o8XzIPw30562LRyQyiUFfi4hSS\n" +
                "rRnvHdlto18+CiBLDJGdj994J5vyxAsggZpXUV4xG2O08tGGMq0Fki/BHyl4acrW\n" +
                "wYx7RzraXX+Z3KCa7SPKkF5XYlMR/OOd4pc0YvfdRlt+M8Q6p44f8rDQrE54U2Mu\n" +
                "qdQeXP1nGNg/BNoO8qWGJh7neWf0Z94hVQV6cxp/IkZQpYsev2uHtx3tCL4tvcxK\n" +
                "VBX3Bc9DRqrAGYqlMJS0+tlFXtr7oxqEqQE66DhP9m3/I1f7yXrEgoK0fv1tOVj6\n" +
                "UuRuSRbSxTOWgRyxXMLx5ELxOWgd6+n7M27rABEBAAG0LVJaTVMgU3lzdGVtIEVt\n" +
                "YWlsIDxyb290LXpvbmUtY3JAdmVyaXNpZ24uY29tPokBPAQTAQIAJgUCSjJXlAIb\n" +
                "AwUJA8JnAAYLCQgHAwIEFQIIAwQWAgMBAh4BAheAAAoJELz0kGDsrIoEetMIALQ+\n" +
                "Oz56sTdrMXRxrzDYGDTSVduHmV0dQ1MLghkAcq+607CpA3NvQSgqTAYQVdJEIstC\n" +
                "FiAuGdYTVvCa3loEXxp3XckpjHw41cX2DnV1ZKggZ74//Ls22bgVMiN9RHSLCkh6\n" +
                "CzI3c3NGiiUcrro5XoqzdhHN0gAKDPJ1RIqYJwrvEgShQmPR+cqbK92EmwkHb9hC\n" +
                "SMKkgRsUygFElTUw5IIuFhQfxU+G5Cl5ZfcZsnRyTPIhu7FtphKNIdlCOv2ZnJ9F\n" +
                "7VuC/XLiDnJpoFz5YQFk0Pk1y7tRqh/vc4kUmBS6wpOECjbxFFlNzns3DDFsVHis\n" +
                "ZCbNiUF85g/viOPQ90aIRgQQEQIABgUCSjfDSwAKCRDcB4+iybQoSBUnAKCtof+Z\n" +
                "mKy4RjlFmfQ9UIiHNLHvJwCglthDuCtp18RteZWKpcWCHEtWc2GJARwEEAECAAYF\n" +
                "Ako3w74ACgkQuYWDBQMQnYbLSwf/W24Jw4LSGsdP+3LU9C0zvwy99K1voNoOXNWF\n" +
                "ReMOpdNelW9HsvYvZVJwjkEGHLUaeXkPPGXxFJ9cog9H59/q6CK/BdT4nyfNI6y3\n" +
                "pZrA+3/SqzULAk3sINgaw4+geHXUR7I9ZJAuEt1nPoYLc+gk/uNbbL6t4CTgjKy1\n" +
                "WioJ32xqk0PcJUqDGlQyJJpjeG94fRDGv9NgI3pgYukuif4cwBW9rtJ95ug91zVy\n" +
                "1mxU/NHSaXLDqgD0Otx4C4/tu9D0n18jbJDjoVc9I00p2LreF+ta03Pspru8+o7X\n" +
                "aGApcFsy+/yukq6kZLzg/lWRFvJuAPc3CaCcxIA3HiKIXcd1hQ==\n" +
                "=ExKz\n" +
                "-----END PGP PUBLIC KEY BLOCK-----");
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
