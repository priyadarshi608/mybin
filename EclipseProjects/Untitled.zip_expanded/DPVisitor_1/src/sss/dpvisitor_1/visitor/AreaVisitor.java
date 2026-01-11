package sss.dpvisitor_1.visitor;

import sss.dpvisitor_1.visitable.Circle;
import sss.dpvisitor_1.visitable.Square;
import sss.dpvisitor_1.visitable.Triangle;

public class AreaVisitor implements Visitor {
    @Override
    public void visit(Circle circle) {
        double area = Math.PI * circle.getRadius() * circle.getRadius();
        System.out.println("Circle - Area: " + area);
    }

    @Override
    public void visit(Square square) {
        double area = square.getSide() * square.getSide();
        System.out.println("Square - Area: " + area);
    }

    @Override
    public void visit(Triangle triangle) {
        double area = 0.5 * triangle.getBase() * triangle.getHeight();
        System.out.println("Triangle - Area: " + area);
    }
}
