package org.iana.rzm.web.services;

import org.apache.hivemind.lib.SpringBeanFactoryHolder;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.common.email.EmailServiceBean;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: simon
 * Date: May 3, 2007
 * Time: 11:01:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmailServiceImpl implements EmailService {

    private EmailServiceBean emailService;

    public void sendLogMessage(String msg, Throwable value) {

        StringWriter writer = new StringWriter();
        value.printStackTrace(new PrintWriter(writer));
        StringBuilder builder = new StringBuilder();

        builder.append("RZM Exception").append("\n");
        builder.append(msg).append("\n");
        builder.append("Exception Information").append("\n");
        builder.append(writer.toString()).append("\n");

        try {
            emailService.sendEmail("system-errors@iana.org", "RZM Exception", builder.toString() );
        } catch (InfrastructureException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

     public void setBeanFactoryHolder(SpringBeanFactoryHolder beanFactoryHolder) {
        emailService = (EmailServiceBean) beanFactoryHolder.getBeanFactory().getBean("emailService");

    }
}
