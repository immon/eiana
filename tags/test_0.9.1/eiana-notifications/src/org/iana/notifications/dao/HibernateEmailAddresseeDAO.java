package org.iana.notifications.dao;

import org.iana.notifications.EmailAddressee;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class HibernateEmailAddresseeDAO extends HibernateDaoSupport implements EmailAddresseeDAO {

    public List<EmailAddressee> findAll() {
        return getHibernateTemplate().find("from EmailAddressee");
    }

    public void delete(EmailAddressee emailAddressee) {
        getHibernateTemplate().delete(emailAddressee);
    }
}
