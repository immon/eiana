package org.iana.rzm.mail.processor.regex;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RegexParser {

    private Map<String, Integer> tokens = new HashMap<String, Integer>();

    private Pattern pattern;

    public class Tokens {

        private Matcher matcher;

        public Tokens(String input) throws ParseException {
            matcher = pattern.matcher(new StringBuilder(input));
            if (!matcher.matches()) throw new ParseException(input, 0);
        }

        public String token(String name) {
            Integer group = tokens.get(name);
            if (group == null) return null;
            return matcher.group(group);
        }
        
    }

    public RegexParser(Map<String, Integer> tokens, String regex) throws PatternSyntaxException {
        this(tokens, regex, false);
    }

    public RegexParser(Map<String, Integer> tokens, String regex, boolean caseSensitive) throws PatternSyntaxException {
        this(tokens, regex, Pattern.DOTALL | (caseSensitive ? 0 : Pattern.CASE_INSENSITIVE));
    }

    public RegexParser(Map<String, Integer> tokens, String regex, int mode) throws PatternSyntaxException {
        this.tokens = tokens;
        this.pattern = Pattern.compile(regex, mode);
    }

    public Tokens parse(String input) throws ParseException {
        return new Tokens(input);
    }

}
