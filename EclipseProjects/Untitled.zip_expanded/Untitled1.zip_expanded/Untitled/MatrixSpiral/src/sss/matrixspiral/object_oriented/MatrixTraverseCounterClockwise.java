package sss.matrixspiral.object_oriented;

public class MatrixTraverseCounterClockwise extends MatrixTraverse {
	public void startPointAndDirectionInit(int[][] matrix) {
		switch (startPosition) {
			case TOP_LEFT :
				row = 0;
				col = 0;
				traverseDirection = TraverseDirection.DOWN;
				break;
			case TOP_RIGHT :
				row = 0;
				col = matrix[0].length - 1;
				traverseDirection = TraverseDirection.LEFT;
				break;
			case BOTTTOM_RIGHT :
				row = matrix.length - 1;
				col = matrix[0].length - 1;
				traverseDirection = TraverseDirection.UP;
				break;
			case BOTTOM_LEFT :
				row = matrix.length - 1;
				col = 0;
				traverseDirection = TraverseDirection.RIGHT;
				break;
			default :
				break;
		}
	}

    
    public void traverseUp() {
        decrementRow();
        if (isUpLimitExceed()) {
            incrementRow();
            decrementCol();
            decrementRightLimit();
            turnLeft();
        }
    }
    public void traverseLeft() {
        decrementCol();
        if (isLeftLimitExceed()) {
            incrementCol();
            incrementRow();
            incrementUpLimit();
            turnDown();
        }
    }
    public void traverseDown() {
        incrementRow();
        if (isDownLimitExceed()) {
            decrementRow();
            incrementCol();
            incrementLeftLimit();
            turnRight();

        }
    }
    public void traverseRight() {
        incrementCol();
        if (isRightLimitExceed()) {
            decrementCol();
            decrementRow();
            decrementDownLimit();
            turnUp();
        }
    }
}
