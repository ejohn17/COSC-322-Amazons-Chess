package ubc.cosc322;

import java.lang.Math;

/**
 *	Credit to Rahul_Roy at https://www.geeksforgeeks.org/ml-monte-carlo-tree-search-mcts/ for the pseudocode used as reference while writing this.
 */
public class MonteCarloMoveGenerator {
	
	// This is the constant value for UCB. It can be tweaked.
	private double C = 2;
	
	private Board board;
	private long timeAlotted = 29;
	private int ourTeam;
	
	public MonteCarloMoveGenerator(Board board, int ourTeam) {
		this.board = board;
		this.ourTeam = ourTeam;
	}

	/**
	 * @param root
	 * @return The potential action deemed best, given the provided GameState.
	 */
	public int[] monteCarloTreeSearch(GameState root) {
		double startTime = (double) (System.currentTimeMillis() / 1000);   // divide by 1000 to get from milliseconds to seconds
		double currentTime = (double) (System.currentTimeMillis() / 1000);
		
		while ((currentTime - startTime) < timeAlotted) {
			int[] leaf = traverse(root);
			int simulation_result = rollout(leaf);
			backpropagate(leaf, simulation_result);
			
			currentTime = (double) (System.currentTimeMillis() / 1000);
		}
		return bestChild(root).getAction();
	}
	
	/**
	 * @param root The parent node.
	 * @return The child of the provided node with the highest UCB value. Returns null if there are no children.
	 */
	private GameState bestChild(GameState root) {
		double highestValue = Double.MIN_VALUE;
		GameState bestChild = null;
		int rootVisits = root.getVisits();
		
		for (GameState child : root.getChildren(ourTeam)) {
			double UCBvalue = getUCB(rootVisits, child.getValue(), child.getVisits());
			if (UCBvalue > highestValue)
				bestChild = child;
		}
		
		return bestChild;
	}
	
	/** Calculates the UCB value of a root's children using the formula: childValue + (C * sqrt( ln(rootVisits) / childVisits))
	 * @param rootVisits The total visits of a root's children
	 * @param childValue The value of the child from simulation
	 * @return A double of the calculated UCB value for the child node
	 */
	private double getUCB(int rootVisits, double childValue, int childVisits) {
		// Note: C is changed at the top of the
		return childValue + (C * Math.sqrt( (Math.log(rootVisits)) / childVisits ));
	}

	/*
	def traverse(node):
		while fully_expanded(node):
    		node = best_uct(node)
      
		# in case no children are present / node is terminal 
		return pick_univisted(node.children) or node 
	 */

	private int[] traverse(GameState node) {
		while (fullyExpanded(node)) //no idea what this is supposed to mean yet
			node = bestChild(node);
		
		return null;
	}

	private int rollout(int[] leaf) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void backpropagate(int[] leaf, int simulation_result) {
		// TODO Auto-generated method stub
		
	}
	
/* Pseudocode:
# main function for the Monte Carlo Tree Search
def monte_carlo_tree_search(root):
	
	while resources_left(time, computational power):
		leaf = traverse(root)
		simulation_result = rollout(leaf)
		backpropagate(leaf, simulation_result)
		
	return best_child(root)

# function for node traversal
def traverse(node):
	while fully_expanded(node):
		node = best_uct(node)
		
	# in case no children are present / node is terminal
	return pick_univisted(node.children) or node

# function for the result of the simulation
def rollout(node):
	while non_terminal(node):
		node = rollout_policy(node)
	return result(node)

# function for randomly selecting a child node
def rollout_policy(node):
	return pick_random(node.children)

# function for backpropagation
def backpropagate(node, result):
	if is_root(node) return
	node.stats = update_stats(node, result)
	backpropagate(node.parent)

# function for selecting the best child
# node with highest number of visits
def best_child(node):
	pick child with highest number of visits
*/
}