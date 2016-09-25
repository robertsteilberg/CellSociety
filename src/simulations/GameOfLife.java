package simulations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;
import xml.GameOfLifeXMLParser;;


/**
 * @author samuelcurtis
 * 
 * GameOfLife class handles the logic of initializing a grid of cells and updating their states 
 * according to the Game of Life simulation rules. Offers two public methods, getGrid() and updateGrid() so
 * that an Animation object can get the grid and continually update it to allow for the visualization of the simulation.
 *
 */

public class GameOfLife extends Simulation {


	public static final String DEFAULT_RESOURCE_PACKAGE = "resources/";
	public static final String LANGUAGE = "English";

	private Grid myGrid;
	private final GameOfLifeXMLParser myParser;
	private final int probCellAlive;
	private final ArrayList<String> possibleStates;
	private final HashMap<String, Color> stateToColorMap;
	
	/**
	 * Reads in the data for probCellAlive and the grid dimensions using the GameOfLifeXMLParser object. 
	 * Initializes the grid. 
	 */
	public GameOfLife() {
		myParser = new GameOfLifeXMLParser("data/GameOfLife.xml");
		probCellAlive = myParser.getProbOfCellAlive();
		possibleStates = new ArrayList<String>();
		stateToColorMap = new HashMap<>();
		myGrid = new Grid(myParser.getGridWidth(),myParser.getGridHeight());
		setPossibleStates();
		mapStatesToColors();
		setInitialGridState();
	}
	
	private void mapStatesToColors() {
		stateToColorMap.put(possibleStates.get(0),Color.RED);
		stateToColorMap.put(possibleStates.get(1), Color.BLUE);
	}

	/**
	 * Updates the next state for every cell so that cells are not computing their states
	 * based on neighbors in different generations.
	 * Once each cells next state is correctly calculated, commitStates() goes through all of the cells
	 * and sets their current state to the next state calculated previously.
	 */
	public void updateGrid(){
		updateNextStates();
		commitStates();
	}
	
	/**
	 * @return the Grid object. 
	 * Used by the Animation class to view the states of all the cells, and allows 
	 * the Animation class to repeatedly call updateGrid() on this grid object to allow 
	 * for visualization of the simulation.
	 */
	public Grid getGrid(){
		return myGrid;
	}
	
	/**
	 * Since there are only two possible states for cells in this simulation mode,
	 * this method adds both possible states to the arraylist of possible states.
	 */
	protected void setPossibleStates() {
		ResourceBundle resources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + LANGUAGE);
		possibleStates.add(resources.getString("Dead"));
		possibleStates.add(resources.getString("Alive"));
	}
	
	
	/**
	 * For each cell in the grid, a random initial state is set, and its neighbors are calculated.
	 */
	protected void setInitialGridState(){
		for(int i = 0; i < myGrid.getHeight(); i++){
			for(int j = 0; j < myGrid.getWidth(); j++){
				Cell currCell = myGrid.getCell(i, j);
				setRandomInitialState(probCellAlive, currCell);
				addNeighbors(currCell);
			}
		}
		

	}
	


	/**
	 * @param prob_Cell_Alive represents the probability that a cell is alive at the start of the simulation.
	 * @param currCell is the cell for which we want an initial state to be randomly chosen
	 */
	private void setRandomInitialState(int prob_Cell_Alive, Cell currCell) {
		Random random = new Random();
		int randNum = random.nextInt(101);
		if(randNum > prob_Cell_Alive){
			currCell.setCurrState(possibleStates.get(0),stateToColorMap.get(possibleStates.get(0)));
		}
		else{
			currCell.setCurrState(possibleStates.get(1),stateToColorMap.get(possibleStates.get(1)));
		}
		
	}
	
	
	
	
	/**
	 * Goes through each cell in the grid and calculates what its next state will be 
	 * based on the Game of Life rules.
	 */
	protected void updateNextStates(){
		for(int i = 0; i < myGrid.getHeight(); i++){
			for(int j = 0; j<myGrid.getWidth(); j++){
				Cell currCell = myGrid.getCell(i, j);
				int numNeighborsAlive = calculateNumNeighborsAlive(currCell);
				if(currCell.getCurrState() == "Dead"){
					if(numNeighborsAlive == 3){
						currCell.setNextState("Alive");
						continue;
					}
					else{
						currCell.setNextState("Dead");
						continue;
					}
				}
				else{
					if(numNeighborsAlive == 2 || numNeighborsAlive == 3){
						currCell.setNextState("Alive");
						continue;
					}
					else{
						currCell.setNextState("Dead");
					}
				}
				
			}
		}
	}
	
	/**
	 * Goes through every cell in the grid and finalizes their updated state after the correct
	 * next state has been calculated for all cells. 
	 */
	protected void commitStates(){
		for(int i = 0; i < myGrid.getHeight(); i++){
			for(int j = 0; j<myGrid.getWidth(); j++){
				Cell currCell = myGrid.getCell(i, j);
				currCell.commitState(stateToColorMap.get(currCell.getNextState()));
			}
		}
	}
	
	/**
	 * @param currCell is the cell for which we want to calculate its neighbors and store
	 * them. 
	 * 
	 * This method calls two helper methods, getNeighborIPosition and getNeighborJposition,
	 * which assist in the logic of calculating the neighbors of edge cells. 
	 */
	protected void addNeighbors(Cell currCell) {
		Tuple position = currCell.getPosition();
		for(int k = -1; k<2; k++){
			for(int g = -1; g<2; g++){
				int i = getNeighborIPosition(position.getIPos(), k);
				int j = getNeighborJPosition(position.getJPos(), g);
				if (!(i == position.getIPos() && j == position.getJPos())) {
					currCell.addNeighbor(myGrid.getCell(i, j));
				}
			}
		}
	}
	
	/**
	 *
	 * @param j - the j poisition of the current cell for which we are calculating the j position for a given neighbor
	 * @param g - an int between -1 and 1 that represents a relative J position to the current cell of a given neighbor
	 * @return the position of the current neighbor, with edge cell case accounted for
	 */
	private int getNeighborJPosition(int j, int g) {
		int jPos = j + g;
		if(jPos < 0){
			jPos = myGrid.getHeight()-1;
		}
		
		if(jPos > myGrid.getHeight()-1){
			jPos = 0;
		}
		return jPos;
	}

	/**
	 *
	 * @param i - the I poisition of the current cell for which we are calculating the I position for a given neighbor
	 * @param k - an int between -1 and 1 that represents a relative I position to the current cell of a given neighbor
	 * @return the position of the current neighbor, with edge cell case accounted for
	 */
	private int getNeighborIPosition(int i, int k){
		int iPos = i + k;
		if(iPos < 0){
			iPos = myGrid.getWidth()-1;
		}
		
		if(iPos > myGrid.getWidth()-1){
			iPos = 0;
		}
		return iPos;
	}
	
	/**
	 * @param currCell is the cell for which we are calculating neighbors for
	 * @return the number of neighbors of currCell that are currently alive.
	 */
	private int calculateNumNeighborsAlive(Cell currCell) {
		int numAlive = 0;
		for(Cell neighbor : currCell.getNeighbors()){
			if(neighbor.getCurrState().equals("Alive")){
				numAlive += 1;
			}
		}
		return numAlive;
	}
	
	/**
	 * returns the number of columns in the grid
	 */
	public int getGridWidth() {
		return myParser.getGridWidth();
	}
	
	/**
	 * returns the number of rows in the grid
	 * @return
	 */
	public int getGridHeight() {
		return myParser.getGridHeight();
	}
	
	
}
