package org.iana.templates.def;


/**
 * This is an implementation of Visitor design pattern (GoF patterns) for
 * TemplateObjectInst hierarchy tree.
 *
 * @author Jakub Laszkiewicz
 */
public interface TemplateObjectDefVisitor {

    void visitSectionDef(SectionDef sd);

    void visitFieldDef(FieldDef fd);

    void visitListDef(ListDef ld);
}
