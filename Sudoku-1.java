package quizRecursion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This is a Sudoku Solver that solves a partial Sudoku Board using
 * recursion. The Sudoku solver fills a boardSize × boardSize
 * (boardSize = 9 by default) grid with digits so that each column,
 * each row, and each of the blockSize × blockSize (boardSize =
 * blockSize × blockSize while blockSize = 3 by default) subgrids
 * contains all of the digits from 1 to 9.
 * <p>
 * You need to implement the solve(SudokuBoard b, int currentSquare)
 * method using recursion, and isSafe(int digit, int square) method in
 * SudokuBoardSoln class. Everything else is given to you.
 * </p>
 * <p>
 * Don't change other given methods/fields. You can add some private
 * helper methods but you may not add new public methods or fields.
 * </p>
 */
public class Sudoku {

    public static int noSolves = 0;
    public static int boardSize;

    @SuppressWarnings("resource")
    public static void main(String[] args)
        throws FileNotFoundException {
        // Initialize board
        Scanner sc = new Scanner(new File ("sudoku.in"));

        boolean more = true;
        while (more) {
            int n = sc.nextInt();
            boardSize = n * n;
            int[][] initial = new int[boardSize][boardSize];

            for (int cell = 0; cell < boardSize * boardSize; cell++) {
                initial[cell / boardSize][cell % boardSize] = sc.nextInt();
            }
            solve(initial);
            more = sc.hasNextInt();
            if (more) {
                System.out.println(); // Print blank line between test
                                      // cases
            }
        }
    }

    public static void solve(int[][] initial) {
        if (boardSize == 1) {
            System.out.println(1);
            return;
        }
        SudokuBoardSoln brd = new SudokuBoardSoln(boardSize, initial);
        if (solve(brd, brd.nextEmpty(-1))) {
            brd.print();
        }
        else {
            System.out.println("NO SOLUTION");
        }
    }

    public static boolean solve(SudokuBoardSoln brdSoln, int currentSquare) {
        noSolves++; // For debugging
        // currentSquare increases from 0 to boardSize^2 - 1 -- first
        // along a row, then downwards.

        // IF the currentSquare placement is at square boardSize^2, we are done (RETURN TRUE).
        // ELSE
        //     LOOP try out all 'boardSize' numbers in the current square.
        //         IF any number is safe to place there
        //             Place it.
        //             Call solve with the next square without an initial value.
        //             IF above returns TRUE
        //                 Done (RETURN TRUE).
        //             ELSE
        //                 Remove the placement, (and try the next possible digit).
        //     END LOOP
        // RETURN FALSE (if we fall out of the LOOP without returning true, we have exhausted 1 - boardSize)

        // TODO: IMPLEMENT THE PSEUDOCODE ABOVE AND COMPLETE THIS
        // METHOD
        
        if(currentSquare == (boardSize * boardSize)) return true;
        else {
        	for(int i = 1; i <= boardSize; i++) {
        		if(brdSoln.isSafe(i, currentSquare)) {
        			brdSoln.set(i, currentSquare);
        			boolean is_solution = solve(brdSoln, brdSoln.nextEmpty(currentSquare));
        			if(is_solution) return true;
        			else {
        				brdSoln.remove(currentSquare);
        			}
        		}
        	}
        }
        return false;
    }
}

class SudokuBoardSoln {

    int[][] board;
    boolean[][] initial;
    int boardSize;
    int blockSize;

    public SudokuBoardSoln(int boardSize) {
        this.boardSize = boardSize;
        blockSize = (int) (Math.sqrt(boardSize) + 1e-3);
        board = new int[boardSize][boardSize];
    }

    public SudokuBoardSoln(int boardSize, int [][] starting) {
        this(boardSize);
        initial = new boolean[boardSize][boardSize]; // Is a square is
                                                     // initialized?
                                                     // Iff not,
                                                     // program may
                                                     // change its
                                                     // value.
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize ; c++) {
                if (starting[r][c] != 0) {
                    initial[r][c] = true;
                }
            }
        }

        for (int r = 0; r < boardSize; r++) {
            board[r] = Arrays.copyOf(starting[r], boardSize);
        }
    }

    public void set(int digit, int square) {
        board[square / boardSize][square % boardSize] = digit;
    }

    public void remove(int square) {
        set(0, square);
    }

    /*
     * Given a square, find the next one that was not initialized.
     */
    public int nextEmpty(int square) {
        square++;
        if (square == boardSize * boardSize) {
            return square;
        }
        int row = square / boardSize;
        int col = square % boardSize;
        while (initial[row][col]) {
            square++;
            if (square == boardSize * boardSize) {
                return square;
            }
            row = square / boardSize;
            col = square % boardSize;
        }
        return square;
    }

    /*
     * Whether safe to place digit 'digit' at location 'square'.
     */
    public boolean isSafe(int digit, int square) {
        // TODO: If putting 'digit' at location 'square' causes
        // duplicate number at its row, return false
    	int row = square / boardSize;
    	int col = square % boardSize;
    	for(int r = 0; r < boardSize; r++) {
    		if(board[row][r] == digit) return false;
    	}
        // TODO: If putting 'digit' at location 'square' causes
        // duplicate number at its column, return false
    	for(int c = 0; c < boardSize; c++) {
    		if(board[c][col] == digit) return false;
    	}

        // TODO: If putting 'digit' at location 'square' causes
        // duplicate number at its inner 3*3 (any blockSize by
        // blockSize) subgrid, return false
    	int r = row - row%blockSize;
    	int c = col - col%blockSize;
    	for(int i = r; i < r+blockSize; i++) {
    		for(int j = c; j < c+blockSize; j++) {
    			if(board[i][j] == digit) return false;
    		}
    	}
        // Hint: If there is a duplicate number, it means that one
        // number is missing (e.g. If we have two 1s in range of
        // [1..9], it means one of the other numbers is missing as we
        // can have at most 9 empty cells).
        return true;
    }

    public void print() {
        for (int r = 0; r < boardSize; r++) {
            String blank = "";
            for (int c = 0; c < boardSize; c++) {
                if (board[r][c] == 0) {
                    System.out.print(blank + "*");
                }
                else {
                    System.out.print(blank + board[r][c]);
                }
                blank = " ";
            }
            System.out.println();
        }
    }
}
