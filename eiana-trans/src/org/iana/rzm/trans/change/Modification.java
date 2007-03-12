package org.iana.rzm.trans.change;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Modification extends Change {

    @ManyToOne(cascade = CascadeType.ALL,
            targetEntity = AbstractValue.class)
    @JoinColumn(name = "modificationValue_objId")
    private Value<? extends Change> value;

    private Modification() {}

    public Modification(String fieldName, ModifiedPrimitiveValue value) {
        super(fieldName, Type.UPDATE);
        this.value = value;
    }
    
    public Modification(String fieldName, Value<? extends Change> value) {
        super(fieldName, Type.UPDATE);
        this.value = value;
    }

    public Value<? extends Change> getValue() {
        return value;
    }

    public void setValue(Value<? extends Change> value) {
        this.value = value;
    }

    public void accept(ChangeVisitor visitor) {
        CheckTool.checkNull(visitor, "visitor");
        visitor.visitModification(this);
    }
}
