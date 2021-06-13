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
	private double C = 0.25; //So far I found 0.05 to be waaay better than 2.
	private long timeAlotted = 10;
	private int ourTeam;
	private int otherTeam;
	private double startTime;
	private int simulationsRan = 0;
	private int maxDepthReached = 0;
	private int numberOfTimesAllMovesGenerated = 0;
	private int estimatedNumberOfMovesLeft = 0;
	private int maxBranchingFactor = 0;
	private long totalSimTime = 0;

	/* Constructor */
	protected MonteCarloMoveGenerator(int ourTeam) {
		this.ourTeam = Integer.valueOf(ourTeam);
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
	protected int[] monteCarloTreeSearch(GameState root) {
		simulationsRan = 0;
		maxDepthReached = 0;
		System.out.println("Starting MONTE CARLO TREE SEARCH...");

		// divide by 1000 to get from milliseconds to seconds
		startTime = (System.currentTimeMillis() / 1000.);
		double currentTime = (System.currentTimeMillis() / 1000.);

		while ((currentTime - startTime) < timeAlotted) {
			GameState leaf = traverse(root);
			int simulation_result = simulate(leaf);
			backPropagate(leaf, simulation_result);
			currentTime = (System.currentTimeMillis() / 1000.);
		}
		
		System.out.println("Simulations ran:\t\t" + simulationsRan);
		System.out.println("Maximum branching factor:\t" + maxBranchingFactor);
		System.out.println("Maximum depth reached:\t\t" + maxDepthReached);
		System.out.printf("UCB of root's best child:\t%.4f\n", bestChild(root).getUCB(C));
		System.out.printf("n value of root's best child:\t%d\n", bestChild(root).getVisits());
		System.out.printf("Win rate of root's best child:\t%.4f\n", bestChild(root).getValue()/(double)bestChild(root).getVisits());
		/* The empirical mean, or win rate, seems to be flawed. It's the first half of the UCB formula, which is the number of won simulations
		 * of a given node/branch, divided by the number of times this node/branch has been visited.
		 * It seems that this value is always reported as being above 80-90%, unless the game is imminently about to be lost.
		 * Perhaps it would be fixed if we reported a lost simulation as having a value of -1 rather than 0.
		 * I ain't looked into it yet.
		 */
		System.out.printf("Avg number of moves per sim:\t%.1f\n", numberOfTimesAllMovesGenerated / (double) simulationsRan);
		System.out.printf("Avg time taken per sim:\t%f ms\n", (totalSimTime / (double) simulationsRan)/1_000_000);
		return bestChild(root).getAction();
	}

	/**
	 * Traverses through the root to find any children that have not been visited.
	 * If all children have been visited, then it will select the child with the
	 * best UCB value and set it as the new root. Continue until an unvisited child
	 * has been found.
	 * 
	 * @param node The root GameState
	 * @return The leaf to simulate
	 */
	private GameState traverse(GameState node) {
		int teamWhoseTurnIsNext = node.getDepth() % 2 == 0 ? ourTeam : otherTeam;
		// Is the current node a leaf node?
		if (node.isExpanded() && node.getChildren(teamWhoseTurnIsNext).size() > 0) {
			// If not a leaf, change current node to the child with best UCB.
			return traverse(bestChild(node));
		}

		// Is the n value for current node 0?
		if (node.getVisits() == 0)
			return node;// If unvisited, return this node to be simulated.
		else {
			// If already visited, generate all its possible actions and add them to the tree, returning
			// the first new child node to be simulated.
			// This is the Expansion step of MCTS.
			ArrayList<GameState> newChildren = node.getChildren(teamWhoseTurnIsNext);
			if (newChildren.size() > maxBranchingFactor) //This is here for analysis purposes
				maxBranchingFactor = newChildren.size();
			if (newChildren.size() > 0)
				return newChildren.get(0);
			else
				return node; //This happens when the node is terminal (win or lose). Not sure what it should be.
		}

	}

	/**
	 * Searches through the children of the root. Utilizes the UCB algorithm to
	 * determine the best child. Uses the depth of the root to determine if it is
	 * our turn, or the other team's turn. Will minimize the UCB if it's the other
	 * team's turn, and maximize if it's our turn.
	 * 
	 * @param root The parent node.
	 * @return The child of the provided node with the highest UCB value. Returns
	 *         null if there are no children.
	 */
	private GameState bestChild(GameState root) {
		GameState bestChild = null;

		// Our turn (maximize value)
		if (root.getDepth() % 2 == 0) {
			double highestValue = Double.MIN_VALUE;

			for (GameState child : root.getChildren(ourTeam)) {
				double UCBvalue = child.getUCB(C);
				if (UCBvalue > highestValue) {
					highestValue = UCBvalue;
					bestChild = child;
				}
				if (child.getVisits() == 0) //This means it has the highest possible UCB value, so let's just skip all the nonsense and return it.
					return child;
			}

			return bestChild;
		}
		// Their turn (minimize value)
		else {
			double lowestValue = Double.MAX_VALUE;

			for (GameState child : root.getChildren(otherTeam)) {
				double UCBvalue = child.getUCB(C);
				if (UCBvalue < lowestValue) {
					lowestValue = UCBvalue;
					bestChild = child;
				}
				if (child.getVisits() == 0) //This means it has the lowest possible UCB value, so let's just skip all the nonsense and return it.
					return child;
			}
			
			return bestChild;
		}
	}

	/**
	 * Runs the simulation process of Monte-Carlo tree search. Takes the Board from
	 * the provided GameState and randomly makes moves on it for alternating teams,
	 * starting with ourTeam if the provided GameState's depth is an even number,
	 * and otherwise the otherTeam.
	 * 
	 * @param node The node that will begin the simulation process
	 * @return 1 if the winning team is ours, else -1.
	 */
	protected int simulate(GameState node) {
		int result;
		simulationsRan++;
		if (node.getDepth() > maxDepthReached)
			maxDepthReached = node.getDepth();
		Board boardToSimulate = Board.copyOf(node.getBoard());
		// Has the imaginary enemy make the first move if the starting state was reached via
		// a move by our team, else vice versa.
		long startTime = System.nanoTime();
		if (node.getDepth() % 2 == 0)
			result = simulateHelper(boardToSimulate, ourTeam, otherTeam) == ourTeam ? 1 : 0; // Returns 1 if our team won
		else
			result = simulateHelper(boardToSimulate, otherTeam, ourTeam) == ourTeam ? 1 : 0; // Returns 1 if our team won
		
		totalSimTime += (System.nanoTime() - startTime);
		return result;
	}

	/**
	 * @param simBoard         Starting board to simulate
	 * @param currentTeam      The first team to make a move
	 * @param currentOtherTeam The other team
	 * @return The number of the team that won.
	 */
	private int simulateHelper(Board simBoard, int currentTeam, int currentOtherTeam) {
		numberOfTimesAllMovesGenerated++;		
		// Generate possible moves
		ArrayList<int[]> allPossibleMoves = simBoard.getAllPossibleMoves(currentTeam);

		// If no possible moves, the game is lost for the current team, so returns the
		// number of the other team.
		if (allPossibleMoves.size() == 0)
			return currentOtherTeam;// (base case)

		// If there are moves to be made, randomly chooses one.
		int[] randomMove = allPossibleMoves.get((int) (Math.random() * allPossibleMoves.size()));
		simBoard.movePiece(randomMove);

		allPossibleMoves = null; // Unreferencing to save memory
		return simulateHelper(simBoard, currentOtherTeam, currentTeam);
	}

	/**
	 * Loops from the given node up to the main root node. Increases the value of
	 * each node by the value of the terminal node as it propagates upwards, and
	 * also increments the number of visits for each node in this path.
	 * 
	 * @param node              The node that was chosen during the traversal
	 *                          process. The simulation process is branched off from
	 *                          this node.
	 * @param simulation_result The result from the simulation process that branched
	 *                          off of the input node.
	 * @return void
	 */
	private void backPropagate(GameState node, int simulation_result) {
		// System.out.println("Back-propagating!");
		GameState currentNode = node;
		while (currentNode != null) {
			currentNode.incrValue(simulation_result);
			currentNode.incrVisits(1);
			currentNode = currentNode.getParent();
		}
	}

	/** Basically the same code as simulate() except it returns an estimate of the number of moves left in une game
	 * @param node
	 * @return
	 */
	protected double estimateMovesLeft(GameState node, int sampleSize) {
		estimatedNumberOfMovesLeft = 0;
		Board boardToSimulate = Board.copyOf(node.getBoard());
		double movesLeft = 0;
		for (int i = 0; i < sampleSize; i++)
			movesLeft += estimateMovesLeftHelper(boardToSimulate, ourTeam, otherTeam);
		return movesLeft/sampleSize;
	}
	
	/**
	 * @param simBoard         Starting board to simulate
	 * @param currentTeam      The first team to make a move
	 * @param currentOtherTeam The other team
	 * @return The number of the team that won.
	 */
	private int estimateMovesLeftHelper(Board simBoard, int currentTeam, int currentOtherTeam) {
		// Generate possible moves
		ArrayList<int[]> allPossibleMoves = simBoard.getAllPossibleMoves(currentTeam);

		// If no possible moves, the game is lost for the current team, so returns the
		// number of the other team.
		if (allPossibleMoves.size() == 0)
			return estimatedNumberOfMovesLeft;// (base case)

		// If there are moves to be made, randomly chooses one.
		int[] randomMove = allPossibleMoves.get((int) (Math.random() * allPossibleMoves.size()));
		simBoard.movePiece(randomMove);

		estimatedNumberOfMovesLeft++;
		allPossibleMoves = null; // Unreferencing to save memory
		return estimateMovesLeftHelper(simBoard, currentOtherTeam, currentTeam);
	}
}
