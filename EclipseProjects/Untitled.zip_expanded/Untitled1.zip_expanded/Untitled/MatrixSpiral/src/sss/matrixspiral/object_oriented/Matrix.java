package sss.matrixspiral.object_oriented;

public class Matrix {
	public int[][] rawMatrix;
	public int matrixCount;
    public int upLimit;
    public int downLimit;
    public int leftLimit;
	public int rightLimit;
    
    public Matrix(int[][] rawMatrix) {
    	this.rawMatrix = rawMatrix;
    	int rowSize = rawMatrix.length;
    	int colSize = rawMatrix[0].length;
        matrixCount = rowSize * colSize;

        // Our limit of movement which gets updated on every turn
        upLimit = 0;
        downLimit = rawMatrix.length - 1;
        leftLimit = 0;
        rightLimit = rawMatrix[0].length - 1;        
    }
}
