package org.iana.rzm.init.ant;

import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.dao.JbpmProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.jbpm.JbpmContextFactory;
import org.iana.rzm.trans.jbpm.JbpmContextFactoryBean;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * It deploys all the needed jBPM process definitions (at this moment it's only the unified workflow process definition).
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
            jbpmCtx.deployProcessDefinition(DefinedTestProcess.getDefinition(DefinedTestProcess.NOTIFICATION_RESENDER));

            // run new instances
            ProcessInstance pi = jbpmCtx.newProcessInstance("Mails Receiver");
            pi.signal();

            pi = jbpmCtx.newProcessInstance("Notifications reSender");
            pi.signal();            
        } finally {
            jbpmCtx.close();
        }
    }
}
