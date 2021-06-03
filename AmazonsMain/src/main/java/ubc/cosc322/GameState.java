package ubc.cosc322;

import java.util.ArrayList;

public class GameState {
	private int[] action; //The action taken to get to this game state from its parent
	private Board board;
	private double value = 0;
	private int visits = 0;
	private ArrayList<GameState> children = new ArrayList<>();;
	private boolean childrenGenerated = false;
	
	public GameState(int[] action, GameState parent) {
		this.action = action;
		board = parent.getBoard();
		board.movePiece(action);
	}
	
	public GameState(Board board) {
		this.action = null;
		this.board = board;
	}
	
	/** @return The value of the current GameState. */
	public double getValue() { return value; }
	
    /** @return The number of times this GameState has been visited by it's parent */
	public int getVisits() { return visits; }

    /** @param visits The number of visits this GameState has been visited by it's parent
     ** @return void*/
	public void setVisits(int visits) { this.visits = visits; }
	
	/** @return The action taken to get to this GameState from its parent GameState. */
	public int[] getAction() { return action; }
	
	/**@return The board configuration that this GameState represents. */
	public Board getBoard() { return board; }
	
	/** Returns a list of this GameState's child states. Internal ArrayList not generated until the first time this method is called.
	 * @param team The number of the team for which you want all possible moves, aka the child nodes
	 * @return An ArrayList containing all the GameStates that can be transitioned to from this GameState in one action.
	 */
	public ArrayList<GameState> getChildren(int team) {
		if (!childrenGenerated) {
			for (int[] action : board.getAllPossibleMoves(team))
				children.add(new GameState(action, this));
			childrenGenerated = true;
		}
		return children;
	}
}