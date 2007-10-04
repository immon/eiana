package org.iana.rzm.facade.system.trans.vo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Date;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionCriteriaVO {
    private Collection<String> domainNames = new HashSet<String>();
    private Collection<String> states = new HashSet<String>();
    private Collection<Long> ticketIds = new HashSet<Long>();
    private Collection<String> processNames = new HashSet<String>();
    private Collection<String> creators = new HashSet<String>();
    private Collection<String> modifiers = new HashSet<String>();
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

    public Collection<String> getStates() {
        return states;
    }

    public void addState(String state) {
        states.add(state);
    }

    public Collection<Long> getTicketIds() {
        return ticketIds;
    }

    public void addTickedId(Long ticketId) {
        ticketIds.add(ticketId);
    }

    public Collection<String> getProcessNames() {
        return processNames;
    }

    public void addProcessName(String processName) {
        processNames.add(processName);
    }

    public Collection<String> getCreators() {
        return creators;
    }

    public void addCreator(String creator) {
        creators.add(creator);
    }

    public Collection<String> getModifiers() {
        return modifiers;
    }

    public void addModifier(String modifier) {
        modifiers.add(modifier);
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
