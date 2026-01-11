import sss.dpvisitor_1.visitable.Visitable;
import sss.dpvisitor_1.visitable.Circle;
import sss.dpvisitor_1.visitable.Square;
import sss.dpvisitor_1.visitable.Triangle;
import sss.dpvisitor_1.visitor.AreaVisitor;
import sss.dpvisitor_1.visitor.PerimeterVisitor;

public class VisitorPatternDemo {
    public static void main(String[] args) {
        Visitable[] shapes = {new Circle(5), new Square(4), new Triangle(3, 4)};

        AreaVisitor areaVisitor = new AreaVisitor();
        for (Visitable shape : shapes) {
            shape.accept(areaVisitor);
        }

        PerimeterVisitor perimeterVisitor = new PerimeterVisitor();
        for (Visitable shape : shapes) {
            shape.accept(perimeterVisitor);
        }
    }
}
