package org.iana.templates.inst;


/**
 * This is an implementation of Visitor design pattern (GoF patterns) for
 * TemplateObjectInst hierarchy tree.
 *
 * @author Jakub Laszkiewicz
 */
public interface TemplateObjectInstVisitor {

    void visitFieldInst(FieldInst fi);

    void visitSectionInst(SectionInst si);

    void visitListInst(ListInst li);
}
