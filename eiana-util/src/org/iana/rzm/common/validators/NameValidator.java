package org.iana.rzm.common.validators;

import org.iana.rzm.common.exceptions.InvalidNameException;

import java.util.List;
import java.util.Arrays;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class NameValidator {

    private static String DOMAIN_PATTERN = "([A-Za-z0-9\\-]+\\.)*[A-Za-z0-9\\-]+";
    private static String HOST_PATTERN = "([A-Za-z0-9\\-]+\\.)*[A-Za-z0-9\\-]+\\.?";

    public static void validateName(String name, boolean isHostName) throws InvalidNameException {
        if (isHostName) {
            if (!name.matches(HOST_PATTERN)) throw new InvalidNameException(name, "invalid character in name");
        } else {
            if (!name.matches(DOMAIN_PATTERN)) throw new InvalidNameException(name, "invalid character in name");
        }

        if (name.length() > 254) throw new InvalidNameException(name, "name to long");

        List<String> pieces = Arrays.asList(name.split("\\."));
        for (String piece : pieces) {
            if (piece.length() > 63) throw new InvalidNameException(name, "label to long");
            if (piece.length() == 0) throw new InvalidNameException(name, "null label");
//            if (!Character.isLetter(piece.charAt(0))) throw new InvalidNameException(name, "label must start with a letter");
            if (!Character.isLetterOrDigit(piece.charAt(piece.length()-1))) throw new InvalidNameException(name, "label must end with a letter or digit");
        }
    }
}
