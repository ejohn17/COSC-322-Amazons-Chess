package ubc.cosc322;
import java.util.*;

/**
 * @author pie-d
 *
 */
public class Board {
	private int[][] board;
	
	/** Constructor for the one-dimensional ArrayList<Integer> format.
	 * @param gamestate board in one-dimensional format.
	 */
	public Board(ArrayList<Integer> gamestate) {
		this.board = convertTo2DArray(gamestate);
	}
	
	/** Constructor for 2D int array.
	 * @param board gamestate board in 2D int array format.
	 */
	public Board(int[][] board) {
		this.board = board;
	}
	
	public void setBoard(ArrayList<Integer> board) {
		this.board = convertTo2DArray(board);
	}
	
	public void setBoard(int[][] board) {
		this.board = board;
	}
	
	/**
	 * @return the board as a primitive 2D int array.
	 */
	public int[][] getBoard(){
		return board;
	}
	
	
	/**
	 * @param team Team number
	 * @return An ArrayList of coordinates x, y (as ArrayLists of size 2) for each queen on the specified team
	 */
	public ArrayList<ArrayList<Integer>> getQueenCoords(int team){
		ArrayList<ArrayList<Integer>> queenLocations = new ArrayList<>(4);
		
		for (int y = 0; y < board.length; y++)
			for (int x = 0; x < board[y].length; x++)
				if (board[x][y] == team)
					queenLocations.add(new ArrayList<Integer>(Arrays.asList(x, y)));
					
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
				newBoard[x][y] = board.get(y*11 + x);
		
		return newBoard;
	}
}
