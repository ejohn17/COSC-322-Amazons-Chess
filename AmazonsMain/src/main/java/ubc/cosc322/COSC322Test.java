
package ubc.cosc322;

import java.util.ArrayList;
import java.util.Map;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 *
 * @author Yong Gao (yong.gao@ubc.ca) Jan 5, 2021
 */
public class COSC322Test extends GamePlayer {

    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;
    private Board board = null;
    
    private int ourTeam = 2;  // white = 2, black = 1
    private int otherTeam = 1;
    
    private String userName = null;
    private String passwd = null;

    /**
     * The main method
     *
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
        COSC322Test player = new COSC322Test(args[0], args[1]);

        if (player.getGameGUI() == null) {
            player.Go();
        } else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    player.Go();
                }
            });
        }
    }

    /**
     * Any name and passwd
     *
     * @param userName
     * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;

        // To make a GUI-based player, create an instance of BaseGameGUI
        // and implement the method getGameGUI() accordingly
         this.gamegui = new BaseGameGUI(this);
    }

    @Override
    public void onLogin() {
		System.out.println("Connected to server\n=====================\n");

		// List rooms
//		System.out.println("\nRoom list:");
//		for (Room r : gameClient.getRoomList())
//			System.out.println(r.getName());
		String roomName = gameClient.getRoomList().get(0).getName();
		
		// join the first room
		gameClient.joinRoom(roomName);
		board = new Board();
        userName = gameClient.getUserName();
        
        if (gamegui != null) {
            gamegui.setRoomInformation(gameClient.getRoomList());
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        // This method will be called by the GameClient when it receives a game-related
        // message
        // from the server.

        // For a detailed description of the message types and format,
        // see the method GamePlayer.handleGameMessage() in the game-client-api
        // document.
        if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
//        	System.out.println("\nGame action move message:\n=====================");
        	
        	// Get the enenemy's move
            ArrayList<Integer> queenpos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
            ArrayList<Integer> queenposNew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
            ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
            
            // Their exact move: (Useful for debugging)
            System.out.println("Enemy Queen inital position: [y:" + queenpos.get(0) + ", x:" + queenpos.get(1) + "]");
            System.out.println("Enemy Queen new position: [y:" + queenposNew.get(0) + ", x:" + queenposNew.get(1) + "]");
            System.out.println("Enemy Arrow position: [y:" + arrowPos.get(0) + ", x:" + arrowPos.get(1) + "]");
            
            //This is up here so that it can be detected before updating the Board (do not move this chunk to be after the board is updated or it'll probs break)
            boolean illegalMoveMade = !checkIfMoveIsValid(queenpos.get(1), 11 - queenpos.get(0), queenposNew.get(1), 11 - queenposNew.get(0), arrowPos.get(1), 11 - arrowPos.get(0));
            if (illegalMoveMade) {
            	for (int i = 0; i < 10; i ++) {
                	System.out.println("WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO!!!!!!!!!!!!!!!!!!!!!");
                	System.out.println("==============================I L L E G A L    M O V E   D E T E C T E D==============================");
            	}
            	return false;
            }
            
            // Update the board and GUI with the opposing player's move
            board.movePiece(queenpos.get(1), 11 - queenpos.get(0), queenposNew.get(1), 11 - queenposNew.get(0), arrowPos.get(1), 11 - arrowPos.get(0), this.otherTeam);
            gamegui.updateGameState(queenpos, queenposNew, arrowPos);
            
            // Print out their move
            System.out.println("\n\nOther player made a move:\n=====================");
            System.out.println(board.toString());
            
            // After enemy has made their move, we make our move
            makeMove();
        }
        
        else if (messageType.equalsIgnoreCase(GameMessage.GAME_STATE_BOARD)) {
        	System.out.println("\nGame state board message:\n=====================");

        	// Gets the current game state
            ArrayList<Integer> gamestate = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
            
            // Set the board and GUI to the inital game state
            board.setBoard(gamestate);
            gamegui.setGameState(gamestate);
            
            // Print out inital board
            System.out.println("\nInital board:\n=====================");
            System.out.println(board.toString());
        }
        
        else if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_START)) { 
        	System.out.println("\nGame action start message:\n=====================");

            // Get the user-names of the players matching black or white
        	String blackUsername = (String)msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
        	String whiteUsername = (String)msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
        	
        	// Print out which teams we are on
        	System.out.println("Black team: " + blackUsername);
        	System.out.println("White team: " + whiteUsername);
        	        	
        	
        	// Determine which team we are on
        	if (blackUsername.equalsIgnoreCase(userName)) {
        		ourTeam = 1;
        		otherTeam = 2;
        	}
        	else if (whiteUsername.equalsIgnoreCase(userName)) {
        		ourTeam = 2;
        		otherTeam = 1;
        	}
        	else {
        		System.err.println("Error. Our username did not match that of either the black or white player.");
        		return false;
        	}
        	        	
        	// Just confirm the team (useful for debugging)
    		System.out.println("We are on team " + (ourTeam == 1? "Black" : "White"));
    		System.out.println("Team number: " + ourTeam);
    		
    		// If we're black team, we make the first move
    		if(ourTeam == 1) 
	        	makeMove();
        }
        
        return true;
    }
    
    /**Gets all possible moves for your team, and then sends that move to the server.
     * 
     * @param Array list of current gamestate
     * @return 
     */
    public void makeMove() {
    	// Find all random moves, and randomly pick one.
        ArrayList<int[]> allMoves = getAllPossibleMoves(ourTeam);
        int[] randomMove = allMoves.get((int) (Math.random() * allMoves.size()));
        
        // Send that play to the server, and then update our board with that move.
        sendPlay(randomMove[0], randomMove[1], randomMove[2], randomMove[3], randomMove[4], randomMove[5]);
        board.movePiece(randomMove[0], randomMove[1], randomMove[2], randomMove[3], randomMove[4], randomMove[5], ourTeam);
        
        // Print out that we made a move, and which move we made
        System.out.println("\n\nWe made a move:\n=====================");
        System.out.println(board.toString());    
        System.out.println("Inital queen position: [y:" + (11 - randomMove[1]) + ", x:" + randomMove[0] + "]"); //These output the coordinates in the game's backward ass coordinate notation
        System.out.println("New queen position: [y:" + (11 - randomMove[3]) + ", x:" + randomMove[2] + "]");
        System.out.println("Arrow position: [y:" + (11 - randomMove[5]) + ", x:" + randomMove[4] + "]");

	}

