package ubc.cosc322;
import java.util.*;

import ubc.cosc322.COSC322Test;

/**
 * @author Vaughn Janes, Nick McGee, Erik Johnston, Ann Ni 
 *	A class for easy manipulation/analysis of the game board for Game of the Amazons
 */
public class Board {
	private int[][] board;
	private COSC322Test game;
	
	/**
	 * Default constructor. The class won't work properly if this constructor is used and the inner board field is never set,
	 * but I made this so that a Board can be instantiated within COSC322Test before a gamestate is provided.
	 */
	public Board() {
	}
	
	/** Constructor for the one-dimensional ArrayList<Integer> format.
	 * @param gamestate board in one-dimensional format.
	 */
	public Board(ArrayList<Integer> gamestate, COSC322Test game) {
		this.board = convertTo2DArray(gamestate);
		this.game = game;
	}
	
	/** Constructor for 2D int array.
	 * @param board gamestate board in 2D int array format.
	 */
	public Board(int[][] board) {
		System.out.println("!!!! - " +  board);
		this.board = board;
	}
	
	/** Setter method for inner board variable
	 * @param board in 1D ArrayList format
	 */
	public void setBoard(ArrayList<Integer> board) {
		this.board = convertTo2DArray(board);
	}
	
	/** Setter method for inner board variable
	 * @param board in 2D int array format
	 */
	public void setBoard(int[][] board) {
		this.board = board;
	}
	
	/** Getter method for inner board variable.
	 * @return the board as a primitive 2D int array.
	 */
	public int[][] getBoard(){
		return board;
	}
	
	/**
	 * @param x int x coordinate
	 * @param y int y coordinate
	 * @return The value of the game board at coords x, y. Returns -1 if out of bounds.
	 */
	public int get(int x, int y) {
		if (x > 0 && y > 0 && x < 11 && y < 11) //Checks if coords "out of bounds" (quotation marks, because 0th column and 0th row count as being out of bounds)
			return board[x][y];
		else
			return -1;
	}
	
	/**
	 * @param team Team number
	 * @return An ArrayList of coordinates x, y (as ArrayLists of size 2) for each queen on the specified team
	 */
	public ArrayList<int[]> getQueenCoords(int team){
		ArrayList<int[]> queenLocations = new ArrayList<>(4);
		
		for (int y = 0; y < board.length; y++)
			for (int x = 0; x < board[y].length; x++)
				if (board[x][y] == team)
					queenLocations.add(new int[] {x, y});
					
		return queenLocations;
	}
	
	/**
	 * @param board as a one-dimensional ArrayList<Integer>
	 * @return A board configuration as a primitive 2D int array. The zero-th row and zero-th column are still obsolete, so that the first index refers to the first row (or column). In other words, coords start at 1.
	 */
	public static int[][] convertTo2DArray(ArrayList<Integer> board){
		int[][] newBoard = new int[11][11];
		
		for (int y = 0; y < 11; y++)
			for (int x = 0; x < 11; x++) 
				newBoard[y][x] = board.get(y*11 + x);

		return newBoard;
	}
	
	/** Updates the board class with new moves
	 * @param qx1 Original queen x position
	 * @param qy1 Original queen y position
	 * @param qx2 New queen x position
	 * @param qy2 New queen y position
	 * @param ax Arrow position
	 * @param ax Arrow position
	 * @return True if move within range of board, false if move is not within range of the board
	 */
	public void movePiece(int qx1, int qy1, int qx2, int qy2, int ax, int ay, int colour) {
		// NOTE: Commented out if because it was rejecting some moves that it shouldn't
		
//		if (qx1 <= 10 && qx1 > 0 && qy1 < 10 && qy1 > 0) {
			int[][] newBoard = board;
			newBoard[qx1][qy1] = 0;
			if (newBoard[qx2][qy2] == 0) {
				newBoard[qx2][qy2] = colour;
			}
			newBoard[ax][ay] = 3;

			setBoard(newBoard);
			
//			System.out.println("From board: Move made.");
//			return true;
//		}
		
//		System.out.println("From board: Move not made.");
//		return false;
	}
	
	/** 
	 * @param none
	 * @return A string that represents the game board. 1 = black, 2 = white, 3 = arrow
	 */
	public String toString() {
		String boardString = "";
		for(int i = 10; i > 0; i--) {
			for(int j = 1; j < 11; j++) 
				boardString += Integer.toString((board[i][j]));
			boardString += "\n";
		}
		return boardString;
	}
}
