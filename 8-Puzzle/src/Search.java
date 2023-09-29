import java.util.*;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.io.*;
import java.lang.*;
public class Search {

	private int maxNodes = 50;

	/*
	/*
	 * 1- initialize the frontier and visited list 2-create an instance of a puzzle
	 * 2- add this puzzle state created to the frontier while the frontier is not
	 * empty //pop the state with the lowest cost (call this state s) // check if
	 * the popped state is the goal state return path if not: // add the popped
	 * state's successsors and set their parent to be s // calculate the cost
	 * g(n)+h(n) // for the successor list: check if one of them is the goal // else
	 * check compute the cost for each one of them // pop the node with the lowest
	 * cost // if there is a node already in the successor;;; update ot's cost if
	 * there is a lower cost // push the popped state to the visited list //
	 * backtrack to get the path
	 */

	public Puzzle solve_A_star(Puzzle state, String heuristic) {
		int numNodes = 0;
		//initialize the frontier and visited list
		PriorityQueue<Puzzle> frontier = new PriorityQueue<Puzzle>(new costComparator());
		Hashtable<String, Integer> visited = new Hashtable<>();
		
		//initialize the frontier as a hashtable to check if an item is in the frontier
		//this helps in keeping track of what is inside the frontier
		Hashtable<String, Integer> inside_frontier = new Hashtable<>();
		
		// add a state to the frontier
		frontier.add(state);

		inside_frontier.put(state.getCurrent_state(),state.getCost());
		while (!frontier.isEmpty() && numNodes <= maxNodes) {
			//popping a state out of the frontier 
			Puzzle popped_state = frontier.poll();
			//System.out.println("popped state " + popped_state.getCurrent_state());
			//checking if the popped state if the goal state
			//if so, backtrack to print the moves
			if (popped_state.isGoal()) {
				List<String> actions = Backtrack(popped_state);
				for(int i= actions.size() - 1; i >= 0; i--) {
					System.out.println(actions.get(i)+ " ");
				}
				System.out.println("Number of moves needed "+ actions.size());
				System.out.println("numeber of generated nodes " + (visited.size() + frontier.size()));
				
				 return popped_state;					
			}
				
				//if the popped item is not the goal
			 else {
				 //////////
				if (!visited.contains(popped_state.getCurrent_state())) {
				
					// adding the popped state to the visited list
					visited.put(popped_state.getCurrent_state(), popped_state.getCost());

					// Generating successors of the popped state
					List<String> possible_actions = new ArrayList<String>();
					possible_actions = popped_state.legal_action();
					for (int i = 0; i < possible_actions.size(); i++) {
						String action = possible_actions.get(i);
						String newState = popped_state.move(action);
						Puzzle added_state = new Puzzle(newState, popped_state, popped_state.getCost() + 1, action, heuristic);	
						//System.out.println(added_state.getCurrent_state()+ " "+added_state.getTotalCost());
						if (!visited.contains(newState)&& !inside_frontier.contains(added_state.getCurrent_state())) {
							
							
							frontier.add(added_state);
							inside_frontier.put(added_state.getCurrent_state(),added_state.getCost());
							
						}
						//Checking if item is not in the visited but is already in the frontier 
						else if(!visited.contains(added_state.getCurrent_state())&& inside_frontier.contains(added_state.getCurrent_state())) {
							 //Compare the costs and update the cost if a cheaper cost is found
							if(added_state.getCost() < inside_frontier.get(added_state.getCurrent_state())) {
								inside_frontier.put(added_state.getCurrent_state(), added_state.getCost());
								update_cost(added_state,frontier);
							}
						}
					}
				}
			}
			numNodes++;
		}
		System.out.println("numeber of generated nodes " + (visited.size() + frontier.size()));
		return null;	
	}

	// add the source node to the frontier
	// pop it out
	// check if the node is the goal node
	// otherwise, add the successors with their costs to the expand list 
	// add the top  W elements to the children list
	// check if any of the children is the goal node
	