	/** An overly complicated method (but much more efficient than the original, simpler idea) to detect whether or not a move is legal
     *  Make sure not to update the game board until after this check is done. TAKES CONVENTIONAL COORDINATE INPUT
     * @param qx1
     * @param qy1
     * @param qx2
     * @param qy2
     * @param ax
     * @param ay
     * @return True if move is valid, else false.
     */
    public boolean checkIfMoveIsValid(int qx1, int qy1, int qx2, int qy2, int ax, int ay) {
    	boolean isQueenMoveLegit = checkIfMoveIsValidHelper(qx1, qy1, qx2, qy2); //Returns true if and no obstructions between queen's original tile and her new tile
    	int temp = board.get(qx1, qy1); //Temporarily set qx1, qy1 blank
    	board.set(qx1, qy1, 0); //Clear the tile qx1, qy1
    	boolean isArrowShotLegit = checkIfMoveIsValidHelper(qx2, qy2, ax, ay); //Returns true if no obstructions between queen's new tile and her arrow shot (excluding the tile whence she came)
    	board.set(qx1, qy1, temp); //Reinstate qx1, qy1
    	return isQueenMoveLegit && isArrowShotLegit;
    }
    
    public boolean checkIfMoveIsValidHelper(int x1, int y1, int x2, int y2) {
    	int deltaX = x2 - x1;
    	int deltaY = y2 - y1;
    	int deltaXAbs = Math.abs(deltaX);
    	int deltaYAbs = Math.abs(deltaY);
    	
    	if (	1 > x1 || x1 > 10 && 
    			1 > y1 || y1 > 10 &&
    			1 > x2 || x2 > 10 &&
    			1 > y2 || y2 > 10)
    		return false;

    	//if deltaX = deltaY then the move must have been diagonal which is good.
    	//if only one of them equals 0 then it's a horizontal/vertical move which is good
    	//if BOTH of them equal zero then it's illegal (you're not allowed to move to the same place)
    	boolean didNotMove = (deltaXAbs == 0 && deltaYAbs == 0);
    	boolean isDiagonal = ((deltaXAbs == deltaYAbs) && !didNotMove);
    	boolean isHorizontalOrVertical = (deltaXAbs == 0 ^ deltaYAbs == 0);
    	if (!isDiagonal && !isHorizontalOrVertical) //the didNotMove might not be neccessary here
    		return false;
    	
    	
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
    		return true; //it has to perhaps falsely return true (better than falsely reporting an invalid move) so that it doesn't get caught in an infinite loop. Throwing an exception seemed to be too complicated.
    	}
    
