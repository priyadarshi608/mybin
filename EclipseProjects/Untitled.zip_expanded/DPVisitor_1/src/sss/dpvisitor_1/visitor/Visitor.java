package sss.dpvisitor_1.visitor;

import sss.dpvisitor_1.visitable.Circle;
import sss.dpvisitor_1.visitable.Square;
import sss.dpvisitor_1.visitable.Triangle;

public interface Visitor {
    void visit(Circle circle);
    void visit(Square square);
    void visit(Triangle triangle);
}
