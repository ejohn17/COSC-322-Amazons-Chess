package ubc.cosc322;
import java.util.*;


/**
 * @author Vaughn Janes, Nick McGee, Erik Johnston, Ann Ni 
 *	A class for easy manipulation/analysis of the game board for Game of the Amazons
 */
public class Board {
	private int[][] board;
	
	/**
	 * Default constructor. The class won't work properly if this constructor is used and the inner board field is never set,
	 * but I made this so that a Board can be instantiated within COSC322Test before a gamestate is provided.
	 */
	public Board() {
	}
	
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
		//this.board = board;
		this.board = Arrays.copyOf(board, board.length);  //uncomment this and delete the other line once everything is working
	}
	
	/**
	 * @param otherBoard The board to return a copy of
	 * @return A copy of the argued Board.
	 */
	public static Board copyOf(Board otherBoard) {
		int[][] temp = otherBoard.getInnerBoardArray();
		int[][] newInnerBoard = new int[11][11];
		for (int i = 0; i < temp.length; i++)
			for (int j = 0; j < temp[0].length; j++)
				newInnerBoard[i][j] = Integer.valueOf(temp[i][j]);
				
		return new Board(newInnerBoard);
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
		this.board = Arrays.copyOf(board, board.length);
	}
	
	/** Getter method for inner board variable.
	 * @return the board, by value, as a primitive 2D int array WITH ELEMENTS AT CONVENTIONAL COORDINATES.
	 */
	public int[][] getInnerBoardArray(){
		return Arrays.copyOf(board, board.length);
	}
	
	/** TAKES CONVENTIONAL ARRAY-COORDINATE INPUT.
	 * @param x int x coordinate
	 * @param y int y coordinate
	 * @return True if successful, else false.
	 */
	public boolean set(int x, int y, int val) {
		if (x > 0 && y > 0 && x < 11 && y < 11) { //Checks if coords "out of bounds" (quotation marks, because 0th column and 0th row do exist but count as being out of bounds)
			board[x][y] = Integer.valueOf(val);
			return true;
		}
		else
			return false;
	}
	
	/** TAKES CONVENTIONAL ARRAY-COORDINATE INPUT.
	 * @param x int x coordinate
	 * @param y int y coordinate
	 * @return The value of the game board at coords x, y. Returns -1 if out of bounds.
	 */
	public int get(int x, int y) {
		if (x > 0 && y > 0 && x < 11 && y < 11) //Checks if coords "out of bounds" (quotation marks, because 0th column and 0th row count as being out of bounds)
			return Integer.valueOf(board[x][y]);
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
		
		//Don't touch this hackjob (It converts the server's retarded one-dimensional gamestate array into one that isn't upside down. Also moves the obsolete row back to the top of the 2D array after flipping.)
		for (int y = 1; y < 11; y++)
			for (int x = 0; x < 11; x++)
				newBoard[x][11 - y] = Integer.valueOf(board.get(y*11 + x));
		//moving obsolete row back to the top:
		for (int x = 0; x < 11; x++)
			newBoard[x][0] = Integer.valueOf(board.get(x));
		
		return newBoard;
	}
	
	/** Updates the board class with new moves. TAKES CONVENTIONAL ARRAY-COORDINATE INPUT.
	 * @param qx1 Original queen x position
	 * @param qy1 Original queen y position
	 * @param qx2 New queen x position
	 * @param qy2 New queen y position
	 * @param ax Arrow position
	 * @param ax Arrow position
	 * @return True if move within range of board, false if move is not within range of the board
	 */
	public boolean movePiece(int[] move) {
		move = Arrays.copyOf(move, move.length);
		if (move[0] <= 10 && move[0] > 0 && move[1] <= 10 && move[1] > 0) {
			int queen = board[move[0]][move[1]];		//save board value at queen's original place
			board[move[0]][move[1]] = 0;				//delete queen from original place
			board[move[2]][move[3]] = queen;			//recreate queen at new place
			board[move[4]][move[5]] = 3;				//put arrow at arrow coords
			
//			System.out.println("From Board: Move made.");
			return true;
		}
		
//		System.err.println("From Board: Move not made.");
		return false;
	}
	
	/** 
	 * @param none
	 * @return A string that represents the game board. 1 = black, 2 = white, 3 = arrow
	 */
	public String toString() {
		StringBuilder boardString = new StringBuilder();
		for(int y = 1; y < 11; y++) {
			for(int x = 1; x < 11; x++) 
				boardString.append(Integer.toString((board[x][y])));
			boardString.append("\n");
		}
		return boardString.toString();
	}
	
    /**
     * Make sure the global field thing, board (a 2D int array version of gamestate), is up to date when calling this.
     * @param team
     * @return a big list of int arrays of length 6, in the format { qx1, qy1, qx2, qy2, ax, ay } IN THE CONVENTIONAL COORDINATE FORMAT (y=1 is at the top of the board)
     */
    public ArrayList<int[]> getAllPossibleMoves(int team){
    	ArrayList<int[]> movesList = new ArrayList<>();
    	ArrayList<int[]> allQueenPositions = this.getQueenCoords(team);
    	for (int[] curQueenPosition : allQueenPositions) {
    		ArrayList<int[]> allMovesForCurrentQueen = getAllPossibleMovesHelper(curQueenPosition[0], curQueenPosition[1]);
    		for (int[] potentialMoveForCurQueen : allMovesForCurrentQueen) {
    			/* The following two lines save and wipe the value of the current queen being considered's original tile,
    			 * so that it can be temporarily considered blank whilst the potential tiles at which her arrow can be shot from her new position are being added to the list.
    			 * The tile value is saved so that it can be reinstated after the potential arrow shots from this potential new position are done being considered.
    			 */
            	int temp = this.get(curQueenPosition[0], curQueenPosition[1]);
            	this.set(curQueenPosition[0], curQueenPosition[1], 0);
    			ArrayList<int[]> allArrowsForCurrentMove = getAllPossibleMovesHelper(potentialMoveForCurQueen[0], potentialMoveForCurQueen[1]);
    			this.set(curQueenPosition[0], curQueenPosition[1], temp); //Reinstating queen's original tile.
    			for (int[] arrow : allArrowsForCurrentMove)
    				movesList.add(new int[] { curQueenPosition[0], curQueenPosition[1], potentialMoveForCurQueen[0], potentialMoveForCurQueen[1], arrow[0], arrow[1] });
    		}
    	}
		return movesList;
    }
    
    /**
     * @param x
     * @param y
     * @return A list of all tiles that can possibly be reached in a straight line from the provided coordinates
     */
    public ArrayList<int[]> getAllPossibleMovesHelper(int x, int y){
    	ArrayList<int[]> list = new ArrayList<>();
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, 0, -1, new ArrayList<int[]>()));	//Up
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, 1, -1, new ArrayList<int[]>()));	//Up-right
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, 1, 0, new ArrayList<int[]>()));	//Right
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, 1, 1, new ArrayList<int[]>()));	//Down-right
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, 0, 1, new ArrayList<int[]>()));	//Down
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, -1, 1, new ArrayList<int[]>()));	//Down-left
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, -1, 0, new ArrayList<int[]>()));	//Left
    	list.addAll(getAllPossibleMovesHelperHelper(x, y, -1, -1, new ArrayList<int[]>()));	//Up-left
    	return list;
    }
    
    
    /** Returns an ArrayList of coordinates (as int arrays of size 2, all in ascending order by distance from starting point) 
     *	that can be reached from starting coordinates (x, y) while taking horizontal/vertical/diagonal steps of size xInc and yInc respectively, before an obstacle is hit.
     * @param x		Starting x coordinate
     * @param xInc	Amount by which to increment x coordinate with each step
     * @param y		Starting y coordinate
     * @param yInc	Amount by which to increment y coordinate with each step
     * @param list	Please provide an empty list (for tail recursion).
     * @return
     */
    public ArrayList<int[]> getAllPossibleMovesHelperHelper(int x, int y, int xInc, int yInc, ArrayList<int[]> list){
    	if (this.get(x + xInc, y + yInc) == 0) {									//Checking if current spot being examined is empty. 
    		list.add(new int[]{x + xInc, y + yInc});								//If so, add it to list,
    		return getAllPossibleMovesHelperHelper(x + xInc, y + yInc, xInc, yInc, list); //and recurse, passing along list and incrementing x & y
    	}
    	else	//If current spot is taken or out of bounds, returns the list.
    		return list;
    }
    
	/** An overly complicated method (but much more efficient than the original, simpler idea) to detect whether or not a move is legal
     *  Make sure not to update the game board until after this check is done. TAKES CONVENTIONAL COORDINATE INPUT.
     * @param qx1
     * @param qy1
     * @param qx2
     * @param qy2
     * @param ax
     * @param ay
     * @return The array of truth, with elements as follows:
     * {{isValid, null, null, null}
     * {isQueenMoveLegit, didNotMove, notDiagonalHorizontalOrVertical, isObstructed, isOutOfBounds, queenDoesNotExist, queenIsAnArrow, queenIsNotYours}
     * {isArrowShotLegit, didNotMove, notDiagonalHorizontalOrVertical, isObstructed, isOutOfBounds, null, null, null}}
     */
    public boolean[][] checkIfMoveIsValid(int qx1, int qy1, int qx2, int qy2, int ax, int ay, int team) {
    	int otherTeam = (team == 1 ? 2 : 1);
    	boolean[][] arrayOfTruth = new boolean[3][8];
    	boolean[] arrayOfTruthForQueen = checkIfMoveIsValidHelper(qx1, qy1, qx2, qy2);
    	int temp = this.get(qx1, qy1); //Temporarily save qx1, qy1 blank so that the arrow can consider it empty.
    	this.set(qx1, qy1, 0); //Clear the tile qx1, qy1
    	boolean[] arrayOfTruthForArrow = checkIfMoveIsValidHelper(qx2, qy2, ax, ay);
    	this.set(qx1, qy1, temp); //Reinstate qx1, qy1
    	
    	if (this.get(qx1, qy1) == 0) {
    		arrayOfTruth[1][5] = true;					//queenDoesNotExist
    		arrayOfTruthForQueen[0] = false;			//queen move not legit
    	}
    	if (this.get(qx1, qy1) == 3) {
    		arrayOfTruth[1][6] = true;					//queenIsAnArrow
    		arrayOfTruthForQueen[0] = false;			//queen move not legit
    	}
    	if (this.get(qx1, qy1) == otherTeam) {
    		arrayOfTruth[1][7] = true;					//queenIsNotYours
    		arrayOfTruthForQueen[0] = false;			//queen move not legit
    	}
    	arrayOfTruth[0][0] = arrayOfTruthForQueen[0] && arrayOfTruthForArrow[0];	//isValid
    	arrayOfTruth[1][0] = arrayOfTruthForQueen[0];	//isQueenMoveLegit
    	arrayOfTruth[1][1] = arrayOfTruthForQueen[1];	//didNotMove
    	arrayOfTruth[1][2] = arrayOfTruthForQueen[2];	//notDiagonalHorizontalOrVertical
    	arrayOfTruth[1][3] = arrayOfTruthForQueen[3];	//isObstructed
    	arrayOfTruth[1][4] = arrayOfTruthForQueen[4];	//isOutOfBounds
		arrayOfTruth[2][0] = arrayOfTruthForArrow[0];	//isArrowShotLegit
    	arrayOfTruth[2][1] = arrayOfTruthForArrow[1];	//didNotMove
    	arrayOfTruth[2][2] = arrayOfTruthForArrow[2];	//notDiagonalHorizontalOrVertical
    	//The queen's move has to be legit in order for the arrow's path to be properly scanned for obstructions, so we avoid false positives here.
    	//if (arrayOfTruth[1][0])
        	arrayOfTruth[2][3] = arrayOfTruthForArrow[3];	//isObstructed
    	arrayOfTruth[2][4] = arrayOfTruthForArrow[4];	//isOutOfBounds
    	
    	return arrayOfTruth;
    }
    
    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return The array of truth, with elements as follows: {{isValid, didNotMove, notDiagonalHorizontalOrVertical, isObstructed, isOutOfBounds}}
     */
    public boolean[] checkIfMoveIsValidHelper(int x1, int y1, int x2, int y2) {
    	boolean[] arrayOfTruth = new boolean[5];
    	int deltaX = x2 - x1;
    	int deltaY = y2 - y1;
    	int deltaXAbs = Math.abs(deltaX);
    	int deltaYAbs = Math.abs(deltaY);
    	
    	if (	1 > x1 || x1 > 10 && 
    			1 > y1 || y1 > 10 &&
    			1 > x2 || x2 > 10 &&
    			1 > y2 || y2 > 10) {
    		arrayOfTruth[0] = false; //sets isValid false
    		arrayOfTruth[4] = true; //sets isOutOfBounds true
    		return arrayOfTruth;
    	}

    	//if deltaX = deltaY then the move must have been diagonal which is good.
    	//if only one of them equals 0 then it's a horizontal/vertical move which is good
    	//if BOTH of them equal zero then it's illegal (you're not allowed to move to the same place)
    	boolean didNotMove = (deltaXAbs == 0 && deltaYAbs == 0);
    	if (didNotMove) {
    		arrayOfTruth[0] = false;	//set isValid
    		arrayOfTruth[1] = true;		//set didNotMove
    		return arrayOfTruth;
    	}
    	boolean isDiagonal = ((deltaXAbs == deltaYAbs) && !didNotMove);
    	boolean isHorizontalOrVertical = (deltaXAbs == 0 ^ deltaYAbs == 0);
    	if (!isDiagonal && !isHorizontalOrVertical) {
    		arrayOfTruth[0] = false; //sets isValid false
    		arrayOfTruth[2] = true; //sets notDiagonalHorizontalOrVertical true
    		return arrayOfTruth;
    	}
    	
    	int xStep, yStep;
    	//Now to check if the queen/arrow ran anything over
    	if (isDiagonal) {
    		xStep = deltaX/deltaXAbs;
    		yStep = deltaY/deltaYAbs;
    	}
    	else { //must be horizontal or vertical
    		if (deltaX == 0) {
    			xStep = deltaX;
    			yStep = deltaY/Math.abs(deltaY);
    		}
    		else {
    			yStep = deltaY;
    			xStep = deltaX/Math.abs(deltaX);
    		}
    	}
    	
    	if (Math.abs(xStep) > 1 || Math.abs(yStep) > 1) {
    		System.err.println("WARNING: Move-validity checker cannot work properly because xStep or yStep is not between -1 and 1. That's probably due to a programming error in the checkIfMoveIsValidHelper method.");
    		arrayOfTruth[0] = true;		//set isValid
    		arrayOfTruth[1] = false;	//set didNotMove
        	arrayOfTruth[2] = false;	//set notDiagonalHorizontalOrVertical
        	arrayOfTruth[3] = false;	//set isObstructed
        	arrayOfTruth[4] = false;	//set isOutOfBounds
    		return arrayOfTruth; //it has to perhaps falsely return true (better than falsely reporting an invalid move) so that it doesn't get caught in an infinite loop. Throwing an exception seemed to be too complicated.
    	}
    
    	int iteration = 0;
    	while (x1 + xStep*iteration != x2 || y1 + yStep*iteration != y2) {
    		iteration++;
    		if (this.get(x1 + xStep*iteration, y1 + yStep*iteration) != 0) {
    			//This means there is an obstruction encountered between (x1, y1) and (x2,y2), not inclusive of (x1, y1), when the specified steps are followed
    			arrayOfTruth[0] = false;
    			arrayOfTruth[3] = true; //set isObstructed true
        		return arrayOfTruth;
    		}
    	}
    	
    	//If it gets to this point, everything must be fine. Just making sure the array is consistent.
    	arrayOfTruth[0] = true;		//set isValid
    	arrayOfTruth[1] = false;	//set didNotMove
    	arrayOfTruth[2] = false;	//set notDiagonalHorizontalOrVertical
    	arrayOfTruth[3] = false;	//set isObstructed
    	arrayOfTruth[4] = false;	//set isOutOfBounds
    	return arrayOfTruth;
    }
}
