package sss.dpfactory.shape;

class Square implements Shape {

	/**
	 * This constructor has default access specifier so it won't be visible
	 * outside this package.
	 */
	Square() { }
	
	@Override
	public void draw() {
		System.out.println("Inside Square::draw() method.");
	}
}