	//  
	/*helper method: this method is to update the cost if a state is in the frontier 
	 * and a cheaper cost for the same sate is found*/
	public void update_cost(Puzzle added, PriorityQueue<Puzzle> pq) {
		//iterating over the priority queue
			for(Puzzle element: pq ) {
				if(element.getCurrent_state().equals(added.getCurrent_state())) {
					pq.remove(element);
					pq.add(added);
				}
			}
	}
	
	public Puzzle solveBeam(int beamWidth, Puzzle state, String heuristic) { 
		int nodeCounter = 0;
		//initialize the frontier, children and visited lists
		PriorityQueue<Puzzle> frontier = new PriorityQueue<Puzzle>(new costComparator()); 
	    PriorityQueue<Puzzle> Children = new PriorityQueue<Puzzle>(new costComparator());
	  
	    Hashtable<String,Integer> visitedBeam = new Hashtable<>();	  
	    //add the state to the frontier
	    frontier.add(state);
	    /// if to check it the puzzle is a goal state and then else statement
	    if(state.isGoal()) {
	    	System.out.println("the initial state is a goal state");
			System.out.println("numeber of generated nodes is 0");
	    	return state;
	    }
	    while(!frontier.isEmpty()&& maxNodes >= nodeCounter) {
	    	/* 0- while loop for :
	    	 * 1- pop all items in frontier
	    	 * for lopp for checking all the items in the frontier
	    	 * 2- check the children of all the popped items
	    	 * 3- add the children to the children prirority queue
	    	 * 
	    	 * another outside while loop
	    	 * 4- take best k children and put them in frontier 
	    	 * 5- empty the children prirotiy queue 
	    	 * 
	    	 * */
	    	
	    	while(!frontier.isEmpty()){
	    		//popping a state
	    		Puzzle popped_state = frontier.poll();
	    		//System.out.println("popped state " + popped_state.getCurrent_state());
    			List<String> possible_actions = new ArrayList<String>();
    			possible_actions = popped_state.legal_action(); 
				 for (int i = 0; i < possible_actions.size(); i++) {
						String action = possible_actions.get(i);
						String newState = popped_state.move(action);
						//checking if the state is in the visited
						if (!visitedBeam.contains(newState)) {
							Puzzle added_state = new Puzzle(newState, popped_state,1, action, heuristic);
							//System.out.println(added_state.getCurrent_state()+ " "+added_state.getTotalCost());
							if(added_state.isGoal()) {
								List<String> actions = Backtrack(added_state);
								for(int j = actions.size() - 1; j >= 0; j--) {
									System.out.println(actions.get(j)+ " ");
								}
								System.out.println("Number of moves needed "+ actions.size());	
								System.out.println("numeber of generated nodes " + (visitedBeam.size() + frontier.size()));
								return added_state;
							}		
							//empty the children
							Children.add(added_state);
						}
						
				}    		
	    		
	    	}
	    	int counter = 0;
	    	while(!Children.isEmpty() && counter < beamWidth){
	    		frontier.add(Children.poll());
	    		counter++;
	    	}
	    	Children.clear();
	    	nodeCounter++;
	  } 
		System.out.println("numeber of generated nodes " + (visitedBeam.size() + frontier.size()));
	    return null;
	  }

	/*A method for setting the maximum number of states to be explored before the 
	 * search algorithm returns that the puzzle is unsolvable 
	 * @param: integer maximum number of nodes*/
	public void maxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}	  
	
	/*helper method: this method helps in backtracking when the goal state is found*/
	public List<String> Backtrack(Puzzle state) {
		List<String> actions_taken = new ArrayList<String>();
		while(state.getParent() != null) {
			actions_taken.add(state.getAction());
			state = state.getParent();
		}
		return actions_taken;
	}
	    
	 
	public static void main(String[] args) {

	//Puzzle j = new Puzzle("14356b278", null, 0, null,"h1");	

	
	//j.test();
	
	}
}	

