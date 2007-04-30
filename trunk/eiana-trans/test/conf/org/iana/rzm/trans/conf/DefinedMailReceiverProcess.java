package org.iana.rzm.trans.conf;

import org.jbpm.graph.def.ProcessDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Jakub Laszkiewicz
 */
public class DefinedMailReceiverProcess {
    private static final String PROCESS_DEFINITION_FILE = "mails-receiver.xml";
    private static ProcessDefinition pd;
    private static String processName;

    public static ProcessDefinition getDefinition()  {
        InputStream inputStream = DefinedTestProcess.class.getClassLoader().getResourceAsStream(PROCESS_DEFINITION_FILE);
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
