package ubc.cosc322;

import java.util.*;

import ubc.cosc322.COSC322Test;

public class GameBoard {
	ArrayList<Integer> board = null;
	COSC322Test game = null;

	final int ROW_WIDTH = 11;
	final int OBSOLETE_COLUMNS = 12;

	public GameBoard(ArrayList<Integer> board, COSC322Test game) {
		this.board = board;
		this.game = game;
	}

	public void setBoard(ArrayList<Integer> board) {
		this.board = board;
	}

	public ArrayList<Integer> getBoard() {
		return board;
	}

	public int[] convertIndexToCoords(int index) {
		int temp = index;
		// remove first 12 coords
		temp += -OBSOLETE_COLUMNS;

		int y = (int) Math.floor(temp / ROW_WIDTH);

		int x = temp - (ROW_WIDTH * y) - 1;

		int[] coords = { x, y };
		return coords;
	}


	public ArrayList<Integer> getPossibleMoves(int colour) {
		ArrayList<Integer> tempBoard = board;
		ArrayList<Integer> validMoves = new ArrayList<Integer>();
		
		int opponent;
		if(colour == 1) opponent = 0;
		else opponent = 1;
		
		// 12 unused columns start there to get the first index of an in play square
		int index = OBSOLETE_COLUMNS;
		for(int i = index; i < tempBoard.size() - 1; i++) {
			if(i == 0 || i == opponent) {
				// add the index of this valid move in the board to the set of valid moves
				validMoves.add(i);
			}
		}
		return validMoves;
	}
	
	public int convertCoordsToIndex(int x, int y) {
		// first 12 coords are useless
		int coordinate = OBSOLETE_COLUMNS;

		// add 11 for each complete row we should skip
		coordinate += (y - 1) * ROW_WIDTH;
		// add the x value of the coordinate to the index + 1 for the not in play column
		coordinate += x + 1;

		return coordinate;
	}

	public boolean movePiece(int qx1, int qy1, int qx2, int qy2, int ax, int ay, int colour) {
		if (qx1 <= 10 && qx1 > 0 && qy1 < 10 && qy1 > 0) {
			int queenStart = convertCoordsToIndex(qx1, qy1);
			int queenFinish = convertCoordsToIndex(qx2, qy2);
			int arrow = convertCoordsToIndex(ax, ay);

			ArrayList<Integer> newBoard = board;
			newBoard.set(queenStart, 0);
			if (newBoard.get(queenFinish) == 0) {
				newBoard.set(queenFinish, colour);
			}
			newBoard.set(arrow, 3);

			game.sendPlay(qx1, qy1, qx2, qy2, ax, ay);
			return true;
		}
		return false;
	}
}