package org.iana.rzm.mail.test;

import org.iana.rzm.mail.processor.regex.RegexParser;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
@SuppressWarnings("unchecked")
public class RegexParserTest {

    @Test
    public void testValidInputCaseSensitiveNoGroups() throws Exception {
        RegexParser p = new RegexParser(Collections.EMPTY_MAP, "test", true);
        RegexParser.Tokens tokens = p.parse("test");
    }

    @Test(expectedExceptions = ParseException.class)
    public void testInvalidInputCaseSensitiveNoGroups() throws Exception {
        RegexParser p = new RegexParser(Collections.EMPTY_MAP, "test", true);
        RegexParser.Tokens tokens = p.parse("TEST");
    }

    @Test
    public void testValidInputCaseInsensitiveNoGroups() throws Exception {
        RegexParser p = new RegexParser(Collections.EMPTY_MAP, "test");
        RegexParser.Tokens tokens = p.parse("test");
    }

    @Test
    public void testValidInput2CaseInsensitiveNoGroups() throws Exception {
        RegexParser p = new RegexParser(Collections.EMPTY_MAP, "test");
        RegexParser.Tokens tokens = p.parse("TEST");
    }

    @Test(expectedExceptions = ParseException.class)
    public void testInvalidInputCaseInsensitiveNoGroups() throws Exception {
        RegexParser p = new RegexParser(Collections.EMPTY_MAP, "test");
        RegexParser.Tokens tokens = p.parse("TESx");
    }

    @Test
    public void testNonExistentToken() throws Exception {
        RegexParser p = new RegexParser(Collections.EMPTY_MAP, "test");
        RegexParser.Tokens tokens = p.parse("TESt");
        String token = tokens.token("any token");
        assert token == null;
    }

    @Test
    public void testEmptyGroup() throws Exception {
        Map<String, Integer> groups = new HashMap<String, Integer>();
        groups.put("1", 1);
        RegexParser p = new RegexParser(groups, "(.*)test");
        RegexParser.Tokens tokens = p.parse("TESt");
        String token1 = tokens.token("1");
        assert "".equals(token1);
    }

    @Test
    public void test1Group() throws Exception {
        Map<String, Integer> groups = new HashMap<String, Integer>();
        groups.put("1", 1);
        RegexParser p = new RegexParser(groups, "(.*)test");
        RegexParser.Tokens tokens = p.parse("xxxTESt");
        String token1 = tokens.token("1");
        assert "xxx".equals(token1);
    }

    @Test
    public void test2Groups() throws Exception {
        Map<String, Integer> groups = new HashMap<String, Integer>();
        groups.put("group2", 1);
        groups.put("group1", 2);
        RegexParser p = new RegexParser(groups, "(.*)test(abc)");
        RegexParser.Tokens tokens = p.parse("xxxTEStabc");
        String token1 = tokens.token("group2");
        String token2 = tokens.token("group1");
        assert "xxx".equals(token1);
        assert "abc".equals(token2);
    }

}
