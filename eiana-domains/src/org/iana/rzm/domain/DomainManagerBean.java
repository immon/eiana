package org.iana.rzm.domain;

import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainManagerBean implements DomainManager {

    private DomainDAO dao;
    private HostManager hostManager;

    public DomainManagerBean(DomainDAO dao, HostManager hostManager) {
        CheckTool.checkNull(dao, "domain dao");
        this.dao = dao;
        CheckTool.checkNull(dao, "host manager");
        this.hostManager = hostManager;
    }

    public Domain get(String name) {
        return dao.get(name);
    }

    public Domain get(long id) {
        return dao.get(id);
    }

    public void create(Domain domain) {
        dao.create(domain);
    }

    public void update(Domain domain) {
        List<Host> newHosts = new ArrayList<Host>();

        for (Host host : domain.getNameServers())
            if (host.getObjId() == null) {
                Host existing = hostManager.get(host.getName());
                if (existing != null) {
                    for (IPAddress ip : host.getAddresses())
                        existing.addIPAddress(ip);
                    newHosts.add(existing);
                } else newHosts.add(host);
            } else newHosts.add(host);

        domain.setNameServers(newHosts);
        dao.update(domain);
    }

    public void delete(Domain domain) {
        dao.delete(domain);
    }

    public void delete(String name) {
        delete(get(name));
    }

    public List<Domain> findAll() {
        return dao.findAll();
    }

    public List<Domain> findDelegatedTo(Set<String> hostNames) {
        return dao.findDelegatedTo(hostNames);
    }

    public List<Domain> find(Criterion criteria) {
        return dao.find(criteria);
    }
}
