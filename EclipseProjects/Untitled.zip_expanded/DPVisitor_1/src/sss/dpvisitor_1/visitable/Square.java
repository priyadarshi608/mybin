package sss.dpvisitor_1.visitable;

import sss.dpvisitor_1.visitor.Visitor;

public class Square implements Visitable {
    private double side;

    public Square(double side) {
        this.side = side;
    }

    public double getSide() {
        return side;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
