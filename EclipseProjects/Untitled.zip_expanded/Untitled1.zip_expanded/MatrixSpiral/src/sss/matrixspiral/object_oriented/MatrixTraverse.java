package sss.matrixspiral.object_oriented;

public abstract class MatrixTraverse {
	private Matrix myMatrix;
	
	protected StartPosition startPosition;
    protected int row;
    protected int col;
    protected TraverseDirection traverseDirection;
    protected abstract void startPointAndDirectionInit(int[][] matrix);
    
    public abstract void traverseUp();
    public abstract void traverseLeft();
    public abstract void traverseDown();
    public abstract void traverseRight();
    
	public void setStartPosition(StartPosition startPosition) {
		this.startPosition = startPosition;
	}
	
    public int[] traverseMatrix(int[][] rawMatrix) {
    	myMatrix = new Matrix(rawMatrix);
        startPointAndDirectionInit(myMatrix.rawMatrix);
        final int[] retTraveserdMatrix = new int[myMatrix.matrixCount];
        for (int index = 0; index < myMatrix.matrixCount; index++) {
            retTraveserdMatrix[index] = myMatrix.rawMatrix[row][col];
            switch (traverseDirection) {
                case RIGHT :
                    traverseRight();
                    break;
                case DOWN :
                    traverseDown();
                    break;
                case LEFT :
                    traverseLeft();
                    break;
                case UP :
                    traverseUp();
                    break;
                default :
                    break;
            }
        }
        return retTraveserdMatrix;
    }
    protected void turnDown() {
        traverseDirection = TraverseDirection.DOWN;
    }
    protected void turnRight() {
        traverseDirection = TraverseDirection.RIGHT;
    }
    protected void turnLeft() {
        traverseDirection = TraverseDirection.LEFT;
    }
    protected void turnUp() {
        traverseDirection = TraverseDirection.UP;
    }
    public void decrementRow() {
        --row;
    }
    public boolean isUpLimitExceed() {
        return (row < myMatrix.upLimit);
    }
    public void incrementLeftLimit() {
        ++myMatrix.leftLimit;
    }
    public void incrementCol() {
        ++col;

    }
    public void incrementRow() {
        ++row;
    }
    public boolean isLeftLimitExceed() {
        return (col < myMatrix.leftLimit);
    }
    public void decrementCol() {
        --col;
    }
    public void decrementDownLimit() {
        --myMatrix.downLimit;
    }
    public void decrementRightLimit() {
        --myMatrix.rightLimit;
    }
    public boolean isDownLimitExceed() {
        return (row > myMatrix.downLimit);
    }
    public boolean isRightLimitExceed() {
        return (col > myMatrix.rightLimit);
    }

    public void incrementUpLimit() {
        ++myMatrix.upLimit;
    }
}
