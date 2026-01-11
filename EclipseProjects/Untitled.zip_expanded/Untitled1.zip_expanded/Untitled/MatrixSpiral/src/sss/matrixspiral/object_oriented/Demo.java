package sss.matrixspiral.object_oriented;

public class Demo {
	public static void main(String[] args) {
		int[][] m = {{ 0,  1,  2,  3},
				     { 4,  5,  6,  7},
				     { 8,  9, 10, 11},
				     {12, 13, 14, 15}};
		int[]result = new int[16];
		MatrixTraverse matrixTraverse = new MatrixTraverseClockwise();
		matrixTraverse.setStartPosition(StartPosition.TOP_LEFT);
		result = matrixTraverse.traverseMatrix(m);
		for (int i : result) {
			System.out.print(i + " ");
		}
		
		System.out.println();
		
		matrixTraverse = new MatrixTraverseCounterClockwise();
		matrixTraverse.setStartPosition(StartPosition.BOTTTOM_RIGHT);
		result = matrixTraverse.traverseMatrix(m);
		for (int i : result) {
			System.out.print(i + " ");
		}
	}
}
