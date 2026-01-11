// Java program to rotate a matrix 
import java.lang.*; 
import java.util.*; 

public class MatrixRotate {
	static int R = 4; 
	static int C = 4; 

	// A function to rotate a matrix 
	// mat[][] of size R x C. 
	// Initially, m = R and n = C 
	static void rotatematrix(int m, int n, int mat[][]) {
		int row = 0, col = 0; 
		int prev = -1, curr = -1;

		/* 
		row - Staring row index 
		m - ending row index 
		col - starting column index 
		n - ending column index 
		i - iterator 
		*/
		while (row < m && col < n) {
			
			printInfo(row, col, prev, curr, mat, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			
			if (row + 1 == m || col + 1 == n) 
				break; 
	
			// Store the first element of next row, this element will replace 
			// first element of current row 
			prev = mat[row + 1][col]; 
	
			printInfo(row, col, prev, curr, mat, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
			
			// Move elements of first row 
			// from the remaining rows 
			for (int i = col; i < n; i++) 
			{ 
				curr = mat[row][i]; 
				mat[row][i] = prev; 
				prev = curr; 
				printInfo(row, col, prev, curr, mat, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			} 
			row++;
			printInfo(row, col, prev, curr, mat, "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
			
			// Move elements of last column 
			// from the remaining columns 
			for (int i = row; i < m; i++) 
			{ 
				curr = mat[i][n-1]; 
				mat[i][n-1] = prev; 
				prev = curr; 
				printInfo(row, col, prev, curr, mat, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			} 
			n--;
			printInfo(row, col, prev, curr, mat, "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
			
			// Move elements of last row from the remaining rows 
			if (row < m) 
			{ 
				printInfo(row, col, prev, curr, mat, "==========================================================");
				for (int i = n-1; i >= col; i--) 
				{ 
					curr = mat[m-1][i]; 
					mat[m-1][i] = prev; 
					prev = curr; 
					printInfo(row, col, prev, curr, mat, "######################################################");
				} 
			} 
			m--;
			printInfo(row, col, prev, curr, mat, "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
			
			// Move elements of first column 
			// from the remaining rows 
			if (col < n) 
			{ 
				printInfo(row, col, prev, curr, mat, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
				for (int i = m-1; i >= row; i--) 
				{ 
					curr = mat[i][col]; 
					mat[i][col] = prev; 
					prev = curr;
					printInfo(row, col, prev, curr, mat, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				} 
			}
			col++;
			printInfo(row, col, prev, curr, mat, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
		} 

	} 
	
	private static void printInfo(int row, int col, int prev, int curr, int[][] mat, String delimiter) {
		System.out.println(delimiter);
		System.out.println("row: " + row);
		System.out.println("col: " + col);
		System.out.println("prev: " + prev);
		System.out.println("curr: " + curr);
		printMatrix(mat);
		System.out.println(delimiter);
	}
	
	private static void printMatrix(int[][] mat) {
		// Print rotated matrix 
		for (int i = 0; i < R; i++) 
		{ 
			for (int j = 0; j < C; j++) 
			System.out.print( mat[i][j] + " "); 
			System.out.print("\n"); 
		} 
	}

/* Driver program to test above functions */
	public static void main(String[] args) 
	{ 
	// Test Case 1 
	int a[][] = { {1, 2, 3, 4}, 
				  {5, 6, 7, 8}, 
				  {9, 10, 11, 12}, 
				  {13, 14, 15, 16} }; 

	// Tese Case 2 
	/* int a[][] = new int {{1, 2, 3}, 
							{4, 5, 6}, 
							{7, 8, 9} 
						};*/
	rotatematrix(R, C, a);
	printMatrix(a);
	} 
} 
