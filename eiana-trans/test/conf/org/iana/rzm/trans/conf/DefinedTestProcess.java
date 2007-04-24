/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.conf;

import org.jbpm.graph.def.ProcessDefinition;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.FileNotFoundException;

public class DefinedTestProcess {

    private static final String PROCESS_DEFINITION_FILE = "eiana-trans/etc/processes/domain-modification.xml";
    private static ProcessDefinition pd;
    private static String processName;

    public static ProcessDefinition getDefinition()  {
        try {
            FileReader fileReader = new FileReader(PROCESS_DEFINITION_FILE);
            pd = ProcessDefinition.parseXmlReader(fileReader);
            processName = pd.getName();
        } catch (FileNotFoundException e) {
            try {
                FileReader fileReader = new FileReader("../" + PROCESS_DEFINITION_FILE);
                pd = ProcessDefinition.parseXmlReader(fileReader);
                processName = pd.getName();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        return pd;
    }

    public static String getProcessName() {
        if (pd == null)
            getDefinition();
        return processName;
    }
}