class costComparator implements Comparator<Puzzle>{
	public int compare(Puzzle p1, Puzzle p2) {
		if (p1.getTotalCost() > p2.getTotalCost()) {
			return 1;
		} else if (p1.getTotalCost() < p2.getTotalCost()) {
			return -1;
		}
							return 0;
	}
}

class Puzzle {
	private String current_state;
	private Puzzle parent;
	private int cost;
	private String action;
	private String heuristic;

	// constructor
	public Puzzle(String state, Puzzle parent, int cost, String action, String heuristic) {
		this.current_state = state;
		this.parent = parent;
		this.cost = cost;
		this.action = action;
		this.heuristic = heuristic;
	}
	
	public String getAction() {
		return action;
	}

	public String getCurrent_state() {
		return current_state;
	}

	public void setCurrent_state(String current_state) {
		this.current_state = current_state;
	}
	
	public Puzzle getParent() {
		return parent;
	}
	
	public void setParent(Puzzle newParent) {
		parent = newParent;
	}

	public int getCost() {
		return cost;
	}
	
	public int getTotalCost(){
	
		if(heuristic.toLowerCase().equals("h1")) {
			return getCost() + h1(); 
		}
		return getCost() + h2(); 
		
	}	
	

	// A method that prints the current puzzle state
	public void printState() {
		char matrix[][] = new char[3][3];
		int k = 0;

		// printing the current puzzle state
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (k < getCurrent_state().length()) {
					matrix[i][j] = getCurrent_state().charAt(k);
				}
				k++;
			}
		}

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}

	}
	
	/* A method that makes n random moves from the goal state
	 * @param: int n random moves*/	
	public String randomizeState(int n) {
		// a for loop that loops n times
		String randState = null;
		Random random = new Random();
		Puzzle randomMove = new Puzzle("b12345678",null,0,null,null);
		String temp = null;
		for (int i = 0; i < n; i++) {
			int randomNum = random.nextInt(randomMove.legal_action().size());
			temp = randomMove.legal_action().get(Math.abs(randomNum));
			randState = randomMove.move(temp);
			try {
				randomMove.setState(randState);
				//System.out.println(randState);
			} catch (InvalidStateException e) {
				System.out.print("Exception" + e);
			}
		}
		// Returning the state
		return randState;
	}

	/*
	 * a method for reading the commands in a path 
	 * @param: filepath the method prints the commands in the file
	 */
	public void readCommands(String path){
		try {
		File pathfile = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(pathfile));
		String word;
		Search temp = new Search();
		Puzzle test = new Puzzle("3124b5678",null,0,null,null); 

		while ((word = br.readLine()) != null) {			
			//System.out.print(word.substring(0,12)+" kkk");
			if(word.substring(0,4).equals("move")) {
				test.move(word.substring(5));
				if(word.substring(5).equals("up")) {
					System.out.println("Before movement:" + test.getCurrent_state());					
					String tempState = move("up");
					System.out.println("After movement: " +tempState);				
				}
				else if(word.substring(5).equals("down")) {
					System.out.println("Before movement:" + test.getCurrent_state());
					String tempState = move("down");
					System.out.println("After movement: " +tempState);						
				}
				else if(word.substring(5).equals("left")) {
					System.out.println("Before movement:" + test.getCurrent_state());					
					String tempState = move("left");
					System.out.println("After movement: " +tempState);					
				}				
				else {
					System.out.println("Before movement:" + test.getCurrent_state());					
					String tempState = move("right");
					System.out.println("After movement: " + tempState);					
				}				
			}
			else if(word.substring(0,8).equals("setState")) {
				Puzzle p = new Puzzle(word.substring(9),null,0,null,null);
				p.setState(word.substring(9));
				System.out.println("The new current state: " + p.getCurrent_state());
			}			

			else if(word.substring(0,10).equals("printState")) {
				printState();	
			}
	
			else if(word.substring(0,14).equals("randomizeState")) {
				randomizeState(Integer.parseInt(word.substring(15)));
				System.out.print(randomizeState(Integer.parseInt(word.substring(15))));
			}
			else if(word.substring(0,12).equals("solve_A-star")) {			
				Puzzle p = new Puzzle(word.substring(13,22),null,0,null,word.substring(23));				
				temp.solve_A_star(p,word.substring(23));
			}
			else if(word.substring(0,9).equals("solveBeam")) {
				
				Puzzle p = new Puzzle(word.substring(12,21),null,0,null,word.substring(10,11));
				temp.solveBeam(Integer.parseInt(word.substring(10,11)),p,word.substring(22));
			}			


						
						
		}
		}catch(Exception e) {
			System.out.print("exception"+ e);
		}
	}
