package ubc.cosc322;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class MiniMaxMoveGenerator {
	private final static int END_REWARD = Integer.MAX_VALUE;
	private static int leafsReached = 0;
	private static int totalNodesVisited = 0;
	private static int timeAllotted = 25;
	private static long startingTime;
	private static int depthReached = 0;
	
	/** Returns the best action to be made
	 * @param root
	 */
	public static int[] getMove(GameState gameState, int ourTeam) {
		startingTime = System.currentTimeMillis();
		Board board = gameState.getBoard();
		ArrayList<int[]> allOurMoves = board.getAllPossibleMoves(ourTeam);
		int otherTeam = (ourTeam == 1 ? 2 : 1);
		GameState bestNode = new GameState(allOurMoves.get(0), gameState);
		for (int[] move : allOurMoves) {
			GameState node = new GameState(move, gameState);
			node.setMiniMaxValue(minValue(0, 0, node.getBoard(), ourTeam, otherTeam, Integer.MIN_VALUE, Integer.MAX_VALUE));
			if (node.getMiniMaxValue() > bestNode.getMiniMaxValue())
				bestNode = node;
		}
		System.out.println("Number of terminal nodes reached: " + leafsReached);
		System.out.println("Number of nodes visited: " + totalNodesVisited);
		System.out.println("Utility of best action: " + bestNode.getMiniMaxValue());
		System.out.println("Time taken: " + (System.currentTimeMillis()/1000. - startingTime/1000.));
		System.out.println("Depth reached: " + depthReached);
		return bestNode.getAction();
	}
	
	private static int maxValue(int depth, int depthLimit, Board board, int ourTeam, int otherTeam, int a, int b) {
		totalNodesVisited++;
		if (depth < depthReached)
			depthReached = depth;
		
		ArrayList<int[]> allOurMoves = board.getAllPossibleMoves(ourTeam);
		depth++;
		
		if (depth == 2 || stateIsTerminal(board)) { 	//If state is terminal
			leafsReached++;
			return getUtility(board, ourTeam, otherTeam);	//Run heuristic function
		}
		
		int v = Integer.MIN_VALUE;
		for (int[] move : allOurMoves) {
			Board tempBoard = Board.copyOf(board);
			tempBoard.movePieceFast(move);
			v = Math.max(v, minValue(Integer.valueOf(depth), depthLimit, tempBoard, ourTeam, otherTeam, a , b));
			if (v >= b)
				return v;
			a = Math.max(a, v);
			if (System.currentTimeMillis()/1000. - startingTime/1000. > timeAllotted)
				break;
		}
		return v;
	}

	private static int minValue(int depth, int depthLimit, Board board, int ourTeam, int otherTeam, int a, int b) {
		totalNodesVisited++;
		if (depth < depthReached)
			depthReached = depth;
		ArrayList<int[]> allEnemyMoves = board.getAllPossibleMoves(otherTeam);
		depth++;
		
		if (depth == 2 || stateIsTerminal(board)) { 	//If state is terminal
			leafsReached++;
			return getUtility(board, ourTeam, otherTeam);	//Run heuristic function
		}
		
		int v = Integer.MAX_VALUE;
		for (int[] move : allEnemyMoves) {
			Board tempBoard = Board.copyOf(board);
			tempBoard.movePieceFast(move);
			v = Math.min(v, maxValue(Integer.valueOf(depth), depthLimit, tempBoard, ourTeam, otherTeam, a, b));
			if (v <= a)
				return v;
			b = Math.min(b, v);
			if (System.currentTimeMillis()/1000. - startingTime/1000. > timeAllotted)
				break;
		}
		return v;
	}
	
	private static boolean stateIsTerminal(Board board) {
		int blackMoves = board.getAllPossibleMoves(1).size();
		int whiteMoves = board.getAllPossibleMoves(2).size();
		if (blackMoves == 0 || whiteMoves == 0)
			return true;
		else
			return false;
	}
	
	private static int getUtility(Board board, int ourTeam, int otherTeam) {
		int c1 = 1;
		int c2 = 0;
		int c3 = 0;
		if (board.getAllPossibleMoves(ourTeam).size() == 0)
			return -END_REWARD;
		if (board.getAllPossibleMoves(otherTeam).size() == 0)
			return END_REWARD;
		return c1*heuristic1(board, ourTeam, otherTeam)		// Difference of sums of obstacles directly adjacent to each team's queens
				+ c2*heuristic3(board, ourTeam, otherTeam)	// Difference of number of moves available to each team
				+ c3*heuristic3(board, ourTeam, otherTeam);	// Difference of amount of space accessible to each team's queens
	}
	
	/**
	 * @param node
	 * @param ourTeam
	 * @param otherTeam
	 * @return The sum of obstacles within 1 tile of each queen for enemy team, minus that of queens for our team.
	 */
	public static int heuristic1(Board board, int ourTeam, int otherTeam) {
		int sumOfObstaclesNearEachEnemyQueen = 0;
		int sumOfObstaclesNearEachOfOurQueens = 0; //overly descriptive identifiers
		
		//Calc proximate obstructions for enemy
		for (int[] queen : board.getQueenCoords(otherTeam))	//For each queen
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++)
					if (!(x == 0 && y == 0)) //doesn't check queen's tile
						if (board.get(queen[0] + x, queen[1] + y) != 0) //If any surrounding tile is not free (must be either an arrow or queen of either team)
							sumOfObstaclesNearEachEnemyQueen++;
		
		//Same thing but for our team
		for (int[] queen : board.getQueenCoords(ourTeam))	//For each queen
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++)
					if (!(x == 0 && y == 0)) //doesn't check queen's tile
						if (board.get(queen[0] + x, queen[1] + y) != 0) //If any surrounding tile is not free (must be either an arrow or queen of either team)
							sumOfObstaclesNearEachOfOurQueens++;
		
		return sumOfObstaclesNearEachEnemyQueen - sumOfObstaclesNearEachOfOurQueens;
	}
	
	/**
	 * @param node
	 * @param ourTeam
	 * @return The number of moves our team can make, minus that of the enemy.
	 */
	public static int heuristic2(Board board, int ourTeam, int otherTeam) {
		return board.getAllPossibleMoves(ourTeam).size() - board.getAllPossibleMoves(otherTeam).size();
	}
	
	/**
	 * @param node
	 * @param ourTeam
	 * @param otherTeam
	 * @return The sum of the number of tiles that each of our queens can potentially reach, minus that of the enemy's queens. Only counts a space shared by multiple queens once.
	 */
	public static int heuristic3(Board board, int ourTeam, int otherTeam) {
		return heuristic3Helper(board, ourTeam) - heuristic3Helper(board, otherTeam	);
	}
	
	private static int heuristic3Helper(Board board, int ourTeam) {
		ArrayList<int[]> allQueenCoords = board.getQueenCoords(ourTeam);
		ArrayList<Coords> queensToSkip = new ArrayList<>(); //Queens will be added to list if they are to be prevented from having BFS performed on them.
		int spaceAvailableToQueens = 0;
		
		for (int[] queenCoords : allQueenCoords) {
			if (queensToSkip.contains(new Coords(queenCoords)))
				continue;
			
			LinkedList<int[]> tileQueue = new LinkedList<>();
			HashSet<Coords> tilesVisited = new HashSet<>();
			tileQueue.add(queenCoords);
			
			//Breadth-first search
			while (!tileQueue.isEmpty()) {
				ArrayList<int[]> tilesToAdd = new ArrayList<>();
				
				tilesToAdd.addAll(board.getTilesAround(tileQueue.poll()));		//Dequeue current tile, add its children to shortlist of tiles to expand upon
				for (int[] tile : tilesToAdd)									//for each shortlisted tile,
					if (!tilesVisited.contains(new Coords(tile)) && board.get(tile) == 0) {	//if it is empty and has not been visited,
						tileQueue.add(tile);										//add tile to queue
						tilesVisited.add(new Coords(tile));									//mark tile as visited
						spaceAvailableToQueens++;								//Now, if the current tile is empty, add it to list of available free tiles.	
					}
					//else if (board.get(tile) == ourTeam) { 			//If the current tile is the location of one of the current team's queens,
					//	queensToSkip.add(new Coords(tile)); 		//ensure that that encountered queen does not have BFS performed on her by removing her from the list.
					//	tilesVisited.add(new Coords(tile));			//also mark tile as visited
					//}
			}
		}
		return spaceAvailableToQueens;
	}
}
