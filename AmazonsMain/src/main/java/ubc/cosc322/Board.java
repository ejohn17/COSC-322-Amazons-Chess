package ubc.cosc322;

import java.util.*;
import ubc.cosc322.COSC322Test;

/**
 * @author pie-d
 *
 */
public class Board {
	int[][] board;
	COSC322Test game = null;
	
	final int ROW_WIDTH = 11;
	final int OBSOLETE_COLUMNS = 12;

	public Board(ArrayList<Integer> board, COSC322Test game) {
		this.board = convertTo2DArray(board);
		this.game = game;
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
	
	public ArrayList<Integer[]> getPossibleMoves(int colour) {
		int[][] tempBoard = board;
		ArrayList<Integer[]> validMoves = new ArrayList<Integer[]>();
		
		int opponent;
		if(colour == 1) opponent = 0;
		else opponent = 1;
		
		// i = y
		for(int i = 0; i < tempBoard.length; i++) {
			// j = x
			for(int j = 0; j < tempBoard[i].length; j++) {
				if(tempBoard[i][j] == 0 || tempBoard[i][j] == opponent) {
					Integer[] temp = {j, i};
					// add coordinate in form (x, y) into the valid moves set
					validMoves.add(temp);
				}
			}
		}
		
		return validMoves;
	}
	
	public boolean movePiece(int qx1, int qy1, int qx2, int qy2, int ax, int ay, int colour) {
		if (qx1 <= 10 && qx1 > 0 && qy1 < 10 && qy1 > 0) {
			int[][] newBoard = board;
			newBoard[qx1][qy1] = 0;
			if (newBoard[qx2][qy2] == 0) {
				newBoard[qx2][qy2] = colour;
			}
			newBoard[ax][ay] = 3;

			game.sendPlay(qx1, qy1, qx2, qy2, ax, ay);
			return true;
		}
		return false;
	}
}
