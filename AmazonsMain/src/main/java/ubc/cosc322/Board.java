package ubc.cosc322;
import java.util.*;

/**
 * @author pie-d
 *
 */
public class Board {
	int[][] board;
	
	public Board(ArrayList<Integer> board) {
		this.board = convertTo2DArray(board);
	}
	
	public void setBoard(ArrayList<Integer> board) {
		this.board = convertTo2DArray(board);
	}
	
	public int[][] getBoard(){
		return board;
	}
	
	
	/**
	 * @return 
	 *
	public Map<String, ArrayList<Integer>> getPieces(){
		 
		
		for (int x = 1; x < 11; x++)
			for (int y = 1; y < 11; y++) {
				if (board.get(y*10) == 1) {
					
				}	
				else if (board.get(x*y + y) == 2) {
					
				}	
			}
		
		return queenLocations;
	}
	*/
	
	/**
	 * @param board as a one-dimensional ArrayList<Integer>
	 * @return A board configuration as a 2-D array. The zero-th row and zero-th column are still obsolete, so that the first index refers to the first row (or column)
	 */
	public static int[][] convertTo2DArray(ArrayList<Integer> board){
		int[][] newBoard = new int[11][11];
		
		for (int y = 0; y < 11; y++)
			for (int x = 0; x < 11; x++)
				newBoard[x][y] = board.get(y*11 + x);
		
		return newBoard;
	}
	
	public void generateRandomMove(int team) {
		
	}
}
