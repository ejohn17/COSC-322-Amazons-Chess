package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Group 2: Vaughn Janes, Nick McGee, Erik Johnston, Ann Ni 
 *	An object with which to represent the state of the game board, for use as a node in state-space-searches.
 */
public class GameState {
	private int[] action; //The action taken to get to this game state from its parent
	private Board board = new Board();
	private int value = 0;
	private int miniMaxValue = 0;
	private double ucb;
	private int visits = 0;
	private int depth = 0;
	private GameState parent;
	private ArrayList<GameState> children = new ArrayList<>();
	private boolean isExpanded = false;
	
	
	/* Constructors */
	public GameState(int[] action, GameState parent, int depth) {
		this.action = Arrays.copyOf(action, action.length);
		this.depth = Integer.valueOf(depth);
		this.parent = parent;
		this.board = Board.copyOf(parent.getBoard());
		this.board.movePiece(action);
	}
	
	public GameState(int[] action, GameState parent) {
		this.action = Arrays.copyOf(action, action.length);
		this.parent = parent;
		this.board = Board.copyOf(parent.getBoard());
		this.board.movePiece(action);
	}
	
	public GameState(Board board) {
		this.action = null;
		this.board = Board.copyOf(board);
		this.parent = null;
	}
	
	
	/* Getters and Setters */
	
	/** @return The action taken to get to this GameState from its parent GameState. */
	public int[] getAction() { return Arrays.copyOf(action, action.length); }
	
	/**@return The board configuration that this GameState represents, by value. */
	public Board getBoard() { return Board.copyOf(board); }
	
    /** @return Set the depth of the node in the GameState tree */
	public int getDepth() { return Integer.valueOf(depth); }
	
	/** @return The parent of the current GameState by reference. */
	public GameState getParent() { 
		if (parent == null) //hopefully avoids null reference exception
			return null;
		else
			return parent; }
	
	/** @return The value of the current GameState. */
	public int getValue() { return Integer.valueOf(value); }
	
	/** @return The value of the current GameState. */
	public int getMiniMaxValue() { return miniMaxValue; }
	
    /** @return The number of times this GameState has been visited by its parent */
	public int getVisits() { return Integer.valueOf(visits); }
	
    /** Calculates and returns the UCB value of this node.
     * @return UCB value of this node. */
	public double getUCB(double C) {
		if (this.parent == null)
			return 0;	//The top of the tree has a UCB value of zero. It shouldn't matter what it is, really.
		
		if (this.getVisits() == 0)
			ucb = Integer.MAX_VALUE;
		else
			ucb = Math.abs(this.getValue())/(double)this.getVisits() + (C * Math.sqrt((Math.log(this.parent.getVisits())) / this.getVisits()));
		
		if (this.getDepth() % 2 == 0) //If this gamestate is only reachable by an action taken by our enemy, flip its sign.
			ucb = -ucb;
		return Double.valueOf(ucb);
	}
	
	/**
	 * @return True if this GameState has been expanded in the tree.
	 */
	public boolean isExpanded() {
		if (this.isExpanded)
			return true;
		else
			return false;
	}
	
    /** @param depth Set the depth of the node in the GameState tree
     ** @return void*/
	public void setDepth(int depth) { this.depth = Integer.valueOf(depth); }
	
	/** @param value The new value of the GameState
	 *  @return void */
	public void setValue(int value) { this.value = Integer.valueOf(value); }
	
	/** @param value The new value of the GameState, for MiniMax
	 *  @return void */
	public void setMiniMaxValue(int val) { this.miniMaxValue = Integer.valueOf(val); }

    /** @param visits The number of visits this GameState has been visited by it's parent
     ** @return void*/
	public void setVisits(int visits) { this.visits = Integer.valueOf(visits); }
	
	/** @param addedValue The value to add onto the current value of the GameState
	 *  @return void */
	public void incrValue(int addedValue) {
		this.value = this.value + addedValue;
	}
	
    /** @param addedVisits How many visits to add onto the current amount of visits
     ** @return void*/
	public void incrVisits(int addedVisits) { this.visits = this.visits + addedVisits; }
	
    /** @param addedDepth How more depth to add onto the current depth of the node
     ** @return void*/
	public void incrDepth(int addedDepth) { this.depth = this.depth + addedDepth; }
	
	/* Class functions */
	
	/** Returns a list of this GameState's child states. Internal ArrayList not generated until the first time this method is called, to reduce redundant re-processing.
	 * @param team The number of the team for which you want all possible moves, aka the child nodes
	 * @return An ArrayList containing all the GameStates that can be transitioned to from this GameState in one action.
	 */
	public ArrayList<GameState> getChildren(int team) {
		if (!isExpanded) {
			//System.out.println("Generating children...");
			for (int[] action : board.getAllPossibleMoves(team))
				this.children.add(new GameState(action, this, this.getDepth() + 1));
			isExpanded = true;
			//System.out.println("Children generated!");
		}
		//System.out.println("Children already generated. Size of array: " + children.size());
		return this.children;	
	}
}