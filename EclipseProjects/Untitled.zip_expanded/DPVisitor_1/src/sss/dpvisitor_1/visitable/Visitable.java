package sss.dpvisitor_1.visitable;

import sss.dpvisitor_1.visitor.Visitor;

public interface Visitable {
    void accept(Visitor visitor);
}
