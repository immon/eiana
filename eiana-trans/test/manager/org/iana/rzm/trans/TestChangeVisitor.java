/**
 * org.iana.rzm.trans.TestVisitor
 * (C) Research and Academic Computer Network - NASK
 * lukaszz, 2007-03-14, 14:45:57
 */
package org.iana.rzm.trans;

import org.iana.rzm.trans.change.*;

import java.util.List;
import java.util.ArrayList;

public class TestChangeVisitor implements ChangeVisitor {

    List<Change> chaneges = new ArrayList<Change>();
    TestValueVisitor valueVisitor = new TestValueVisitor(this);

    public void visitAddition(Addition add) {
        chaneges.add(add);
        System.out.println("*********Addition***********");
        System.out.println("Dodano :"+add.getFieldName());
        System.out.println(add);
        add.getValue().accept(valueVisitor);
        System.out.println("*********End Addition*******");
    }

    public void visitRemoval(Removal rem) {
        System.out.println("*********Removal************");
        System.out.println("Usunieto :"+rem.getFieldName());
        System.out.println(rem);
        chaneges.add(rem);
        rem.getValue().accept(valueVisitor);
        System.out.println("********End Removal*********");
    }

    public void visitModification(Modification mod) {
        System.out.println("*********Modification********");
        System.out.println("Zmodyfikowano :"+mod.getFieldName());
        System.out.println(mod);
        chaneges.add(mod);
        mod.getValue().accept(valueVisitor);
        System.out.println("*********End Modification***");
    }

    public void printVisitedchanges(){
        for (Change change : chaneges)
            System.out.println(change);
        for(ModifiedPrimitiveValue mod :valueVisitor.getMods())
            System.out.println("---ModifiedPrimitiveValue---\n"+" newVal ="+mod.getNewValue()+"\n oldVal ="+mod.getOldValue()+" \n------------");
        for(PrimitiveValue mod :valueVisitor.getPrimit())
            System.out.println("---PrimitiveValue---\n"+" newVal ="+mod.getValue()+"\n------------");
    }

    class TestValueVisitor implements ValueVisitor{

        ChangeVisitor parentVisitor;

        List<PrimitiveValue> primit = new ArrayList<PrimitiveValue>();

        List<ModifiedPrimitiveValue> mods = new ArrayList<ModifiedPrimitiveValue>();

        TestValueVisitor(ChangeVisitor chVis){
            parentVisitor=chVis;        
        }


        public void visitPrimitiveValue(PrimitiveValue value) {
            System.out.println("---PrimitiveValue-----------\n"+
                               " newVal ="+value.getValue()+
                               "\n----------------------------");
            primit.add(value);
        }

        public void visitModifiedPrimitiveValue(ModifiedPrimitiveValue value) {
            System.out.println("---ModifiedPrimitiveValue---\n"+
                               " newVal ="+value.getNewValue()+"\n " +
                               "oldVal ="+value.getOldValue()+
                               "\n----------------------------");
            mods.add(value);
        }

        public<T extends Change> void visitObjectValue(ObjectValue<T> value) {
            System.out.println("--ObjectValue---------------");
            System.out.println("[[[[[[[[[[[[[[[[[[[[[[[[[[[[");
            for(Change ch : value.getChanges())
                ch.accept(parentVisitor);

            System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
            System.out.println("----------------------------");
        }

        public List<PrimitiveValue> getPrimit() {
            return primit;
        }

        public List<ModifiedPrimitiveValue> getMods() {
            return mods;
        }
    }
}
