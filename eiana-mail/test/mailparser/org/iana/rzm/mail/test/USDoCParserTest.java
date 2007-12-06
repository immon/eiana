package org.iana.rzm.mail.test;

import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.usdoc.USDoCAnswer;
import org.iana.rzm.mail.processor.usdoc.USDoCAnswerParser;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class USDoCParserTest {

    USDoCAnswerParser parser;

    @BeforeClass
    public void init() {
        Map<String, Integer> nsSubjectGroups = new HashMap<String, Integer>();
        nsSubjectGroups.put(USDoCAnswerParser.TICKET_ID, 2);
        nsSubjectGroups.put(USDoCAnswerParser.EPP_ID, 3);
        nsSubjectGroups.put(USDoCAnswerParser.DOMAIN_NAME, 4);
        String nsSubjectPattern = "([^:]*:)*\\s*\\[Root change ([\\d]+):([^\\]]+)\\] Name server change to ([^\\s]+)";
        RegexParser nsChangeParser = new RegexParser(nsSubjectGroups, nsSubjectPattern);

        Map<String, Integer> databaseSubjectGroups = new HashMap<String, Integer>();
        databaseSubjectGroups.put(USDoCAnswerParser.TICKET_ID, 2);
        databaseSubjectGroups.put(USDoCAnswerParser.DOMAIN_NAME, 3);
        String databaseSubjectPattern = "([^:]*:)*\\s*\\[Root change ([\\d]+)] Database change to ([^\\s]+)";
        RegexParser databaseChangeParser = new RegexParser(databaseSubjectGroups, databaseSubjectPattern);

        Map<String, Integer> contentGroups = new HashMap<String, Integer>();
        contentGroups.put(USDoCAnswerParser.ACCEPT, 1);
        contentGroups.put(USDoCAnswerParser.CHANGE_SUMMARY, 2);
        String contentPattern = ".+Authorized: (yes|no).+\\[\\+\\] Begin Change Request Summary: DO NOT EDIT BELOW(.+)\\[-\\] End Change Request Summary: DO NOT EDIT ABOVE.*";
        RegexParser contentParser = new RegexParser(contentGroups, contentPattern);

        parser = new USDoCAnswerParser(nsChangeParser, databaseChangeParser, contentParser, "yes");
    }

    @Test
    public void testValidNameserverAccept() throws Exception {
        String subject = "Re: [Root change 11:22] Name server change to us";
        String content = "Content " +
                "Authorized: yes\n\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW" +
                "Change-change-change " +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE";
        USDoCAnswer answer = (USDoCAnswer) parser.parse(subject, content);
        assert 11 == answer.getTicketID();
        assert "22".equals(answer.getEppID());
        assert "Change-change-change ".equals(answer.getChangeSummary());
        assert answer.isAccept();
        assert answer.isNameserverChange();
    }

    @Test
    public void testValidNameserverDecline() throws Exception {
        String subject = "Re: [Root change 11:22] Name server change to us";
        String content = "Content " +
                "Authorized: no\n\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW" +
                "Change-change-change " +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE";
        USDoCAnswer answer = (USDoCAnswer) parser.parse(subject, content);
        assert 11 == answer.getTicketID();
        assert "22".equals(answer.getEppID());
        assert "Change-change-change ".equals(answer.getChangeSummary());
        assert !answer.isAccept();
        assert answer.isNameserverChange();
    }

    @Test(expectedExceptions = EmailParseException.class)
    public void testInvalidNameserverAccept() throws Exception {
        String subject = "Re: [Root change 11:22] Name server change to us";
        String content = "Content " +
                "Authorized: I ACCEPT\n\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW" +
                "Change-change-change " +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE";
        USDoCAnswer answer = (USDoCAnswer) parser.parse(subject, content);
    }

    @Test
    public void testValidDatabaseAccept() throws Exception {
        String subject = "Re: [Root change 11] Database change to us";
        String content = "Content " +
                "Authorized: yes\n\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW" +
                "Change-change-change " +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE";
        USDoCAnswer answer = (USDoCAnswer) parser.parse(subject, content);
        assert 11 == answer.getTicketID();
        assert null == answer.getEppID();
        assert "Change-change-change ".equals(answer.getChangeSummary());
        assert answer.isAccept();
        assert !answer.isNameserverChange();
    }

    @Test
    public void testValidDatabaseDecline() throws Exception {
        String subject = "Re: [Root change 11] Database change to us";
        String content = "Content " +
                "Authorized: no\n\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW" +
                "Change-change-change " +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE";
        USDoCAnswer answer = (USDoCAnswer) parser.parse(subject, content);
        assert 11 == answer.getTicketID();
        assert null == answer.getEppID();
        assert "Change-change-change ".equals(answer.getChangeSummary());
        assert !answer.isAccept();
        assert !answer.isNameserverChange();
    }

    @Test(expectedExceptions = EmailParseException.class)
    public void testInvalidDatabaseSubject() throws Exception {
        String subject = "Re: [Root change 11:22] Database change to us";
        String content = "Content " +
                "Authorized: yes\n\n" +
                "[+] Begin Change Request Summary: DO NOT EDIT BELOW" +
                "Change-change-change " +
                "[-] End Change Request Summary: DO NOT EDIT ABOVE";
        USDoCAnswer answer = (USDoCAnswer) parser.parse(subject, content);
    }

}