    	int iteration = 0;
    	while (x1 + xStep*iteration != x2 || y1 + yStep*iteration != y2) {
    		iteration++;
    		if (board.get(x1 + xStep*iteration, y1 + yStep*iteration) != 0)
        		return false;
    	}
    	return true;
    }
    

    //Note to selves: With the way the getAllPossibleMoves is coded, it probably won't let itself move a queen and then shoot an arrow onto the tile that the queen moved from (because it thinks there's an obstacle there)
    //which will be a problem in the end-game scenario
    
    /**
     * Make sure the global field thing, board (a 2D int array version of gamestate), is up to date when calling this.
     * @param team
     * @return a big list of int arrays of length 6, in the format { qx1, qy1, qx2, qy2, ax, ay } IN THE CONVENTIONAL COORDINATE FORMAT (y=1 is at the top of the board)
     */
    public ArrayList<int[]> getAllPossibleMoves(int team){
    	ArrayList<int[]> movesList = new ArrayList<>();
    	ArrayList<int[]> allQueenPositions = board.getQueenCoords(team);
    	for (int[] curQueenPosition : allQueenPositions) {
    		ArrayList<int[]> allMovesForCurrentQueen = getAllPossibleMovesHelper(curQueenPosition[0], curQueenPosition[1]);
    		for (int[] potentialMoveForCurQueen : allMovesForCurrentQueen) {
    			/* The following two lines save and wipe the value of the current queen being considered's original tile,
    			 * so that it can be temporarily considered blank whilst the potential tiles at which her arrow can be shot from her new position are being added to the list.
    			 * The tile value is saved so that it can be reinstated after the potential arrow shots from this potential new position are done being considered.
    			 */
            	int temp = board.get(curQueenPosition[0], curQueenPosition[1]);
            	board.set(curQueenPosition[0], curQueenPosition[1], 0);
    			ArrayList<int[]> allArrowsForCurrentMove = getAllPossibleMovesHelper(potentialMoveForCurQueen[0], potentialMoveForCurQueen[1]);
    			board.set(curQueenPosition[0], curQueenPosition[1], temp); //Reinstating queen's original tile.
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
    	if (board.get(x + xInc, y + yInc) == 0) {									//Checking if current spot being examined is empty. 
    		list.add(new int[]{x + xInc, y + yInc});								//If so, add it to list,
    		return getAllPossibleMovesHelperHelper(x + xInc, y + yInc, xInc, yInc, list); //and recurse, passing along list and incrementing x & y
    	}
    	else	//If current spot is taken or out of bounds, returns the list.
    		return list;
    }

    public void sendPlay(int qx1, int qy1, int qx2, int qy2, int ax, int ay) {
    	ArrayList<Integer> queenStart = new ArrayList<Integer>();
    	queenStart.add(11 - qy1);
    	queenStart.add(qx1);
    	
    	ArrayList<Integer> queenEnd = new ArrayList<Integer>();
    	queenEnd.add(11 - qy2);
    	queenEnd.add(qx2);
    	
    	ArrayList<Integer> arrow = new ArrayList<Integer>();
    	arrow.add(11 - ay);
    	arrow.add(ax);
    	
    	this.gameClient.sendMoveMessage(queenStart, queenEnd, arrow);
    	this.gamegui.updateGameState(queenStart, queenEnd, arrow);
    }
    
    @Override
    public String userName() {
        return userName;
    }

    @Override
    public GameClient getGameClient() {
        // TODO Auto-generated method stub
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        // TODO Auto-generated method stub
        return gamegui;
    }

    @Override
    public void connect() {
        // TODO Auto-generated method stub
        gameClient = new GameClient(userName, passwd, this);
    }

}// end of class
