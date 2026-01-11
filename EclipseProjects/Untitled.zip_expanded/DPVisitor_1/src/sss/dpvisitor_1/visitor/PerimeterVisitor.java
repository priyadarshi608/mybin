package sss.dpvisitor_1.visitor;

import sss.dpvisitor_1.visitable.Circle;
import sss.dpvisitor_1.visitable.Square;
import sss.dpvisitor_1.visitable.Triangle;

public class PerimeterVisitor implements Visitor {
	
    @Override
    public void visit(Circle circle) {
        double perimeter = 2 * Math.PI * circle.getRadius();
        System.out.println("Circle - Perimeter: " + perimeter);
    }

    @Override
    public void visit(Square square) {
        double perimeter = 4 * square.getSide();
        System.out.println("Square - Perimeter: " + perimeter);
    }

    @Override
    public void visit(Triangle triangle) {
        double perimeter = triangle.getBase() + 2 * Math.sqrt((triangle.getBase() * triangle.getBase() / 4) + triangle.getHeight() * triangle.getHeight());
        System.out.println("Triangle - Perimeter: " + perimeter);
    }
}