/*helper method: finds the blank tile location*/
	public int get_blank_tile_location(String state) {
		int blank_tile_location = 0;
		//iterates through the state to find the index of the tile location
		for (int i = 0; i < 9; i++) {
			if (state.charAt(i) == 'b') {
				blank_tile_location = i;
			}
		}
		return blank_tile_location;
	}

	/*A helper method for finding the X coordinates for value in the puzzle state
	 * @param: integer*/
	public int getX(int value) {
		return Math.floorDiv(value, 3);
	}
	
	/*A helper method for finding the Y coordinates for value in the puzzle state
	 * @param: integer*/
	public int getY(int value) {
		return value % 3;
	}
	
	/*A function for calculating the heuristic cost 
		it calculates the number of misplaced tiles
	 	@param: puzzle state */	
	public int h1() {
		int counter = 0;

		for (int i = 1; i < current_state.length(); i++) {
			if (i != (current_state.charAt(i) - 48)) {
				counter++;
			}
		}
		return counter;
	}
	
	/*A function for calculating the heuristic cost 
		it calculates the sum of the distances of the tiles from their goal positions
	 * @param: puzzle state*/
	public int h2() {
		int counterX;
		int counterY;
		int totalH = 0;
		for (int i = 0; i < 9; i++) {
			char x = current_state.charAt(i);
			if (current_state.charAt(i) != 'b') {
				counterX = Math.abs(getX(i) - getX(x - 48));
				counterY = Math.abs(getY(i) - getY(x - 48));
				totalH = totalH + counterY + counterX;
			}
		}
		return totalH;
	}
	
	/* A helper method that checks whether the state is the goal state */
	public boolean isGoal() {
		if (current_state.toLowerCase().equals("b12345678")) {
			return true;
		}
		return false;
	}	
