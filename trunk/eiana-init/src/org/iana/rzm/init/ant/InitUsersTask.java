package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;

/**
 * @author Jakub Laszkiewicz
 */
public class InitUsersTask extends HibernateTask {
    private RZMUser setupUser(RZMUser user, String name) {
        user.setEmail(name + "-@no-mail.org");
        user.setFirstName(name);
        user.setLastName(name);
        user.setLoginName(name);
        user.setOrganization(name);
        user.setPassword(new MD5Password(name));
        user.setSecurID(false);
        return user;
    }

    public void doExecute(Session session) {
        RZMUser iana = setupUser(new RZMUser(), "iana");
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        session.save(iana);

        RZMUser govOversight = setupUser(new RZMUser(), "gov_oversight");
        govOversight.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        session.save(govOversight);

        RZMUser zonePublisher = setupUser(new RZMUser(), "zone_publisher");
        zonePublisher.addRole(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER));
        session.save(zonePublisher);
    }

    public static void main(String[] args) {
        InitUsersTask task = new InitUsersTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
