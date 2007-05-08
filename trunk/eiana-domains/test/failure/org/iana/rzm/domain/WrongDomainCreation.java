package org.iana.rzm.domain;

import org.testng.annotations.Test;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InvalidEmailException;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"wrongDomainCreation", "eiana-domains"})
public class WrongDomainCreation {

    @Test (expectedExceptions = {InvalidNameException.class})
    public void testWrongDomainName() throws Exception {
        Domain domain = new Domain();
        String domainName = "aa#$-()a";
        try {
            domain.setName(domainName);
        } catch (InvalidNameException e) {
            assert e.getName().equals(domainName);
            throw e;
        }
    }
    
   @Test (expectedExceptions = {InvalidEmailException.class})
   public void testWrongContactEmail() throws Exception {
        String emailAddress = "wrong#email";
        try {
            Contact contact = new Contact();
            contact.addEmail(emailAddress);
        } catch (InvalidEmailException e) {
            assert e.getEmail().equals(emailAddress);
            throw e;
        }
    }
}
