package ubc.cosc322;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class MiniMaxMoveGenerator {
	
	/** Returns the best action to be made
	 * @param root
	 */
	public static int getMove(GameState root, int ourTeam) {
		int otherTeam = (ourTeam == 1 ? 2: 1);
		int heuristicTest = heuristic1(root, ourTeam, otherTeam);
		
		return heuristicTest;
	}
	
	/**
	 * @param node
	 * @param ourTeam
	 * @param otherTeam
	 * @return The sum of obstacles within 1 tile of each queen for enemy team, minus that of queens for our team.
	 */
	public static int heuristic1(GameState node, int ourTeam, int otherTeam) {
		Board board = Board.copyOf(node.getBoard());
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
	
	public static int heuristic2(GameState node, int ourTeam) {
		Board board = Board.copyOf(node.getBoard());
		int otherTeam = (ourTeam == 1 ? 2 : 1);
		return board.getAllPossibleMoves(ourTeam).size() - board.getAllPossibleMoves(otherTeam).size();
	}
	
	/**
	 * @param node
	 * @param ourTeam
	 * @param otherTeam
	 * @return The sum of the number of tiles that each of our queens can potentially reach, minus that of the enemy's queens. Only counts a space shared by multiple queens once.
	 */
	public static int heuristic3(GameState node, int ourTeam, int otherTeam) {
		Board board = Board.copyOf(node.getBoard());
		return heuristic3Helper(board, ourTeam);
	}
	
	private static int heuristic3Helper(Board board, int ourTeam) {
		ArrayList<int[]> queenCoords = board.getQueenCoords(ourTeam);
		ArrayList<int[]> queensToSkip = new ArrayList<>(); //Queens will be added to list if they are to be prevented from having BFS performed on them.
		int spaceAvailableToQueens = 0;
		
		for (int[] queen : queenCoords) {
			if (queensToSkip.contains(queen))
				continue;
			
			LinkedList<int[]> queue = new LinkedList<>();
			HashSet<int[]> tilesVisited = new HashSet<>();
			queue.add(queen);
			
			while (!queue.isEmpty()) {
				ArrayList<int[]> tilesToAdd = new ArrayList<>();
				tilesToAdd.addAll(board.getTilesAround(queue.poll())); //Dequeue current tile, add its children to shortlist of tiles to expand upon
				for (int[] tile : tilesToAdd)
					if (!tilesVisited.contains(tile)) {			//for each shortlisted tile, (temp is the same as tile, except in ArrayList format because for some retarded reason Java can't properly run .equals on primitive arrays)
						queue.add(tile);						//add tile to queue if it has not already been visited
						tilesVisited.add(tile);				//add tile to list of visited tiles
						if (board.get(tile) == 0)				//Now, if the current tile is empty, add it to list of available free tiles.
							spaceAvailableToQueens++;
						else if (queenCoords.contains(tile)) //If the current tile is the location of one of the current team's queens, ensure that that encountered queen does not have BFS performed on her by removing her from the list.
							queensToSkip.add(tile);
					}
			}
		}
		return spaceAvailableToQueens;
	}
	private static ArrayList<Integer[]> convIntArrToIntegerArrList(ArrayList<int[]> arr){
		ArrayList<Integer[]> newArr = new ArrayList<>();
		for (int[] coords : arr)
			newArr.add(new Integer[] {coords[0], coords[1]});
		return newArr;
	}
	private static int[] convIntegerArrToIntArr(Integer[] arr){
		int[] newArr = new int[2];
		newArr[0] = arr[0];
		newArr[1] = arr[1];
		return newArr;
	}
}
