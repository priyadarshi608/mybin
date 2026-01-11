package sss.dpfactory.shape;

public class ShapeFactory {

	//use getShape method to get object of type shape 
	public Shape getShape(TypeOfShape typeOfShape){
//		if(shapeType == null){
//			return null;
//		}		
		if(typeOfShape == TypeOfShape.CIRCLE){
			return new Circle();

		} else if(typeOfShape == TypeOfShape.RECTANGLE){
			return new Rectangle();

		} else if(typeOfShape == TypeOfShape.SQUARE){
			return new Square();
		}

		return null;
	}
}
