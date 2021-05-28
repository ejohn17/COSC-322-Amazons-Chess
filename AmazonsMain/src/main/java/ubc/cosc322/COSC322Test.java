
package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ubc.cosc322.GameBoard;

/**
 * An example illustrating how to implement a GamePlayer
 *
 * @author Yong Gao (yong.gao@ubc.ca) Jan 5, 2021
 */
public class COSC322Test extends GamePlayer {

    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;
    private GameBoard gameBoard = null;
    		

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
		System.out.println(
				"Congratualations!!! " + "I am called because the server indicated that the login is successfully");
		System.out.println("The next step is to find a room and join it: "
				+ "the gameClient instance created in my constructor knows how!");
		System.out.println("\nRoom list:");
		for (Room r : gameClient.getRoomList())
			System.out.println(r.getName());
		String roomName = gameClient.getRoomList().get(0).getName();
		
		gameClient.joinRoom(roomName);
		gameBoard = new GameBoard(null, this);
        userName = gameClient.getUserName();
        
        if (gamegui != null) {
            gamegui.setRoomInformation(gameClient.getRoomList());
        }
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        // This method will be called by the GameClient when it receives a game-related
        // message
        // from the server.

        // For a detailed description of the message types and format,
        // see the method GamePlayer.handleGameMessage() in the game-client-api
        // document.
        if (messageType.equalsIgnoreCase(GameMessage.GAME_ACTION_MOVE)) {
            ArrayList<Integer> queenpos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
            ArrayList<Integer> queenposNew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
            ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
            gamegui.updateGameState(queenpos, queenposNew, arrowPos);
            System.out.println("Queen initial position: " + queenpos.toString());
            System.out.println("Queen new position: " + queenposNew.toString());
            System.out.println("Arrow position: " + arrowPos.toString());
        } else if (messageType.equalsIgnoreCase(GameMessage.GAME_STATE_BOARD)) {
            ArrayList<Integer> gamestate = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
            gamegui.setGameState(gamestate);
            gameBoard.setBoard(gamestate);
            System.out.println("Game state: " + gamestate.toString());
        }
        return true;
    }

    public void sendPlay(int qx1, int qy1, int qx2, int qy2, int ax, int ay) {
    	ArrayList<Integer> queenStart = new ArrayList<Integer>();
    	queenStart.add(qy1);
    	queenStart.add(qx1);
    	ArrayList<Integer> queenEnd = new ArrayList<Integer>();
    	queenEnd.add(qy2);
    	queenEnd.add(qx2);
    	ArrayList<Integer> arrow = new ArrayList<Integer>();
    	arrow.add(ay);
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
