package ubc.cosc322;

import java.lang.Math;
import java.util.ArrayList;

/**
 * @author Vaughn Janes, Nick McGee, Erik Johnston, Ann Ni, Rahul_Roy A class
 *         for the Monte Carlo algorithm for the game board for Game of the
 *         Amazons. Credit to Rahul_Roy at
 *         https://www.geeksforgeeks.org/ml-monte-carlo-tree-search-mcts/ for
 *         the pseudocode used as reference while writing this.
 */

public class MonteCarloMoveGenerator {

	// This is the constant value for UCB. It can be tweaked.
	private double C = 2;

	private Board board;
	private long timeAlotted = 29;
	private int ourTeam;
	private int otherTeam;

	
	/* Constructor */
	public MonteCarloMoveGenerator(Board board, int ourTeam) {
		this.board = board;
		this.ourTeam = ourTeam;

		if (this.ourTeam == 1)
			otherTeam = 2;
		else
			otherTeam = 1;
	}
	

	/* Class functions */

	/**
	 * @param root
	 * @return The potential action deemed best, given the provided GameState.
	 */
	public int[] monteCarloTreeSearch(GameState root) {
		System.out.println("Starting Monte Carlo Tree Search...");
		
		// divide by 1000 to get from milliseconds to seconds
		double startTime = (double) (System.currentTimeMillis() / 1000);																
		double currentTime = (double) (System.currentTimeMillis() / 1000);

		while ((currentTime - startTime) < timeAlotted) {
			GameState leaf = traverse(root);
			int simulation_result = simulate(leaf);
			backPropagate(leaf, simulation_result);

			currentTime = (double) (System.currentTimeMillis() / 1000);
			
			System.out.printf("Time remaining: %.1f seconds\n", currentTime - startTime);
		}
		
		return bestChild(root).getAction();
	}
	
	
	/**
	 * Traverses through the root to find any children that have not been visited. 
	 * If all children have been visited, then it will select the child with the 
	 * best UCB value and set it as the new root. Continue until an unvisited child 
	 * has been found.
	 * 
	 * @param root The root GameState
	 * @return The leaf to simulate
	 */
	private GameState traverse(GameState root) {
//		System.out.println("Traversing!");
		
		// Increase root's visits
		root.incrVisits(1);
				
		// Check for unvisited children
		
		// Our team
		if (root.getDepth() % 2 == 0) {
			if(root.getChildren(ourTeam).isEmpty())
				return root;
			
			for (GameState child : root.getChildren(ourTeam))
				if(child.getVisits() == 0) {
					child.incrVisits(1);
					return child;
				}
		}
		// Their turn
		else {
			if(root.getChildren(otherTeam).isEmpty())
				return root;
			
			for (GameState child : root.getChildren(otherTeam))
				if(child.getVisits() == 0) {
					child.incrVisits(1);
					return child;
				}
		}
		
		// If no unvisited children, expand to the best UCB value
		return traverse(bestChild(root));
	}
	

	/**
	 * Searches through the children of the root. Utilizes the UCB algorithm
	 * to determine the best child. Uses the depth of the root to determine if it
	 * is our turn, or the other team's turn. Will minimize the UCB if it's the 
	 * other team's turn, and maximize if it's our turn.
	 * 
	 * @param root The parent node.
	 * @return The child of the provided node with the highest UCB value. Returns
	 *         null if there are no children.
	 */
	private GameState bestChild(GameState root) {
		GameState bestChild = null;
		int rootVisits = root.getVisits();

		// Our turn (maximize value)
		if (root.getDepth() % 2 == 0) {
			double highestValue = Double.MIN_VALUE;

			for (GameState child : root.getChildren(ourTeam)) {
				double UCBvalue = getUCB(rootVisits, child.getValue(), child.getVisits(), Double.MAX_VALUE);
				if (UCBvalue > highestValue)
					bestChild = child;
			}
			
			return bestChild;
		}
		// Their turn (minimize value)
		else {
			double lowestValue = Double.MAX_VALUE;

			for (GameState child : root.getChildren(otherTeam)) {
				double UCBvalue = getUCB(rootVisits, child.getValue(), child.getVisits(), Double.MIN_VALUE);
				if (UCBvalue < lowestValue)
					bestChild = child;
			}
			
			return bestChild;
		}
	}
	

