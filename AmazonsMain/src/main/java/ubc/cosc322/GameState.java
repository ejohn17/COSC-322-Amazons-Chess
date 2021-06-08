package ubc.cosc322;

import java.util.ArrayList;

public class GameState {
	private int[] action; //The action taken to get to this game state from its parent
	private Board board;
	private double value = 0;
	private int visits = 0;
	private int depth = 0;
	private GameState parent;
	private ArrayList<GameState> children = new ArrayList<>();;
	private boolean childrenGenerated = false;
	
	
	/* Constructors */
	public GameState(int[] action, GameState parent, int depth) {
		this.action = action;
		this.depth = depth;
		this.parent = parent;
		board = parent.getBoard();
		board.movePiece(action);
	}
	
	public GameState(Board board) {
		this.action = null;
		this.board = board;
		this.parent = null;
	}
	
	
	/* Getters and Setters */
	
	/** @return The parent of the current GameState. */
	public GameState getParent() { return parent; }
	
	
	/** @return The value of the current GameState */
	public double getValue() { return value; }
	
	/** @param value The new value of the GameState
	 *  @return void */
	public void setValue(int value) { this.value = value; }
	
	/** @param addedValue The value to add onto the current value of the GameState
	 *  @return void */
	public void incrValue(int addedValue) { this.value = this.value + addedValue; }
	
	
    /** @return The number of times this GameState has been visited by it's parent */
	public int getVisits() { return visits; }

    /** @param visits The number of visits this GameState has been visited by it's parent
     ** @return void*/
	public void setVisits(int visits) { this.visits = visits; }
	
    /** @param addedVisits How many visits to add onto the current amount of visits
     ** @return void*/
	public void incrVisits(int addedVisits) { this.visits = this.visits + addedVisits; }
	
	
    /** @return Set the depth of the node in the GameState tree */
	public int getDepth() { return depth; }

    /** @param depth Set the depth of the node in the GameState tree
     ** @return void*/
	public void setDepth(int depth) { this.depth = depth; }
	
    /** @param addedDepth How more depth to add onto the current depth of the node
     ** @return void*/
	public void incrDepth(int addedDepth) { this.depth = this.depth + addedDepth; }
	
	
	/** @return The action taken to get to this GameState from its parent GameState. */
	public int[] getAction() { return action; }
	
	
	/**@return The board configuration that this GameState represents. */
	public Board getBoard() { return board; }
	
	
	/* Class functions */
	
	/** Returns a list of this GameState's child states. Internal ArrayList not generated until the first time this method is called.
	 * @param team The number of the team for which you want all possible moves, aka the child nodes
	 * @return An ArrayList containing all the GameStates that can be transitioned to from this GameState in one action.
	 */
	public ArrayList<GameState> getChildren(int team) {
		if (!childrenGenerated) {
			for (int[] action : board.getAllPossibleMoves(team))
				children.add(new GameState(action, this, this.getDepth() + 1));
			childrenGenerated = true;
		}
		return children;
	}
}