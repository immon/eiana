package org.iana.dns.validator;

import static org.iana.dns.validator.InvalidDomainNameException.Reason.*;

import java.util.Arrays;
import java.util.List;

/**
 * It validates whether a given name is a valid domain name according to RFC 1034 syntax or fully qualified domain name.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class DomainNameValidator {

    private static String DOMAIN_PATTERN = "([A-Za-z0-9\\-]+\\.)*[A-Za-z0-9\\-]+";

    public static void validateName(String name) throws InvalidDomainNameException {
        if (name == null) throw new InvalidDomainNameException(name, NULL_NAME);

        if (name.endsWith(".")) name = name.substring(0, name.length()-1);

        if (!name.matches(DOMAIN_PATTERN)) throw new InvalidDomainNameException(name, PATTERN_MISMATCH);

        if (name.length() > 254) throw new InvalidDomainNameException(name, NAME_TOO_LONG);

        List<String> pieces = Arrays.asList(name.split("\\."));
        for (String piece : pieces) {
            if (piece.length() > 63)
                throw new InvalidDomainNameException(name, LABEL_TOO_LONG);
            if (piece.length() == 0)
                throw new InvalidDomainNameException(name, LABEL_EMPTY);
            if (!Character.isLetter(piece.charAt(0)))
                throw new InvalidDomainNameException(name, LABEL_FIRT_CHAR_NOT_LETTER);
            if (!Character.isLetterOrDigit(piece.charAt(piece.length() - 1)))
                throw new InvalidDomainNameException(name, LABEL_LAST_CHAR_NOT_LETTER_OR_DIGIT);
        }
    }
}
