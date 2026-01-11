package sss.dpfactory.shape;

class Circle implements Shape {

	/**
	 * This constructor has default access specifier so it won't be visible
	 * outside this package.
	 */
	Circle() { }
	
	@Override
	public void draw() {
		System.out.println("Inside Circle::draw() method.");
	}
}
