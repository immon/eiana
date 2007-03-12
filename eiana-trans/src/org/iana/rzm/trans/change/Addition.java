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
public class Addition extends AdditionOrRemoval {

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AbstractValue.class)
    @JoinColumn(name = "additionValue_objId")
    private Value<Addition> value;

    private Addition() {}

    public Addition(String fieldName, String value) {
        this(fieldName, new PrimitiveValue<Addition>(value));
    }

    public Addition(String fieldName, Value<Addition> value) {
        super(fieldName, Type.ADD);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void accept(ChangeVisitor visitor) {
        CheckTool.checkNull(visitor, "visitor");
        visitor.visitAddition(this);
    }
}
