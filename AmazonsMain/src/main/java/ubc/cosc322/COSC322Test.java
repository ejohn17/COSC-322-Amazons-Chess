
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
    
    private int ourTeam;  // white = 2, black = 1
    private int otherTeam;
    
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
            System.out.println("Enemy Queen initial position: [y:" + queenpos.get(0) + ", x:" + queenpos.get(1) + "]");
            System.out.println("Enemy Queen new position: [y:" + queenposNew.get(0) + ", x:" + queenposNew.get(1) + "]");
            System.out.println("Enemy Arrow position: [y:" + arrowPos.get(0) + ", x:" + arrowPos.get(1) + "]");
            
            //This is up here so that it can be detected before updating the Board (do not move this chunk to be after the board is updated or it'll probs break)
            boolean[][] arrayOfTruth = board.checkIfMoveIsValid(queenpos.get(1), 11 - queenpos.get(0), queenposNew.get(1), 11 - queenposNew.get(0), arrowPos.get(1), 11 - arrowPos.get(0), otherTeam);
            if (!arrayOfTruth[0][0]) {
            	for (int i = 0; i < 10; i ++) {
                	System.out.println("WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO WEE WOO!!!!!!!!!!!!!!!!!!!!!");
                	System.out.println("==============================I L L E G A L    M O V E   D E T E C T E D==============================");
            	}
            	System.out.println("\n============The move that was illegal was============");
            	System.out.println("Enemy Queen initial position: [y:" + queenpos.get(0) + ", x:" + queenpos.get(1) + "]");
                System.out.println("Enemy Queen new position: [y:" + queenposNew.get(0) + ", x:" + queenposNew.get(1) + "]");
                System.out.println("Enemy Arrow position: [y:" + arrowPos.get(0) + ", x:" + arrowPos.get(1) + "]");
                System.out.println("Whilst the board state, before their move, was:");
                System.out.println(board.toString());
                System.out.println("Reasons for invalidity are as follows:");
                int numOfBrokenRules = 0;
                if (arrayOfTruth[1][1])
                	System.out.println(++numOfBrokenRules + ": Queen did not move.");
                if (arrayOfTruth[1][2])
                	System.out.println(++numOfBrokenRules + ": Queen's movement was not diagonal, horizontal, or vertical.");
                if (arrayOfTruth[1][3])
                	System.out.println(++numOfBrokenRules + ": Queen's path was obstructed.");
                if (arrayOfTruth[1][4])
                	System.out.println(++numOfBrokenRules + ": Queen's final or starting position is out of bounds.");
                if (arrayOfTruth[1][5])
                	System.out.println(++numOfBrokenRules + ": The queen attempted to be moved does not exist.");
                if (arrayOfTruth[1][6])
                	System.out.println(++numOfBrokenRules + ": The queen attempted to be moved is an arrow.");
                if (arrayOfTruth[1][7])
                	System.out.println(++numOfBrokenRules + ": The queen attempted to be moved does not belong to the player attempting to move it.");
                if (arrayOfTruth[2][1])
                	System.out.println(++numOfBrokenRules + ": Arrow was thrown onto queen's new position.");
                if (arrayOfTruth[2][2])
                	System.out.println(++numOfBrokenRules + ": Arrow's trajectory was not diagonal, horizontal, or vertical.");
                if (arrayOfTruth[2][3])
                	System.out.println(++numOfBrokenRules + ": Arrow's path was obstructed.");
                if (arrayOfTruth[2][4])
                	System.out.println(++numOfBrokenRules + ": Arrow's final or starting position is out of bounds.");
            }
            
            // Update the board and GUI with the opposing player's move
            // Had to add this line below to fix the dumb format that the coords were provided in
            int[] fixedMoveInformation = new int[] { queenpos.get(1), 11 - queenpos.get(0), queenposNew.get(1), 11 - queenposNew.get(0), arrowPos.get(1), 11 - arrowPos.get(0)};
            board.movePiece(fixedMoveInformation);
            gamegui.updateGameState(queenpos, queenposNew, arrowPos);
            
            //Moved this down here so it still updates our GUI with their invalid move
            if (arrayOfTruth[0][0] == false) {
            	return false;
            }
            
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
            System.out.println("\nInitial board:\n=====================");
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
        		otherTeam = 1;
        		ourTeam = 2;
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
        // MonteCarloMoveGenerator AI
        MonteCarloMoveGenerator moveGen = new MonteCarloMoveGenerator(ourTeam);
        int[] move = moveGen.monteCarloTreeSearch(new GameState(board));
        
        double movesLeft = moveGen.estimateMovesLeft(new GameState(board), 10);
        System.out.println("Estimated moves left: " + movesLeft);
        System.out.println("Tiles available to us: " + MiniMaxMoveGenerator.heuristic3(board, ourTeam, otherTeam));
        System.out.println("Tiles available to them: " + MiniMaxMoveGenerator.heuristic3(board, otherTeam, ourTeam));
        
        // Send that play to the server, and then update our board with that move.
        sendPlay(move);
        board.movePiece(move);
        
        // Print out that we made a move, and which move we made
        System.out.println("\nWe made a move:\n=====================");
        System.out.println("We are team " + (ourTeam == 1 ? "Black" : "White"));
        System.out.println(board.toString());    
        System.out.println("Initial queen position: [y:" + (11 - move[1]) + ", x:" + move[0] + "]"); //These output the coordinates in the game's backward ass coordinate notation
        System.out.println("New queen position: [y:" + (11 - move[3]) + ", x:" + move[2] + "]");
        System.out.println("Arrow position: [y:" + (11 - move[5]) + ", x:" + move[4] + "]");

	}

    public void sendPlay(int[] move) {
    	ArrayList<Integer> queenStart = new ArrayList<Integer>();
    	queenStart.add(11 - move[1]);
    	queenStart.add(move[0]);
    	
    	ArrayList<Integer> queenEnd = new ArrayList<Integer>();
    	queenEnd.add(11 - move[3]);
    	queenEnd.add(move[2]);
    	
    	ArrayList<Integer> arrow = new ArrayList<Integer>();
    	arrow.add(11 - move[5]);
    	arrow.add(move[4]);
    	
    	this.gameClient.sendMoveMessage(queenStart, queenEnd, arrow);
    	this.gamegui.updateGameState(queenStart, queenEnd, arrow);
    }
    
    public int getTeam() {
    	return ourTeam;
    }
    
    @Override
    public String userName() {
        return userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        return gamegui;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, this);
    }

}// end of class
