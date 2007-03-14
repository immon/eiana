/**
 * org.iana.rzm.trans.TransactionActionsListProducer
 * (C) Research and Academic Computer Network - NASK
 * lukaszz, 2007-03-12, 15:51:03
 */
package org.iana.rzm.trans;

import org.iana.rzm.domain.*;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.change.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

class TransactionActionsListBuilder {

    static void addSupportingOrganizationAction(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        if ((originDomain.getSupportingOrg() == null && domain.getSupportingOrg() != null)) {
            ta = new TransactionAction();
            ta.setChange(additionSupportOrganizationList(domain));
        } else if ((originDomain.getSupportingOrg() != null && domain.getSupportingOrg() == null)) {
            ta = new TransactionAction();
            ta.setChange(removalSupportOrganizationList(originDomain));
        } else
        if (originDomain.getSupportingOrg() != null && !originDomain.getSupportingOrg().equals(domain.getSupportingOrg())) {
            ta = new TransactionAction();
            ta.setChange(modificationContactChangeList(domain.getSupportingOrg(), originDomain.getSupportingOrg()));
        }
        if (ta != null) {
            ta.setName(TransactionAction.Name.MODIFY_SUPPORTING_ORGANIZATION);
            resultList.add(ta);
        }
    }

    static void addAdministrationContactsActions(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        if ((originDomain.getAdminContacts() == null || originDomain.getAdminContacts().size() == 0) && (domain.getAdminContacts() == null || domain.getAdminContacts().size() == 0))
            return;
        List<Contact> originContats = (originDomain.getAdminContacts() != null ? originDomain.getAdminContacts() : new ArrayList<Contact>());
        List<Contact> newContacts = (domain.getAdminContacts() != null ? domain.getAdminContacts() : new ArrayList<Contact>());
        if (!originContats.equals(newContacts)) {
            TransactionAction ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Modification("adminContacts", new ObjectValue<Change>(modificationAdministrationContactsChangeList(newContacts, originContats))));
            ta.setChange(changes);
            ta.setName(TransactionAction.Name.MODIFY_CONTACT);
            resultList.add(ta);
        }
    }

    static void addTechnicalContactsActions(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        if ((originDomain.getTechContacts() == null || originDomain.getTechContacts().size() == 0) && (domain.getTechContacts() == null || domain.getTechContacts().size() == 0))
            return;
        List<Contact> originContats = (originDomain.getTechContacts() != null ? originDomain.getTechContacts() : new ArrayList<Contact>());
        List<Contact> newContacts = (domain.getTechContacts() != null ? domain.getTechContacts() : new ArrayList<Contact>());
        if (!originContats.equals(newContacts)) {
            TransactionAction ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Modification("techContacts", new ObjectValue<Change>(modificationAdministrationContactsChangeList(newContacts, originContats))));
            ta.setChange(changes);
            ta.setName(TransactionAction.Name.MODIFY_CONTACT);
            resultList.add(ta);
        }
    }

    static void addNSAction(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        if ((originDomain.getNameServers() == null || originDomain.getNameServers().size() == 0) && (domain.getNameServers() == null || domain.getNameServers().size() == 0))
            return;
        List<Host> originHosts = (originDomain.getNameServers() != null ? originDomain.getNameServers() : new ArrayList<Host>());
        List<Host> newHosts = (domain.getNameServers() != null ? domain.getNameServers() : new ArrayList<Host>());
        if (!originHosts.equals(newHosts)) {
            TransactionAction ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Modification("nameServers", new ObjectValue<Change>(modificationNameServersChangeList(newHosts, originHosts))));
            ta.setChange(changes);
            ta.setName(TransactionAction.Name.MODIFY_NAMESERVER);
            resultList.add(ta);
        }
    }

    static void addWhoisServerAction(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        if ((originDomain.getWhoisServer() == null && domain.getWhoisServer() != null)) {
            ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Addition("whoisServer", domain.getWhoisServer()));
            ta.setChange(changes);
        } else if ((originDomain.getWhoisServer() != null && domain.getWhoisServer() == null)) {
            ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Removal("whoisServer", originDomain.getWhoisServer()));
            ta.setChange(changes);
        } else
        if (originDomain.getWhoisServer() != null && !originDomain.getWhoisServer().equals(domain.getWhoisServer())) {
            ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Modification("whoisServer", new ModifiedPrimitiveValue(domain.getWhoisServer(), originDomain.getWhoisServer())));
            ta.setChange(changes);
        }
        if (ta != null) {
            ta.setName(TransactionAction.Name.MODIFY_WHOIS_SERVER);
            resultList.add(ta);
        }

    }

    static void addRegisterURLAction(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        if ((originDomain.getRegistryUrl() == null && domain.getRegistryUrl() != null)) {
            ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Addition("registryUrl", domain.getRegistryUrl().toString()));
            ta.setChange(changes);
        } else if ((originDomain.getRegistryUrl() != null && domain.getRegistryUrl() == null)) {
            ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Removal("registryUrl", originDomain.getRegistryUrl().toString()));
            ta.setChange(changes);
        } else
        if (originDomain.getRegistryUrl() != null && !originDomain.getRegistryUrl().equals(domain.getRegistryUrl())) {
            ta = new TransactionAction();
            List<Change> changes = new ArrayList<Change>();
            changes.add(new Modification("registryUrl", new ModifiedPrimitiveValue(domain.getRegistryUrl().toString(), originDomain.getRegistryUrl().toString())));
            ta.setChange(changes);
        }
        if (ta != null) {
            ta.setName(TransactionAction.Name.MODIFY_REGISTRATION_URL);
            resultList.add(ta);
        }
    }

    private static List<Change> modificationIPAddressesChangeList(Set<IPAddress> newIPs, Set<IPAddress> originIPs) {
        List<Change> contactsChangesList = new ArrayList<Change>();
        for (IPAddress originIp : originIPs) {
            boolean contians = false;
            for (IPAddress newIP : newIPs) {
                if (originIp.equals(newIP)) {
                    contians = true;
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(removalIPChangeList(originIp));
            }
        }
        for (IPAddress newIP : newIPs) {
            boolean contians = false;
            for (IPAddress originIP : originIPs) {
                if (newIP.equals(originIP)) {
                    contians = true;
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(additionIPChangeList(newIP));
            }
        }
        return contactsChangesList;
    }

    private static List<Change> modificationAddressesChangeList(List<Address> newAddresses, List<Address> originAddresses) {
        List<Change> changesList = new ArrayList<Change>();
        int newSize = newAddresses.size();
        int originSize = originAddresses.size();
        for (int i = 0; i < (newSize < originSize ? newSize : originSize); i++) {
            Address newAdr = newAddresses.get(i);
            Address originAdr = originAddresses.get(i);
            if (!newAdr.equals(originAdr))
                changesList.addAll(modificationAddressChangeList(newAdr, originAdr));
        }
        if (newSize > originSize) {
            int start = originSize > 0 ? originSize - 1: 0;
            for (int i = start; i < newSize; i++) {
                Address newArd = newAddresses.get(i);
                changesList.addAll(additionAdressChangeList(newArd));
            }
        }
        if (originSize > newSize) {
            int start = newSize > 0 ? newSize - 1: 0;
            for (int i = start; i < originSize; i++) {
                Address orgAddress = originAddresses.get(i);
                changesList.addAll(removalAddressChangeList(orgAddress));
            }
        }
        return changesList;
    }

    private static List<Change> modificationAdministrationContactsChangeList(List<Contact> newContacts, List<Contact> originContacts) {
        List<Change> changesList = new ArrayList<Change>();
        int newSize = newContacts.size();
        int originSize = originContacts.size();
        for (int i = 0; i < (newSize < originSize ? newSize : originSize); i++) {
            Contact newContact = newContacts.get(i);
            Contact originContact = originContacts.get(i);
            if (!newContact.equals(originContact))
                changesList.addAll(modificationContactChangeList(newContact, originContact));
        }
        if (newSize > originSize) {
            int start = originSize > 0 ? originSize - 1: 0;
            for (int i = start; i < newSize; i++) {
                Contact newContact = newContacts.get(i);
                changesList.addAll(additionContactChangeList(newContact));
            }
        }
        if (originSize > newSize) {
            int start = newSize > 0 ? newSize - 1: 0;
            for (int i = start; i < originSize; i++) {
                Contact orgContact = originContacts.get(i);
                changesList.addAll(removalContactChangeList(orgContact));
            }
        }
        return changesList;
    }

    private static List<Change> modificationStringsChangeList(String fieldName, List<String> newStringList, List<String> originStringList) {
        List<Change> stringChangesList = new ArrayList<Change>();
        int newSize = newStringList.size();
        int originSize = originStringList.size();
        for (int i = 0; i < (newSize < originSize ? newSize : originSize); i++) {
            String newString = newStringList.get(i);
            String orgString = originStringList.get(i);
            if (!newString.equals(orgString))
                stringChangesList.add(new Modification(fieldName, new ModifiedPrimitiveValue(newString, orgString)));
        }
        if (newSize > originSize) {
            int start = originSize > 0 ? originSize - 1: 0;
            for (int i = start; i < newSize; i++) {
                String newString = newStringList.get(i);
                stringChangesList.add(new Addition(fieldName, newString));
            }
        }
        if (originSize > newSize) {
            int start = newSize > 0 ? newSize - 1: 0;
            for (int i = start; i < originSize; i++) {
                String orgString = originStringList.get(i);
                stringChangesList.add(new Removal(fieldName, orgString));
            }
        }
        return stringChangesList;
    }

    private static List<Change> modificationNameServersChangeList(List<Host> newHosts, List<Host> orgHosts) {
        List<Change> stringChangesList = new ArrayList<Change>();
        int newSize = newHosts.size();
        int originSize = orgHosts.size();
        for (int i = 0; i < (newSize > originSize ? newSize : originSize); i++) {
            Host newHost = newHosts.get(i);
            Host orgHost = orgHosts.get(i);
            if (!newHost.equals(orgHost))
                stringChangesList.addAll(modificationHostChangeList(newHost, orgHost));
        }
        if (newSize > originSize) {
            for (int i = originSize - 1; i < newSize; i++) {
                Host newHost = newHosts.get(i);
                stringChangesList.addAll(modificationHostChangeList(newHost, null));
            }
        }
        if (originSize > newSize) {
            for (int i = newSize - 1; i < originSize; i++) {
                Host orgHost = orgHosts.get(i);
                stringChangesList.addAll(modificationHostChangeList(null, orgHost));
            }
        }
        return stringChangesList;
    }


    private static List<Change> modificationHostChangeList(Host newHost, Host originHost) {
        List<Change> hostChangeList = new ArrayList<Change>();
        Set<IPAddress> originIPs = (originHost != null && originHost.getAddresses() != null ? originHost.getAddresses() : new HashSet<IPAddress>());
        Set<IPAddress> newIPs = (newHost != null && newHost.getAddresses() != null ? newHost.getAddresses() : new HashSet<IPAddress>());
        if (originIPs.size() != 0 && newIPs.size() != 0)
            if (!(originIPs.equals(newIPs))) {
                hostChangeList.add(new Modification("addresses", new ObjectValue<Change>(modificationIPAddressesChangeList(newIPs, originIPs))));
            }
        if (originHost == null && newHost != null) {
            hostChangeList.add(new Addition("name", newHost.getName()));
        } else if (originHost != null && newHost == null) {
            hostChangeList.add(new Removal("name", originHost.getName()));
        } else if (originHost.getName() == null && newHost.getName() != null) {
            hostChangeList.add(new Addition("name", newHost.getName()));
        } else if (originHost.getName() != null && newHost.getName() == null) {
            hostChangeList.add(new Removal("name", originHost.getName()));
        } else if (originHost.getName() != null && originHost.getName().equals(newHost.getName())) {
            hostChangeList.add(new Modification("name", new ModifiedPrimitiveValue(newHost.getName(), originHost.getName())));
        }
        return hostChangeList;
    }


    private static List<Change> removalIPChangeList(IPAddress ip) {
        List<Change> ipChangesList = new ArrayList<Change>();
        ipChangesList.add(new Removal("type", ip.getType().toString()));
        ipChangesList.add(new Removal("address", ip.getAddress()));
        return ipChangesList;
    }

    private static List<Change> additionIPChangeList(IPAddress ip) {
        List<Change> ipChangesList = new ArrayList<Change>();
        ipChangesList.add(new Addition("type", ip.getType().toString()));
        ipChangesList.add(new Addition("address", ip.getAddress()));
        return ipChangesList;
    }

    private static List<Change> modificationIPChangeList(IPAddress newIP, IPAddress originIP) {
        List<Change> ipChangesList = new ArrayList<Change>();
        ipChangesList.add(new Modification("type", new ModifiedPrimitiveValue(newIP.getType().toString(), originIP.getType().toString())));
        ipChangesList.add(new Modification("address", new ModifiedPrimitiveValue(newIP.getAddress(), originIP.getAddress())));
        return ipChangesList;
    }


    private static List<Change> additionSupportOrganizationList(Domain domain) {
        List<Change> contactChanges = new ArrayList<Change>();
        contactChanges.add(new Addition("supportingOrg", new ObjectValue<Addition>(additionContactChangeList(domain.getSupportingOrg()))));
        return contactChanges;
    }

    private static List<Addition> additionContactChangeList(Contact contact) {
        List<Addition> changes = new ArrayList<Addition>();
        changes.addAll(additionAdressesChangeList(contact.getAddresses()));
        changes.addAll(additionStringList("emails", contact.getEmails()));
        changes.addAll(additionStringList("faxNumbers", contact.getFaxNumbers()));
        changes.addAll(additionStringList("phoneNumbers", contact.getPhoneNumbers()));
        changes.add(new Addition("name", contact.getName()));
        return changes;
    }

    private static List<Addition> additionAdressesChangeList(List<Address> addressList) {
        List<Addition> resultAdditions = new ArrayList<Addition>();
        for (Address adr : addressList) {
            resultAdditions.add(new Addition("addresses", new ObjectValue<Addition>(additionAdressChangeList(adr))));
        }
        return resultAdditions;
    }

    private static List<Addition> additionAdressChangeList(Address adr) {
        List<Addition> addressAddition = new ArrayList<Addition>();
        addressAddition.add(new Addition("city", adr.getCity()));
        addressAddition.add(new Addition("countryCode", adr.getCountryCode()));
        addressAddition.add(new Addition("postalCode", adr.getPostalCode()));
        addressAddition.add(new Addition("state", adr.getState()));
        addressAddition.add(new Addition("street", adr.getStreet()));
        return addressAddition;
    }

    private static List<Addition> additionStringList(String fieldName, List<String> stringList) {
        List<Addition> resultAdditions = new ArrayList<Addition>();
        for (String s : stringList) {
            resultAdditions.add(new Addition(fieldName, new PrimitiveValue<Addition>(s)));
        }
        return resultAdditions;
    }

    private static List<Change> removalSupportOrganizationList(Domain domain) {
        List<Change> contactChanges = new ArrayList<Change>();
        contactChanges.add(new Removal("supportingOrg", new ObjectValue<Removal>(removalContactChangeList(domain.getSupportingOrg()))));
        return contactChanges;
    }

    private static List<Removal> removalContactChangeList(Contact contact) {
        List<Removal> changes = new ArrayList<Removal>();
        changes.addAll(removalAdressesChangeList(contact.getAddresses()));
        changes.addAll(removalStringList("emails", contact.getEmails()));
        changes.addAll(removalStringList("faxNumbers", contact.getFaxNumbers()));
        changes.addAll(removalStringList("phoneNumbers", contact.getPhoneNumbers()));
        changes.add(new Removal("name", contact.getName()));
        return changes;
    }

    private static List<Removal> removalAdressesChangeList(List<Address> addressList) {
        List<Removal> resultRemoval = new ArrayList<Removal>();
        for (Address adr : addressList) {
            resultRemoval.add(new Removal("addresses", new ObjectValue<Removal>(removalAddressChangeList(adr))));
        }
        return resultRemoval;
    }

    private static List<Removal> removalAddressChangeList(Address adr) {
        List<Removal> addressRemovals = new ArrayList<Removal>();
        addressRemovals.add(new Removal("city", adr.getCity()));
        addressRemovals.add(new Removal("countryCode", adr.getCountryCode()));
        addressRemovals.add(new Removal("postalCode", adr.getPostalCode()));
        addressRemovals.add(new Removal("state", adr.getState()));
        addressRemovals.add(new Removal("street", adr.getStreet()));
        return addressRemovals;
    }

    private static List<Removal> removalStringList(String fieldName, List<String> stringList) {
        List<Removal> resultRemoval = new ArrayList<Removal>();
        for (String s : stringList) {
            resultRemoval.add(new Removal(fieldName, new PrimitiveValue<Removal>(s)));
        }
        return resultRemoval;
    }

    private static List<Change> modificationContactChangeList(Contact newContact, Contact originContact) {
        List<Change> supportingOrgChangeList = new ArrayList<Change>();
        if (!((originContact.getAddresses() == null || originContact.getAddresses().size() == 0) && (newContact.getAddresses() == null || newContact.getAddresses().size() == 0)))
            if (!originContact.getAddresses().equals(newContact.getAddresses())) {
                supportingOrgChangeList.add(new Modification("addesses", new ObjectValue<Change>(modificationAddressesChangeList(newContact.getAddresses(), originContact.getAddresses()))));
            }
        if (!((originContact.getEmails() == null || originContact.getEmails().size() == 0) && (newContact.getEmails() == null || newContact.getEmails().size() == 0)))
            if (!originContact.getEmails().equals(newContact.getEmails())) {
                supportingOrgChangeList.addAll(modificationStringsChangeList("emails", newContact.getEmails(), originContact.getEmails()));
            }
        if (!((originContact.getPhoneNumbers() == null || originContact.getPhoneNumbers().size() == 0) && (newContact.getPhoneNumbers() == null || newContact.getPhoneNumbers().size() == 0)))
            if (!originContact.getPhoneNumbers().equals(newContact.getPhoneNumbers())) {
                supportingOrgChangeList.addAll(modificationStringsChangeList("phoneNumbers", newContact.getPhoneNumbers(), originContact.getPhoneNumbers()));
            }
        if (!((originContact.getFaxNumbers() == null || originContact.getFaxNumbers().size() == 0) && (newContact.getFaxNumbers() == null || newContact.getFaxNumbers().size() == 0)))
            if (!originContact.getFaxNumbers().equals(newContact.getFaxNumbers())) {
                supportingOrgChangeList.addAll(modificationStringsChangeList("faxNumbers", newContact.getFaxNumbers(), originContact.getFaxNumbers()));
            }
        if (!originContact.getName().equals(newContact.getName())) {
            supportingOrgChangeList.add(new Modification("name", new ModifiedPrimitiveValue(newContact.getName(), originContact.getName())));
        }
        return supportingOrgChangeList;
    }


    private static List<Modification> modificationAddressChangeList(Address newAdr, Address originAdr) {
        List<Modification> modsList = new ArrayList<Modification>();
        if (!originAdr.getCity().equals(newAdr.getCity()))
            modsList.add(new Modification("city", new ModifiedPrimitiveValue(newAdr.getCity(), originAdr.getCity())));
        if (!originAdr.getCountryCode().equals(newAdr.getCountryCode()))
            modsList.add(new Modification("countryCode", new ModifiedPrimitiveValue(newAdr.getCountryCode(), originAdr.getCountryCode())));
        if (!originAdr.getPostalCode().equals(newAdr.getPostalCode()))
            modsList.add(new Modification("postalCode", new ModifiedPrimitiveValue(newAdr.getPostalCode(), originAdr.getPostalCode())));
        if (!originAdr.getState().equals(newAdr.getState()))
            modsList.add(new Modification("state", new ModifiedPrimitiveValue(newAdr.getState(), originAdr.getState())));
        if (!originAdr.getStreet().equals(newAdr.getStreet()))
            modsList.add(new Modification("street", new ModifiedPrimitiveValue(newAdr.getStreet(), originAdr.getStreet())));
        return modsList;
    }

    /*
    private static<T> List<Change> veryTestMethod(List<T> newHosts, List<T> originHosts) {
       List<Change> contactsChangesList = new ArrayList<Change>();
       for (T originHost : originHosts) {
           boolean contians = false;
           for (T newHost : newHosts) {
               if (originHost.getObjId().equals(newHost.getObjId())) {
                   if (originHost.equals(newHost)) contians = true;
                   else {
                       contactsChangesList.addAll(modificationHostChangeList(newHost, originHost));
                   }
               }
           }
           if (contians == false) {
               contactsChangesList.addAll(modificationHostChangeList(null, originHost));
           }
       }
       for (T newHost : newHosts) {
           boolean contians = false;
           for (T originHost : originHosts) {
               if (newHost.getObjId().equals(originHost.getObjId())) {
                   contians = true;
               }
           }
           if (contians == false) {
               contactsChangesList.addAll(modificationHostChangeList(newHost, null));
           }
       }
       return contactsChangesList;
   } */
}
