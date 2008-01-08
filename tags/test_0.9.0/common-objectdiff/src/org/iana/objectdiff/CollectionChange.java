package org.iana.objectdiff;

import javax.persistence.*;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class CollectionChange extends Change {

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    @JoinTable(name = "CollectionChange_Added",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    private Collection<Change> added = new ArrayList<Change>();
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    @JoinTable(name = "CollectionChange_Removed",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    private Collection<Change> removed = new ArrayList<Change>();
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    @JoinTable(name = "CollectionChange_Modified",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    private Collection<Change> modified = new ArrayList<Change>();

    private CollectionChange() {}

    public CollectionChange(Type type) {
        super(type);
    }

    public void addAdded(Change change) {
        added.add(change);
    }

    public void addAdded(Collection<Change> change) {
        added.addAll(change);
    }

    public Collection<Change> getAdded() {
        return added;
    }

    public void addRemoved(Change change) {
        removed.add(change);
    }

    public void addRemoved(Collection<Change> change) {
        removed.addAll(change);
    }

    public Collection<Change> getRemoved() {
        return removed;
    }

    public void addModified(Change change) {
        modified.add(change);    
    }

    public void addModified(Collection<Change> change) {
        modified.addAll(change);
    }

    public Collection<Change> getModified() {
        return modified;
    }

    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty() && modified.isEmpty();
    }

    public void accept(ChangeVisitor v) {
        v.visitCollection(this);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectionChange that = (CollectionChange) o;

        if (added != null ? !added.equals(that.added) : that.added != null) return false;
        if (modified != null ? !modified.equals(that.modified) : that.modified != null) return false;
        if (removed != null ? !removed.equals(that.removed) : that.removed != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (added != null ? added.hashCode() : 0);
        result = 31 * result + (removed != null ? removed.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        return result;
    }
}
