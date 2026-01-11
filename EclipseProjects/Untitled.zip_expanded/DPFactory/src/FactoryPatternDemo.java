import sss.dpfactory.shape.*;

public class FactoryPatternDemo {

	public static void main(String[] args) {
		ShapeFactory shapeFactory = new ShapeFactory();

		// Creation of objects of concrete classes of Shape is not possible here.
		// Circle circle = new Circle();

		//get an object of Circle and call its draw method.
		Shape shape1 = shapeFactory.getShape(TypeOfShape.CIRCLE);

		//call draw method of Circle
		shape1.draw(); // This is not the method of object of circle class
		// but of Shape interface.

		//get an object of Rectangle and call its draw method.
		Shape shape2 = shapeFactory.getShape(TypeOfShape.RECTANGLE);

		//call draw method of Rectangle
		shape2.draw();

		//get an object of Square and call its draw method.
		Shape shape3 = shapeFactory.getShape(TypeOfShape.SQUARE);

		//call draw method of circle
		shape3.draw();
	}
}
