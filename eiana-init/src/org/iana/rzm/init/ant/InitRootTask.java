package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;

public class InitRootTask extends HibernateTask {


    public void doExecute(Session session) throws Exception {
        RZMUser user = new RZMUser();
        user.setLoginName("root");
        user.setFirstName("root");
        user.setLastName("root");
        user.setPassword("root");
        user.setEmail("root");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        session.save(user);
    }

    public static void main(String[] args) {
        InitRootTask task = new InitRootTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
