package org.iana.notifications;

import org.testng.annotations.Test;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"accuracy", "eiana-notifications"})
public class TemplateContentConverterTest {

    @Test
    public void testTemplateContentConverter() throws Exception {

        Map<String, String> valuesMap = new HashMap<String, String>();

        valuesMap.put("testText", "some sample text");
        valuesMap.put("testNull", null);
        valuesMap.put("testDate", "123456789");

        String convertedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(new Long("123456789")));

        TemplateContent templateContent = new TemplateContent("SAMPLE_TEMPLATE3");
        templateContent.setValues(valuesMap);

        TemplateContentConverter tcc = new TemplateContentConverter();

        assert tcc.createSubject(templateContent).equals("Sample subject some sample text");
        assert tcc.createBody(templateContent).equals("Sample text some sample text, Null -- and date " + convertedDate + ".");

    }
}
