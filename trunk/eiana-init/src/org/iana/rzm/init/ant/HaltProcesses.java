package org.iana.rzm.init.ant;

import org.iana.rzm.trans.dao.ProcessDAO;
import org.springframework.context.ApplicationContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HaltProcesses {

    public static void main(String[] args) {
        ApplicationContext context = SpringInitContext.getContext();
        ProcessDAO processDAO = (ProcessDAO) context.getBean("processDAO");
        processDAO.deleteHelperProcesses();
    }

}
