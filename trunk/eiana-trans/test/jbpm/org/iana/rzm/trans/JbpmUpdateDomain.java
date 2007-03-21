/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans;

import org.springframework.context.ApplicationContext;

public interface JbpmUpdateDomain {
    void setContext(ApplicationContext ctx);
    boolean doUpdate() throws Exception;
}
