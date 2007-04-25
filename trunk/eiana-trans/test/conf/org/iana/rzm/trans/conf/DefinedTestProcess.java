/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.conf;

import org.jbpm.graph.def.ProcessDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;

public class DefinedTestProcess {

    private static final String PROCESS_DEFINITION_FILE = "domain-modification.xml";
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
