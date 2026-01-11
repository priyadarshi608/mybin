package sss.matrixspiral.object_oriented;

public class MatrixTraverseClockwise extends MatrixTraverse {
	public void startPointAndDirectionInit(int[][] matrix) {
		switch (startPosition) {
			case TOP_LEFT :
				row = 0;
				col = 0;
				traverseDirection = TraverseDirection.RIGHT;
				break;
			case TOP_RIGHT :
				row = 0;
				col = matrix[0].length - 1;
				traverseDirection = TraverseDirection.DOWN;
				break;
			case BOTTTOM_RIGHT :
				row = matrix.length - 1;
				col = matrix[0].length - 1;
				traverseDirection = TraverseDirection.LEFT;
				break;
			case BOTTOM_LEFT :
				row = matrix.length - 1;
				col = 0;
				traverseDirection = TraverseDirection.UP;
				break;
			default :
				break;
		}
	}

	public void traverseUp() {
		decrementRow();
		if (isUpLimitExceed()) {
			incrementRow();
			incrementCol();
			incrementLeftLimit();
			turnRight();
		}
	}
	public void traverseLeft() {
		decrementCol();
		if (isLeftLimitExceed()) {
			incrementCol();
			decrementRow();
			decrementDownLimit();
			turnUp();
		}
	}
	public void traverseDown() {
		incrementRow();
		if (isDownLimitExceed()) {
			decrementRow();
			decrementCol();
			decrementRightLimit();
			turnLeft();
		}
	}
	public void traverseRight() {
		incrementCol();
		if (isRightLimitExceed()) {
			decrementCol();
			incrementRow();
			incrementUpLimit();
			turnDown();
		}
	}
}
