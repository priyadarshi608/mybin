package sss.dpvisitor_1.visitable;

import sss.dpvisitor_1.visitor.Visitor;

public class Circle implements Visitable {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
