/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.conf;

import org.jbpm.graph.def.ProcessDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;

public class DefinedTestProcess {

    public static final String PROCESS_DEFINITION_FILE = "domain-modification.xml";
    public static final String MAILS_RECEIVER = "mails-receiver.xml";
    public static final String NOTIFICATION_RESENDER = "notifications-resender.xml";
    public static final String DOMAIN_CREATION = "domain-creation.xml";

    private static ProcessDefinition pd;
    private static String processName;

    public static ProcessDefinition getDefinition()  {
        return getDefinition(PROCESS_DEFINITION_FILE);
    }

    public static ProcessDefinition getDefinition(String filename)  {
        InputStream inputStream = DefinedTestProcess.class.getClassLoader().getResourceAsStream(filename);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        pd = ProcessDefinition.parseXmlReader(inputStreamReader);
        processName = pd.getName();
        return pd;
    }

    public static String getProcessName() {
        if (pd == null)
            getDefinition();
        return processName;
    }
}
