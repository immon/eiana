package org.iana.rzm.trans.jbpm;

import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Service;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @author Jakub Laszkiewicz
 */
public class SpringDbPersistenceServiceFactory extends DbPersistenceServiceFactory {

	private static final long serialVersionUID = 1L;

	public Service openService() {
		log.debug("creating persistence service");
		return new SpringDbPersistenceService(this);
	}

	private static Log log = LogFactory.getLog(SpringDbPersistenceServiceFactory.class);
}
