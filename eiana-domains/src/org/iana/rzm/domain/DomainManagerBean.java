package org.iana.rzm.domain;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.exporter.DomainExporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 */
public class DomainManagerBean implements DomainManager {

    private DomainDAO dao;

    private HostManager hostManager;

    private DomainExporter domainExporter;

    public DomainManagerBean(DomainDAO dao, HostManager hostManager, DomainExporter domainExporter) {
        CheckTool.checkNull(dao, "domain dao");
        this.dao = dao;
        CheckTool.checkNull(hostManager, "host manager");
        this.hostManager = hostManager;
        CheckTool.checkNull(domainExporter, "domain exporter");
        this.domainExporter = domainExporter;
    }

    public Domain get(String name) {
        return dao.get(name);
    }

    public Domain get(long id) {
        return dao.get(id);
    }

    public Domain getCloned(String name) {
        Domain domain = dao.get(name);
        return domain == null ? null : domain.clone();
    }

    public Domain getCloned(long id) {
        Domain domain = dao.get(id);
        return domain == null ? null : domain.clone();
    }

    public void create(Domain domain) {
        updateNameServers(domain);
        updateState(domain);
        dao.create(domain);
    }

    public void updateState(Domain domain) {
        Domain retDomain = get(domain.getName());
        if (retDomain != null) {
            domain.setOpenProcesses(retDomain.getOpenProcesses());
            domain.setThirdPartyPendingProcesses(retDomain.getThirdPartyPendingProcesses());
        }
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

    public void update(Domain domain, String updater) {
        CheckTool.checkNull(domain, "modified domain");
        Domain old = dao.get(domain.getObjId());
        CheckTool.checkNull(old, "existing domain");
        updateNameServers(domain);
        if (old.getAdminContact() != null) {
            Contact contact = old.getAdminContact();
            domain.getAdminContact().setId(contact.getObjId());
        }
        if (old.getTechContact() != null) {
            Contact contact = old.getTechContact();
            domain.getTechContact().setId(contact.getObjId());
        }
        if (old.getSupportingOrg() != null) {
            Contact contact = old.getSupportingOrg();
            domain.getSupportingOrg().setId(contact.getObjId());
        }
        domain.setOpenProcesses(old.getOpenProcesses());
        domain.setThirdPartyPendingProcesses(old.getThirdPartyPendingProcesses());
        domain.setModifiedBy(updater);
        dao.update(domain);
        domainExporter.exportToXML(dao.findAll());
    }

    public void delete(Domain domain) {
        CheckTool.checkNull(domain, "domain");
        domain.setAdminContact(null);
        domain.setTechContact(null);
        domain.setSupportingOrg(null);
        List<Host> hosts = new ArrayList<Host>(domain.getNameServers());
        for (Host host : hosts) {
            domain.removeNameServer(host);
            if (!host.isNameServer()) hostManager.delete(host);
            else hostManager.update(host);
        }
        //dao.update(domain);
        dao.delete(domain);
    }

    public void delete(String name) {
        Domain domain = get(name);
        if (domain != null) delete(domain);
    }

    public void deleteAll() {
        for (Domain domain : findAll()) {
            delete(domain);
        }
    }

    public List<Domain> findAll() {
        return dao.findAll();
    }

    public List<Domain> findDelegatedTo(Set<String> hostNames) {
        if (hostNames == null || hostNames.isEmpty()) return new ArrayList<Domain>();
        return dao.findDelegatedTo(hostNames);
    }

    public List<Domain> find(Criterion criteria) {
        return dao.find(criteria);
    }

    public int count(Criterion criteria) {
        return dao.count(criteria);
    }

    public List<Domain> find(Criterion criteria, int offset, int limit) {
        CheckTool.checkNoNegative(offset, "offset is negative");
        CheckTool.checkNoNegative(limit, "limit is negative");
        return dao.find(criteria, offset, limit);
    }

    public void updateOpenProcesses(String name, boolean inc) {
        Domain domain = dao.get(name);
        if (domain == null) throw new IllegalArgumentException("domain " + name + " does not exist");
        if (inc) domain.incOpenProcesses();
        else domain.decOpenProcesses();
    }

    public void updateThirdPartyPendingProcesses(String name, boolean inc) {
        Domain domain = dao.get(name);
        if (domain == null) throw new IllegalArgumentException("domain " + name + " does not exist");
        if (inc) domain.incThirdPartyPendingProcesses();
        else domain.decThirdPartyPendingProcesses();
    }
}
