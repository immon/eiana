package org.iana.rzm.init.ant;

import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * It deploys all the needed jBPM process definitions.
 *
 * @author Patrycja Wegrzynowicz
 */
@SuppressWarnings("unchecked")
public class ProcessDeployment {
    public static void main(String[] args) {
        ApplicationContext context = SpringInitContext.getContext();
        JbpmConfiguration jbpmCfg = (JbpmConfiguration) context.getBean("jbpmConfiguration");
        JbpmContext jbpmCtx = jbpmCfg.createJbpmContext();
        try {
            // delete all process instances
            GraphSession graph = jbpmCtx.getGraphSession();
            for (ProcessDefinition pd : (List<ProcessDefinition>) graph.findAllProcessDefinitions()) {
                for (ProcessInstance pi : (List<ProcessInstance>) graph.findProcessInstances(pd.getId())) {
                    graph.deleteProcessInstance(pi.getId());
                }
            }

            // deploy new processes
            jbpmCtx.deployProcessDefinition(DefinedTestProcess.getDefinition());
            jbpmCtx.deployProcessDefinition(DefinedTestProcess.getDefinition(DefinedTestProcess.MAILS_RECEIVER));
            jbpmCtx.deployProcessDefinition(DefinedTestProcess.getDefinition(DefinedTestProcess.EPP_POLL_PROCESS));

            // run new instances
            ProcessInstance pi = jbpmCtx.newProcessInstance("Mails Receiver");
            pi.signal();

            pi = jbpmCtx.newProcessInstance("Notifications reSender");
            pi.signal();            

            pi = jbpmCtx.newProcessInstance("EPP Poll Process");
            pi.signal();
        } finally {
            jbpmCtx.close();
        }
    }
}
