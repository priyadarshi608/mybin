import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Solution {
    static final String TOP_TO_BOTTOM = "TOP_TO_BOTTOM";
    static final String LEFT_TO_RIGHT = "LEFT_TO_RIGHT";
    static final String DOWN_TO_UP = "DOWN_TO_UP";
    static final String RIGHT_TO_LEFT = "RIGHT_TO_LEFT";
    static String direction = TOP_TO_BOTTOM;

    static String getTransformedString(String str, int size) {
    	ExecutorService executorService = Executors.newFixedThreadPool(5);
        Character[][] charMatrix = new Character[size][size];
        char[] strCharArray = str.toCharArray();

        int inputIndex = 0;
        int rowIndex = -1;
        int columnIndex = 0;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strCharArray.length; i++) {
            if (direction == TOP_TO_BOTTOM) {
                if (rowIndex < size - 1 && charMatrix[rowIndex + 1][columnIndex] == null) {
                    rowIndex++;
                    charMatrix[rowIndex][columnIndex] = strCharArray[i];
                } else {
                    direction = LEFT_TO_RIGHT;
                }
            }
            if (direction == LEFT_TO_RIGHT) {
                if (columnIndex < size - 1 && charMatrix[rowIndex][columnIndex + 1] == null) {
                    columnIndex++;
                    charMatrix[rowIndex][columnIndex] = strCharArray[i];
                } else {
                    direction = DOWN_TO_UP;
                }
            }
            if (direction == DOWN_TO_UP) {
                if (rowIndex > 0 && charMatrix[rowIndex - 1][columnIndex] == null) {
                    rowIndex--;
                    charMatrix[rowIndex][columnIndex] = strCharArray[i];
                } else {
                    direction = RIGHT_TO_LEFT;
                }
            }
            if (direction == RIGHT_TO_LEFT) {
                if (columnIndex > 0 && charMatrix[rowIndex][columnIndex - 1] == null) {
                    columnIndex--;
                    charMatrix[rowIndex][columnIndex] = strCharArray[i];
                } else {
                    direction = TOP_TO_BOTTOM;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (charMatrix[i][j] != null) {
                    result.append(charMatrix[i][j]);
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3); // Adjust the pool size based on the number of inputs

        String[][] inputs = {{"ARCESIUMISHIRING", "3"}, {"ARCESIUMISHIRING", "4"}, {"HELLO", "6"}};

        for (String[] input : inputs) {
            executorService.execute(() -> {
                String transformedString = getTransformedString(input[0], Integer.parseInt(input[1]));
                System.out.println(transformedString);
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
