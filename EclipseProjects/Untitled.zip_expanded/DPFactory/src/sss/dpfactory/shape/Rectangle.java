package sss.dpfactory.shape;

class Rectangle implements Shape {

	/**
	 * This constructor has default access specifier so it won't be visible
	 * outside this package.
	 */
	Rectangle() { }
	
	@Override
	public void draw() {
		System.out.println("Inside Rectangle::draw() method.");
	}
}
