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

    public Domain getCloned(String name) throws CloneNotSupportedException {
        Domain domain = dao.get(name);
        return domain == null ? null : domain.clone();
    }

    public Domain getCloned(long id) throws CloneNotSupportedException {
        Domain domain = dao.get(id);
        return domain == null ? null : domain.clone();
    }

    public void create(Domain domain) {
        updateNameServers(domain);
        dao.create(domain);
    }

    private void updateNameServers(Domain domain) {
        List<Host> newHosts = new ArrayList<Host>();
        for (Host host : domain.getNameServers()) {
            Host found = hostManager.get(host.getName());
            if (found == null) {
                host.setDelegations(0);
                newHosts.add(host);
            } else {
                found.setAddresses(host.getAddresses());
                newHosts.add(found);
            }
        }
        domain.setNameServers(newHosts);
    }

    public void update(Domain domain) {
        updateNameServers(domain);
        dao.update(domain);
    }

    public void delete(Domain domain) {
        CheckTool.checkNull(domain, "domain");
        List<Host> hosts = new ArrayList<Host>(domain.getNameServers());
        for (Host host : hosts) {
            domain.removeNameServer(host);
            if (!host.isNameServer()) hostManager.delete(host);
        }
        dao.delete(domain);
    }

    public void delete(String name) {
        Domain domain = get(name);
        if (domain != null) delete(domain);
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
