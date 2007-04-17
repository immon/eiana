package org.iana.rzm.trans.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.Date;

/**
 * @author Jakub Laszkiewicz
 */
public class ProcessCriteria {
    private Collection<String> domainNames = new HashSet<String>();
    private Collection<String> states = new HashSet<String>();
    private Collection<Long> ticketIds = new HashSet<Long>();
    private Collection<String> processNames = new HashSet<String>();
    private Collection<String> creators = new HashSet<String>();
    private Collection<String> modifiers = new HashSet<String>();
    private Collection<String> userNames = new HashSet<String>();
    private Date startedAfter;
    private Date startedBefore;
    private Date finishedAfter;
    private Date finishedBefore;
    private Date createdAfter;
    private Date createdBefore;
    private Date modifiedAfter;
    private Date modifiedBefore;

    public Collection<String> getDomainNames() {
        return domainNames;
    }

    public void addDomainName(String domainName) {
        domainNames.add(domainName);
    }

    public void addAllDomainNames(Collection<String> domainNames) {
        this.domainNames.addAll(domainNames);
    }

    public Collection<String> getStates() {
        return states;
    }

    public void addState(String state) {
        states.add(state);
    }

    public void addAllStates(Collection<String> states) {
        this.states.addAll(states);
    }

    public Collection<Long> getTicketIds() {
        return ticketIds;
    }

    public void addTicketId(Long ticketId) {
        ticketIds.add(ticketId);
    }

    public void addAllTicketIds(Collection<Long> ticketIds) {
        this.ticketIds.addAll(ticketIds);
    }

    public Collection<String> getProcessNames() {
        return processNames;
    }

    public void addProcessName(String processName) {
        processNames.add(processName);
    }

    public void addAllProcessNames(Collection<String> processNames) {
        this.processNames.addAll(processNames);
    }

    public Collection<String> getCreators() {
        return creators;
    }

    public void addCreator(String creator) {
        creators.add(creator);
    }

    public void addAllCreators(Collection<String> creators) {
        this.creators.addAll(creators);
    }

    public Collection<String> getModifiers() {
        return modifiers;
    }

    public void addModifier(String modifier) {
        modifiers.add(modifier);
    }

    public void addAllModifiers(Collection<String> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    public Collection<String> getUserNames() {
        return userNames;
    }

    public void addUserName(String userName) {
        userNames.add(userName);
    }

    public void addAllUserName(Collection<String> userName) {
        this.userNames.addAll(userName);
    }

    public Date getStartedAfter() {
        return startedAfter;
    }

    public void setStartedAfter(Date startedAfter) {
        this.startedAfter = startedAfter;
    }

    public Date getStartedBefore() {
        return startedBefore;
    }

    public void setStartedBefore(Date startedBefore) {
        this.startedBefore = startedBefore;
    }

    public Date getFinishedAfter() {
        return finishedAfter;
    }

    public void setFinishedAfter(Date finishedAfter) {
        this.finishedAfter = finishedAfter;
    }

    public Date getFinishedBefore() {
        return finishedBefore;
    }

    public void setFinishedBefore(Date finishedBefore) {
        this.finishedBefore = finishedBefore;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
    }

    public Date getModifiedAfter() {
        return modifiedAfter;
    }

    public void setModifiedAfter(Date modifiedAfter) {
        this.modifiedAfter = modifiedAfter;
    }

    public Date getModifiedBefore() {
        return modifiedBefore;
    }

    public void setModifiedBefore(Date modifiedBefore) {
        this.modifiedBefore = modifiedBefore;
    }
}
