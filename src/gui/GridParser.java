package gui;

import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import simulations.*;

/**
 * @author Robert H. Steilberg II | rhs16
 *         <p>
 *         The GridParse class parses the grid given in each step from whichever
 *         Simulation instance is running. This class handles printing the grid
 *         for the first time and updating the grid through each step of the
 *         simulation. This class also accounts for different cell shapes, such
 *         as drawing the grid with triangles or hexagons.
 *         <p>
 *         Dependencies: CellNode.java, Graph.java
 */
public class GridParser {
    private Simulation mySimulation;
    private Grid myGrid;
    private ResourceBundle myResources;
    private Pane myRoot;
    private int myNumCellVertices;

    GridParser(Simulation sim, Grid grid, ResourceBundle resources, Pane root, int numCellVertices) {
        mySimulation = sim;
        myGrid = grid;
        myResources = resources;
        myRoot = root;
        myNumCellVertices = numCellVertices;
    }

    /**
     * Clear the grid and re-initialize the simulation
     */
    protected void clearGrid() {
        for (int i = 0; i < mySimulation.getGridHeight(); i++) {
            for (int j = 0; j < mySimulation.getGridWidth(); j++) {
                String id = Integer.toString(i) + Integer.toString(j);
                // get each node via CSS id
                Node toDelete = myRoot.lookup("#" + id);
                myRoot.getChildren().remove(toDelete);
            }
        }
    }

    /**
     * Draw the grid to the scene
     *
     * @param newGrid true if the grid is being drawn for the first time, false
     *                otherwise
     */
    protected void drawGrid(boolean newGrid) {
        if (!newGrid) {
            clearGrid();
        }
        double cellSize = Integer.parseInt(myResources.getString("GridSize")) / mySimulation.getGridHeight();
        for (int i = 0; i < mySimulation.getGridHeight(); i++) {
            for (int j = 0; j < mySimulation.getGridHeight(); j++) {
                CellNode node = new CellNode();
                Polygon cell = node.getCellNode(myGrid, cellSize, Integer.parseInt(myResources.getString("GridOffset")),
                        i, j, myNumCellVertices);
                if (newGrid) {
                    String id = Integer.toString(i) + Integer.toString(j);
                    // set a CSS id so we can get this cell later to remove it
                    cell.setId(id);
                }
                myRoot.getChildren().add(cell);
            }
        }
    }
}
