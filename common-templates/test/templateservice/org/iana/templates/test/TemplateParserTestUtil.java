package org.iana.templates.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplateParserTestUtil {
    public static final String TEMPLATE_DEF_FILE_NAME = "mail-templates.xml";

    public static String loadFromFile(String fileName) throws IOException {
        DataInputStream dis = new DataInputStream(TemplateParserTestUtil.class.getResourceAsStream(fileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(dis, "US-ASCII"));
        StringBuffer buf = new StringBuffer();
        String line = reader.readLine();
        if (line != null) {
            buf.append(line);
            while ((line = reader.readLine()) != null) {
                buf.append("\n");
                buf.append(line);
            }
        }
        reader.close();
        dis.close();
        return buf.toString();
    }
}
