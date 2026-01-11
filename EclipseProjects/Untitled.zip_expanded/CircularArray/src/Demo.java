public class Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 10;
		CircularArray<String> array = new CircularArray<String>(size);
		for (int i = 0; i < size; i++) {
			array.set(i, String.valueOf(i));
		}

		for (int i = 0; i < size; i++) {
			System.out.print(array.get(i) + " ");
		}

		System.out.println();
		
		array.rotateLeft(22);
		for (int i = 0; i < size; i++) {
			System.out.print(array.get(i) + " ");
		}
		
		System.out.println();
		
		array.rotateRight(33);
		for (int i = 0; i < size; i++) {
			System.out.print(array.get(i) + " ");
		}
		
		System.out.println();
		
		array.rotateLeft(4);
		for (String s : array) {
			System.out.print(s + " ");
		}
	}

}
