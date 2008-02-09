package org.iana.notifications.dao;

import org.iana.notifications.EmailAddressee;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public interface EmailAddresseeDAO {

    public List<EmailAddressee> findAll();

    public void delete(EmailAddressee emailAddressee);
}
