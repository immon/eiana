package org.iana.rzm.init.ant;

import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.springframework.context.ApplicationContext;

/**
 * It deploys all the needed jBPM process definitions.
 *
 * @author Patrycja Wegrzynowicz
 */
@SuppressWarnings("unchecked")
public class ProcessDeployment {
    public static void main(String[] args) {
        ApplicationContext context = SpringInitContext.getContext();
        ProcessDAO processDAO = (ProcessDAO) context.getBean("processDAO");

        processDAO.deleteAll();

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.deploy(DefinedTestProcess.getDefinition(DefinedTestProcess.MAILS_RECEIVER));
        processDAO.deploy(DefinedTestProcess.getDefinition(DefinedTestProcess.EPP_POLL_PROCESS));

        processDAO.newProcessInstanceAndSignal("Mails Receiver");
        processDAO.newProcessInstanceAndSignal("EPP Poll Process");

    }
}
