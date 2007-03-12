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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

class TransactionActionsListBuilder {

    static void addNSAction(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        List<Host> originHosts = (originDomain.getNameServers() != null ? originDomain.getNameServers() : new ArrayList<Host>());
        List<Host> newHosts = (domain.getNameServers() != null ? domain.getNameServers() : new ArrayList<Host>());
        ta = new TransactionAction();
        List<Change> changes = new ArrayList<Change>();
        changes.add(new Modification("nameServers", new ObjectValue<Change>(modificationNameServersChangeList(newHosts, originHosts))));
        ta.setChange(changes);
        ta.setName(TransactionAction.Name.MODIFY_CONTACT);
        resultList.add(ta);
    }


   private static List<Change> modificationNameServersChangeList(List<Host> newHosts, List<Host> originHosts) {
        List<Change> contactsChangesList = new ArrayList<Change>();
        for (Host originHost : originHosts) {
            boolean contians = false;
            for (Host newHost : newHosts) {
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
        for (Host newHost : newHosts) {
            boolean contians = false;
            for (Host originHost : originHosts) {
                if (newHost.getObjId().equals(originHost.getObjId())) {
                    contians = true;
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(modificationHostChangeList(newHost, null));
            }
        }
        return contactsChangesList;
    }


    private static List<Change> modificationHostChangeList(Host newHost, Host originHost) {
        List<Change> hostChangeList = new ArrayList<Change>();
        Set<IPAddress> originIPs = (originHost.getAddresses() != null ? originHost.getAddresses() : new HashSet<IPAddress>());
        Set<IPAddress> newIPs = (newHost.getAddresses() != null ? newHost.getAddresses() : new HashSet<IPAddress>());

        if (!originIPs.equals(newIPs)) {
            hostChangeList.add(new Modification("addresses", new ObjectValue<Change>(modificationIPAddressesChangeList(newIPs, originIPs))));
        }
        return hostChangeList;
    }

    private static List<Change> modificationIPAddressesChangeList(Set<IPAddress> newIPs, Set<IPAddress> originIPs) {
        List<Change> contactsChangesList = new ArrayList<Change>();
        for (IPAddress originIp : originIPs) {
            boolean contians = false;
            for (IPAddress newIP : newIPs) {
                if (originIp.getObjId().equals(newIP.getObjId())) {
                    if (originIp.equals(newIP)) contians = true;
                    else {
                        contactsChangesList.addAll(modificationIPChangeList(newIP, originIp));
                    }
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(removalIPChangeList(originIp));
            }
        }
        for (IPAddress newIP : newIPs) {
            boolean contians = false;
            for (IPAddress originIP : originIPs) {
                if (newIP.getObjId().equals(originIP.getObjId())) {
                    contians = true;
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(additionIPChangeList(newIP));
            }
        }
        return contactsChangesList;
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

    static void addTechnicalContactsActions(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        List<Contact> originContats = (originDomain.getTechContacts() != null ? originDomain.getTechContacts() : new ArrayList<Contact>());
        List<Contact> newContacts = (domain.getTechContacts() != null ? domain.getTechContacts() : new ArrayList<Contact>());
        ta = new TransactionAction();
        List<Change> changes = new ArrayList<Change>();
        if (!originContats.equals(newContacts))
            changes.add(new Modification("techContacts", new ObjectValue<Change>(modificationAdministrationContactsChangeList(newContacts, originContats))));
        ta.setChange(changes);
        ta.setName(TransactionAction.Name.MODIFY_CONTACT);
        resultList.add(ta);
    }

    static void addWhoisServerAction(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        if ((originDomain.getWhoisServer() == null && domain.getWhoisServer() != null)) {
            ta = new TransactionAction();
            ta.setName(TransactionAction.Name.MODIFY_CONTACT);
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
            ta.setChange(modificationSupportOrganizationChangesList(domain.getSupportingOrg(), originDomain.getSupportingOrg()));
        }
        if (ta != null) {
            ta.setName(TransactionAction.Name.MODIFY_SUPPORTING_ORGANIZATION);
            resultList.add(ta);
        }
    }

    private static List<Change> additionSupportOrganizationList(Domain domain) {
        List<Addition> changes = new ArrayList<Addition>();
        changes.addAll(additionAdressesChangeList(domain.getSupportingOrg().getAddresses()));
        changes.addAll(additionStringList("emails", domain.getSupportingOrg().getEmails()));
        changes.addAll(additionStringList("faxNumbers", domain.getSupportingOrg().getFaxNumbers()));
        changes.addAll(additionStringList("phoneNumbers", domain.getSupportingOrg().getPhoneNumbers()));
        List<Change> contactChanges = new ArrayList<Change>();
        contactChanges.add(new Addition("supportingOrg", new ObjectValue<Addition>(changes)));
        return contactChanges;
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
        List<Removal> changes = new ArrayList<Removal>();
        changes.addAll(removalAdressesChangeList(domain.getSupportingOrg().getAddresses()));
        changes.addAll(removalStringList("emails", domain.getSupportingOrg().getEmails()));
        changes.addAll(removalStringList("faxNumbers", domain.getSupportingOrg().getFaxNumbers()));
        changes.addAll(removalStringList("phoneNumbers", domain.getSupportingOrg().getPhoneNumbers()));
        List<Change> contactChanges = new ArrayList<Change>();
        contactChanges.add(new Removal("supportingOrg", new ObjectValue<Removal>(changes)));
        return contactChanges;
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


    private static List<Change> modificationSupportOrganizationChangesList(Contact newContact, Contact originContact) {
        List<Change> supportingOrgChangeList = new ArrayList<Change>();
        if (!originContact.getAddresses().equals(newContact.getAddresses())) {
            supportingOrgChangeList.add(new Modification("addesses", new ObjectValue<Change>(modificationAddressesChangeList(newContact.getAddresses(), originContact.getAddresses()))));
        }
        if (!originContact.getEmails().equals(newContact.getEmails())) {
            supportingOrgChangeList.addAll(modificationStringsChangeList("emails", newContact.getEmails(), originContact.getEmails()));
        }
        if (!originContact.getPhoneNumbers().equals(newContact.getPhoneNumbers())) {
            supportingOrgChangeList.addAll(modificationStringsChangeList("phoneNumbers", newContact.getPhoneNumbers(), originContact.getPhoneNumbers()));
        }
        if (!originContact.getFaxNumbers().equals(newContact.getFaxNumbers())) {
            supportingOrgChangeList.addAll(modificationStringsChangeList("faxNumbers", newContact.getFaxNumbers(), originContact.getFaxNumbers()));
        }


        return supportingOrgChangeList;
    }

    private static List<Change> modificationAddressesChangeList(List<Address> newAddresses, List<Address> originAddresses) {
        List<Change> addressesChangesList = new ArrayList<Change>();
        for (Address originAdr : originAddresses) {
            boolean contians = false;
            for (Address newAdr : newAddresses) {
                if (originAdr.getObjId().equals(newAdr.getObjId())) {
                    if (originAdr.equals(newAdr)) contians = true;
                    else {
                        addressesChangesList.addAll(modificationAddressChangeList(newAdr, originAdr));
                    }
                }
            }
            if (contians == false) {
                addressesChangesList.addAll(removalAddressChangeList(originAdr));
            }
        }
        for (Address newAdr : newAddresses) {
            boolean contians = false;
            for (Address originAdr : originAddresses) {
                if (newAdr.getObjId().equals(originAdr.getObjId())) {
                    contians = true;
                }
            }
            if (contians == false) {
                addressesChangesList.addAll(additionAdressChangeList(newAdr));
            }
        }
        return addressesChangesList;
    }

    private static List<Change> modificationStringsChangeList(String fieldName, List<String> newStringList, List<String> originStringList) {
        List<Change> stringChangesList = new ArrayList<Change>();
        int newSize = newStringList.size();
        int originSize = originStringList.size();
        for (int i = 0; i < (newSize > originSize ? newSize : originSize); i++) {
            String newString = newStringList.get(i);
            String orgString = originStringList.get(i);
            if (!newString.equals(orgString))
                stringChangesList.add(new Modification(fieldName, new ModifiedPrimitiveValue(newString, orgString)));
        }
        if (newSize > originSize) {
            for (int i = originSize - 1; i < newSize; i++) {
                String newString = newStringList.get(i);
                stringChangesList.add(new Addition(fieldName, newString));
            }
        }
        if (originSize > newSize) {
            for (int i = originSize - 1; i < newSize; i++) {
                String orgString = originStringList.get(i);
                stringChangesList.add(new Removal(fieldName, orgString));
            }
        }
        return stringChangesList;
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


    static void addAdministrationContactsActions(Domain domain, Domain originDomain, List<TransactionAction> resultList) {
        TransactionAction ta = null;
        List<Contact> originContats = (originDomain.getAdminContacts() != null ? originDomain.getAdminContacts() : new ArrayList<Contact>());
        List<Contact> newContacts = (domain.getAdminContacts() != null ? domain.getAdminContacts() : new ArrayList<Contact>());
        ta = new TransactionAction();
        List<Change> changes = new ArrayList<Change>();
        changes.add(new Modification("administrationContact", new ObjectValue<Change>(modificationAdministrationContactsChangeList(newContacts, originContats))));
        ta.setChange(changes);
        ta.setName(TransactionAction.Name.MODIFY_CONTACT);
        resultList.add(ta);
    }


    private static List<Change> modificationAdministrationContactsChangeList(List<Contact> newContacts, List<Contact> originContacts) {
        List<Change> contactsChangesList = new ArrayList<Change>();
        for (Contact originContact : originContacts) {
            boolean contians = false;
            for (Contact newContact : newContacts) {
                if (originContact.getObjId().equals(newContact.getObjId())) {
                    if (originContact.equals(newContact)) contians = true;
                    else {
                        contactsChangesList.addAll(modificationContactChangeList(newContact, originContact));
                    }
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(removalContactChangeList(originContact));
            }
        }
        for (Contact newContact : newContacts) {
            boolean contians = false;
            for (Contact originContact : originContacts) {
                if (newContact.getObjId().equals(originContact.getObjId())) {
                    contians = true;
                }
            }
            if (contians == false) {
                contactsChangesList.addAll(additionContactChangeList(newContact));
            }
        }
        return contactsChangesList;
    }

    private static List<Change> modificationContactChangeList(Contact newContact, Contact originContact) {
        List<Change> supportingOrgChangeList = new ArrayList<Change>();
        if (!originContact.getAddresses().equals(newContact.getAddresses())) {
            supportingOrgChangeList.add(new Modification("addesses", new ObjectValue<Change>(modificationAddressesChangeList(newContact.getAddresses(), originContact.getAddresses()))));
        }
        if (!originContact.getEmails().equals(newContact.getEmails())) {
            supportingOrgChangeList.addAll(modificationStringsChangeList("emails", newContact.getEmails(), originContact.getEmails()));
        }
        if (!originContact.getPhoneNumbers().equals(newContact.getPhoneNumbers())) {
            supportingOrgChangeList.addAll(modificationStringsChangeList("phoneNumbers", newContact.getPhoneNumbers(), originContact.getPhoneNumbers()));
        }
        if (!originContact.getFaxNumbers().equals(newContact.getFaxNumbers())) {
            supportingOrgChangeList.addAll(modificationStringsChangeList("faxNumbers", newContact.getFaxNumbers(), originContact.getFaxNumbers()));
        }
        return supportingOrgChangeList;
    }

    private static List<Change> removalContactChangeList(Contact contact) {
        List<Change> changes = new ArrayList<Change>();
        changes.addAll(removalAdressesChangeList(contact.getAddresses()));
        changes.addAll(removalStringList("emails", contact.getEmails()));
        changes.addAll(removalStringList("faxNumbers", contact.getFaxNumbers()));
        changes.addAll(removalStringList("phoneNumbers", contact.getPhoneNumbers()));
        return changes;
    }

    private static List<Change> additionContactChangeList(Contact contact) {
        List<Change> changes = new ArrayList<Change>();
        changes.addAll(additionAdressesChangeList(contact.getAddresses()));
        changes.addAll(additionStringList("emails", contact.getEmails()));
        changes.addAll(additionStringList("faxNumbers", contact.getFaxNumbers()));
        changes.addAll(additionStringList("phoneNumbers", contact.getPhoneNumbers()));
        return changes;
    }

}