/*A method for setting the state and throws an exception in case of invalid puzzle state
 * @param: state*/
	 public void setState(String newState)throws InvalidStateException{
		 if(is_valid_state(newState) == true ) { 
			 current_state = newState; 
		} 
		else {
			throw new InvalidStateException("Incorrect input"); 
		}
	}	
	// A helper method to check whether the values entered are within the right
	// range
	public boolean valid_range(String state) {
		for (int i = 0; i < state.length(); i++) {
			if (Character.isDigit(state.charAt(i))) {
				int value = state.charAt(i) - 48;
				if ((value > 8) || (value < 0)) {
					return false;
				}
			} else if (Character.isDigit(state.charAt(i)) == false) {
				if (state.charAt(i) != 'b') {
					return false;
				}
			}
		}
		return true;
	}

	// A helper method for is_valid_state method
	// this method checks if the string entered has duplicate numbers
	public boolean has_duplicates(String state) {
		HashSet<Character> set = new HashSet<Character>();
		for (int i = 0; i < state.length(); i++) {
				set.add(state.charAt(i));
		}
		if (set.size() < 9) {
			return true;
		}
		return false;
	}
	
	
	// helper method for set state
	public boolean is_valid_state(String state) {
		if (state.length() != 9) {
			return false;
		}
		else if (has_duplicates(state) == true) {
			return false;
		}
		else if (valid_range(state) == false) {
			return false;
		}

		return true;

	}
	
	  /*A method for moving the blank tile location up*/
	public String move_up(String state) {
		String newState;
		int tile_pos = get_blank_tile_location(state);
		// changed if condition from til post >= to tile pos -3 >=0
		newState = Swap(state, tile_pos, tile_pos - 3);
		return newState;

	}
	/* A method for moving the blank tile location down */

	public String move_down(String state) {

		String newState;
		int tile_pos = get_blank_tile_location(state);
		newState = Swap(state, tile_pos, tile_pos + 3);
		return newState;

	}
	/* A method for moving the blank tile location left */

	public String move_left(String state) {

		String newState;
		int tile_pos = get_blank_tile_location(state);
		newState = Swap(state, tile_pos, tile_pos - 1);
		return newState;
	}

	/* A method for moving the blank tile location right */

	public String move_right(String state) {

		String newState;
		int tile_pos = get_blank_tile_location(state);
		newState = Swap(state, tile_pos, tile_pos + 1);
		return newState;
	}
	

	/* A helper method that checks the possible actions for a state */
	public List<String> legal_action() {
		List<String> list = new ArrayList<String>();
		// if(direction.toLowerCase() == "up") {
		int tile_pos = get_blank_tile_location(current_state);
		if (tile_pos - 3 >= 0) {
			// move_up(state);
			list.add("up");
		}
		if (tile_pos + 3 <= 8) {
			list.add("down");
		}
		//Math.floorDiv(tile_pos, 3) == 1 || Math.floorDiv(tile_pos, 3)== 2
		if (tile_pos % 3 == 1 || tile_pos % 3 == 2) {
			list.add("left");
		}
		if (tile_pos % 3 == 0 || tile_pos % 3 == 1) {
			list.add("right");
		}	
		return list;
	}
	/*method for moving tile 
	 * @param: direction*/
	public String move(String direction) {
		
		if(direction.toLowerCase()== "up") {
			return move_up(current_state);
		}
		else if(direction.toLowerCase() == "down") {
			return move_down(current_state);
		}
		else if(direction.toLowerCase() == "left") {
			return move_left(current_state);
			
		} else {
			return move_right(current_state);
		}
		
	}
	/* A helper method for moving the blank tile up, down, left and right */
	public String Swap(String state, int a, int b) {
		char[] arr = state.toCharArray();
		char temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;

		return new String(arr);
	}
	
	public void test(){
		Search test = new Search();
		String testcase = randomizeState(20);
		System.out.println(testcase);
		Puzzle test1 = new Puzzle(testcase,null,0,null,null);
		test1.printState();
		System.out.println("Results for A-star search using h1");
		test.solve_A_star(test1,"h1");
		if(test.solve_A_star(test1,"h1")==null) {
			System.out.println("A-star using h1: The puzzle couldn't be solved");
		}
		System.out.println("----------------------------------");
		System.out.println("Results for A-star search using h2");		
		test.solve_A_star(test1,"h2");
		if(test.solve_A_star(test1,"h2")==null) {
			System.out.println("A-star using h2:The puzzle couldn't be solved");
		}		
		System.out.println("----------------------------------");		
		System.out.println("Results for Beam search using h1");		
		test.solveBeam(1,test1,"h1");
		if(test.solveBeam(1,test1,"h1")==null) {
			System.out.println("Beam search using h1:The puzzle couldn't be solved");
		}		
		
		System.out.println("----------------------------------");
		
		System.out.println("Results for Beam search using h2");		
		test.solveBeam(3,test1,"h2");	
		System.out.print(test.solveBeam(1,test1,"h2"));
		if(test.solveBeam(3,test1,"h2")==null) {
			System.out.println("Beam search using h2:The puzzle couldn't be solved");
		}	
						
		
		
	}

	 
}