	/**
	 * Calculates the UCB value of a root's children using the formula: childValue +
	 * (C * sqrt( ln(rootVisits) / childVisits))
	 * 
	 * @param rootVisits The total visits of a root's children
	 * @param childValue The value of the child from simulation
	 * @param minMax     The theoretical min or max value for UCB
	 * @return A double of the calculated UCB value for the child node
	 */
	private double getUCB(int rootVisits, double childValue, int childVisits, double minMax) {
		// Note: C is a constant that can be changed at the top of the file
		if (childVisits != 0)
			return childValue + (C * Math.sqrt((Math.log(rootVisits)) / childVisits));
		else
			return minMax;
	}
	

	/** 
	 * Runs the simulation process of Monte-Carlo tree search. When passed a
	 * root node, the function will randomly keep selecting child nodes until
	 * a node with no children is reached (a terminal node). The difference 
	 * between the remaining moves on our team and the enemy team is returned
	 * as the value of the terminal node.
	 * 
	 * @param root The node that will begin the simulation process
	 * @return The terminal node value of the simulation
	 */
	private int simulate(GameState root) {
//		System.out.println("Simulating!");
		
		ArrayList<GameState> children;
		int depth = 0;
		
		// Get initial children
		if(root.getDepth() % 2 == 0) 
			children = root.getChildren(ourTeam);
		else 
			children = root.getChildren(otherTeam);
		
		System.out.println("Simulate: Children is empty: " + children.isEmpty() + "\n");
		
		// Loop until we get to a point where there are no children
		while (!children.isEmpty()) {
			int randomChildIndex = (int) (children.size() * Math.random());
			depth = children.get(randomChildIndex).getDepth();
			
			System.out.printf("Simulation Depth: %d\n", depth);
			
			// Swap between our team and their team based on depth of children
			if(depth % 2 == 0) 
				children = children.get(randomChildIndex).getChildren(ourTeam);
			else
				children = children.get(randomChildIndex).getChildren(otherTeam);
		}
		
		/* Calculating value.
		 * Value is based off of the remaining moves we have, and the remaining moves
		 * the other team has. If it's negative, they won with 'x' many moves left.
		 * If it's positive, we won with 'x' many moves left.
		 */
		int ourMoves;
		int otherMoves;
		
		// When the loop ends, the depth that is left over signifies who lost.
		if(depth % 2 == 0) {
			ourMoves = 0;
			otherMoves = children.size();
		}
		else {
			ourMoves = children.size();
			otherMoves = 0;
		}
		
		return(ourMoves - otherMoves);
	}
	
	
	/**
	 * Recursively loops back from the node up to the main root parent.
	 * Increases the value of each node by the value of the terminal node
	 * as it propagates upwards. 
	 * 
	 * @param node The node that was chosen during the traversal process.
	 *             The simulation process is branched off from this node.
	 * @param simulation_result The result from the simulation process that
	 * 		       branched off of the input node.
	 * @return void
	 */
	private void backPropagate(GameState node, int simulation_result) {
//		System.out.println("Back-propagating!");
				
		if(node.getParent() != null) {
			node.incrValue(simulation_result);
			backPropagate(node.getParent(), simulation_result);
		}
	}
	
	
	
	

	/**
	 * 
	 * @param children the children of the Parent node we are rolling out
	 * @return a new random child node
	 */
	/*
	private GameState rollout_policy(ArrayList<GameState> children) {
		ArrayList<GameState> unvisited = new ArrayList<GameState>();
		
		// get all children which have not been searched yet
		for (GameState a : children) {
			unvisited.add(a);
		}

		int child = (int) (unvisited.size() * Math.random());
		return unvisited.get(child);
	}
	*/
	
	
	
	
	
	/*
	 * Pseudocode: # main function for the Monte Carlo Tree Search def
	 * monte_carlo_tree_search(root):
	 * 
	 * while resources_left(time, computational power): leaf = traverse(root)
	 * simulation_result = rollout(leaf) backpropagate(leaf, simulation_result)
	 * 
	 * return best_child(root)
	 * 
	 * # function for node traversal def traverse(node): while fully_expanded(node):
	 * node = best_uct(node)
	 * 
	 * # in case no children are present / node is terminal return
	 * pick_univisted(node.children) or node
	 * 
	 * # function for the result of the simulation def rollout(node): while
	 * non_terminal(node): node = rollout_policy(node) return result(node)
	 * 
	 * # function for randomly selecting a child node def rollout_policy(node):
	 * return pick_random(node.children)
	 * 
	 * # function for backpropagation def backpropagate(node, result): if
	 * is_root(node) return node.stats = update_stats(node, result)
	 * backpropagate(node.parent)
	 * 
	 * # function for selecting the best child # node with highest number of visits
	 * def best_child(node): pick child with highest number of visits
	 */
}
