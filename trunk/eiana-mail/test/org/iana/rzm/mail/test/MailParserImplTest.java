package org.iana.rzm.mail.test;

import org.testng.annotations.Test;
import org.iana.rzm.mail.parser.*;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class MailParserImplTest {
    private static final String VALID_FROM = "mail@no-mail.org";
    private static final Long VALID_TICKET_ID = 123L;
    private static final String VALID_STATE_NAME = "PENDING_CONTACT_CONFIRMATION";
    private static final String VALID_SUBJECT = "Re: " + VALID_TICKET_ID + " | " + VALID_STATE_NAME;
    private static final String VALID_CONTENT =
            "> declaration declaration declaration declaration" +
            "> declaration declaration declaration declaration" +
            "> declaration declaration declaration declaration" +
            "> declaration declaration declaration declaration" +
            "I ACCEPT";

    @Test
    public void testParseConfirmationMail() throws MailParserException {
        MailParser parser = new MailParserImpl();
        MailData result = parser.parse(VALID_FROM, VALID_SUBJECT, VALID_CONTENT);
        assert result instanceof ConfirmationMailData;
        ConfirmationMailData cmd = (ConfirmationMailData) result;
        assert VALID_FROM.equals(cmd.getEmail());
        assert VALID_TICKET_ID.equals(cmd.getTicketId());
        assert VALID_STATE_NAME.equals(cmd.getStateName());
        assert cmd.isAccepted();
    }
}
