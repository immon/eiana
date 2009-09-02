package org.iana.rzm.mail.test;

import org.iana.rzm.mail.processor.contact.ContactAnswer;
import org.iana.rzm.mail.processor.contact.ContactAnswerParser;
import org.iana.rzm.mail.processor.contact.ContactMessageParseException;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class ContactParserTest {

    ContactAnswerParser parser;

    @BeforeClass
    public void init() {
        Map<String, Integer> subjectGroups = new HashMap<String, Integer>();
        subjectGroups.put(ContactAnswerParser.TICKET_ID, 1);
        subjectGroups.put(ContactAnswerParser.TOKEN, 2);
        String subjectPattern = "^.*\\[IANA #(\\d+)].*\\(\\%([a-zA-Z0-9\\+\\/]+)\\)\\s*$";
        RegexParser subjectParser = new RegexParser(subjectGroups, subjectPattern);

        Map<String, Integer> contentGroups = new HashMap<String, Integer>();
        contentGroups.put(ContactAnswerParser.ACCEPT, 1);
        String contentPattern = "(.+)Dear.+";
        RegexParser contentParser = new RegexParser(contentGroups, contentPattern);

        parser = new ContactAnswerParser(subjectParser, contentParser, "I ACCEPT", "I DECLINE");
    }

    @Test
    public void testValidAccept() throws Exception {
        String subject = "[IANA #1] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = "  I accept \n > Dear content...";
        ContactAnswer answer = (ContactAnswer) parser.parse("a@example.tld",  subject, content);
        assert 1 == answer.getTicketID();
        assert "1sa2d3".equals(answer.getToken());
        assert answer.isAccept();
    }

    @Test
    public void testValidDecline() throws Exception {
        String subject = "[IANA #1] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = "  I decline Dear content...";
        ContactAnswer answer = (ContactAnswer) parser.parse("a@example.tld",  subject, content);
        assert 1 == answer.getTicketID();
        assert "1sa2d3".equals(answer.getToken());
        assert !answer.isAccept();
    }

    @Test
    public void testValidRePrefix() throws Exception {
        String subject = "Re: Odp: [IANA #1] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = "  I accept \n > Dear content...";
        ContactAnswer answer = (ContactAnswer) parser.parse("a@example.tld",  subject, content);
        assert 1 == answer.getTicketID();
        assert "1sa2d3".equals(answer.getToken());
        assert answer.isAccept();
    }

    @Test (expectedExceptions = ContactMessageParseException.class)
    public void testInvalidAccept() throws Exception {
        String subject = "[IANA #1] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = "  I'm trying to accept content... Dear sdadas";
        parser.parse("a@example.tld",  subject, content);
    }

    @Test (expectedExceptions = ContactMessageParseException.class)
    public void testInvalidDecline() throws Exception {
        String subject = "[IANA #1] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = " I'm trying to decline content... Dear sdsdsd";
        parser.parse("a@example.tld",  subject, content);
    }

    @Test (expectedExceptions = ContactMessageParseException.class)
    public void testAcceptAndDeclinePresent() throws Exception {
        String subject = "Re: Odp: [IANA #1] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = "  I accept I decline \n > Dear content...";
        parser.parse("a@example.tld",  subject, content);
    }

    @Test(expectedExceptions = EmailParseException.class)
    public void testInvalidTicketID() throws Exception {
        String subject = "Re: [IANA #TicketId] Your confirmation requested to alter ac domain (%1sa2d3)";
        String content = "  I ACCEPT content...";
        ContactAnswer answer = (ContactAnswer) parser.parse("a@example.tld",  subject, content);
    }
}
