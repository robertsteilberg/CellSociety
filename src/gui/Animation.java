package gui;

import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import simulations.*;
import simulations.AntForaging.ForagingAnts;
import simulations.Fire.Fire;
import simulations.GameOfLife.GameOfLife;
import simulations.Segregation.Segregation;
import simulations.Wator.Wator;
import xml.XMLParser;

/**
 * @author Robert H. Steilberg II | rhs16
 *
 *         The Animation class handles the GUI for the program. After creating
 *         the general Scene, the SimControls class is used to populate the
 *         scene with control elements. The SimEvents class adds functionality
 *         to these elements. Then, according to the default simulation set in
 *         the properties file, the grid is initialized via GridParser class to
 *         that simulation's parameters. The step function is prepared for
 *         stepping, and the simulation begins when the play button is clicked.
 *         The step function updates the simulation grid on each frame via the
 *         GridParser class. Each grid iteration is drawn via the CellNode class
 *         that creates each cell according to a specified shape and then
 *         returns the cell with the correct state according to the simulation.
 *         The simulation continues until it is stopped by the user. The GUI
 *         cell state graph is implemented via the Graph subclass.
 *
 *         Dependencies: SimControls.java, SimEvents.java, CellNode.java,
 *         CellShape.java, GridParser.java, FileBrowser.java, Graph.java
 */

public class Animation {
    private static final String TITLE = "CellSociety";
    private static final String DEFAULT_RESOURCE_PACKAGE = "resources/";
    private static final String LANGUAGE = "English";
    private ResourceBundle myResources;
    private Stage myStage;
    private Pane myRoot;
    private Grid myGrid;
    private Simulation mySimulation;
    private Timeline myTimeline;
    private GridParser myGridParser;
    private Graph myGraph;
    private FileBrowser myFileChooser;
    private String myXMLFilePath;
    protected ComboBox<String> myComboBox;

    public Animation(Stage stage) {
        myStage = stage;
    }

    /**
     * Get the window title for the scene
     *
     * @return the title as a String
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Stop the step function, clear the grid, and prepare for a new simulation
     *
     * @param animation
     *            the current Timeline
     *
     * @param changeXML
     *            true if there is a new XML file to parse, false otherwise
     */
    protected void resetSimulation(Timeline animation, boolean changeXML) {
        animation.stop();
        if (changeXML) {
            String newXMLFilePath = myFileChooser.getXMLFileName(myComboBox.getValue());
            if (newXMLFilePath == null) { // clicked cancel, resume simulation
                animation.play();
                return;
            } else {
                myXMLFilePath = newXMLFilePath;
            }
        }
        myGridParser.clearGrid();
        myGraph.clearGraph();
        initStep(myComboBox.getValue(), myXMLFilePath);
    }

    /**
     * Set the simulation to a specified choice
     *
     * @param simulation
     *            a String corresponding to the desired simulation
     * @param XMLFileName
     *            a String representing the path to an XML file to initialize
     *            the simulation parameters with
     */
    private void setSimulation(String simulation, String XMLFileName) {
        if (simulation.equals(myResources.getString("GameOfLifeSim"))) {
            mySimulation = new GameOfLife(XMLFileName);
        }
        if (simulation.equals(myResources.getString("SegregationSim"))) {
            mySimulation = new Segregation(XMLFileName);
        }
        if (simulation.equals(myResources.getString("WatorSim"))) {
            mySimulation = new Wator(XMLFileName);
        }
        if (simulation.equals(myResources.getString("FireSim"))) {
            mySimulation = new Fire(XMLFileName);
        }
        if (simulation.equals(myResources.getString("ForagingAntsSim"))) {
            mySimulation = new ForagingAnts(XMLFileName);
        }
        myGrid = mySimulation.getGrid();
    }

    /**
     * Set up variables for the step function and then call the step function
     *
     * @param simulation
     *            a String specifying which simulation should be loaded
     * @param XMLFileName
     *            a String representing the path to an XML file to initialize
     *            the simulation parameters with
     */
    protected void initStep(String simulation, String XMLFileName) {
        setSimulation(simulation, XMLFileName);
        // allows us to get the shape of the cell (i.e. hexagon)
        XMLParser parser = new XMLParser(XMLFileName);
        myGraph = new Graph(mySimulation, myRoot);
        myGridParser = new GridParser(mySimulation, myGrid, myResources, myRoot, parser.getNumCellVertices());
        myGridParser.drawGrid(true); // pass true because this is a new grid
        myTimeline = new Timeline();
        int framesPerSecond = Integer.parseInt(myResources.getString("DefaultFPS"));
        // pass in millisecond delay and second delay, respectively
        KeyFrame frame = new KeyFrame(Duration.millis(1000 / framesPerSecond), e -> step(1.0 / framesPerSecond));
        myTimeline.setCycleCount(Timeline.INDEFINITE);
        myTimeline.getKeyFrames().add(frame);
    }

    /**
     * Step through the simulation and update the grid when necessary
     *
     * @param elapsedTime
     *            a number corresponding to the second delay for each frame
     */
    protected void step(double elapsedTime) {
        mySimulation.updateGrid(); // calculate new grid
        // pass false because we are updating an old grid
        myGridParser.drawGrid(false);
        myGraph.updateGraph(myGrid);
    }

    /**
     * Initialize the simulation stage by creating GUI elements and ultimately
     * calling the actual simulation functions
     *
     * @return the scene to be displayed in the window
     */
    public Scene init() {
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + LANGUAGE);
        myFileChooser = new FileBrowser(myStage, myResources);
        myRoot = new Pane();
        myRoot.getStylesheets().add(DEFAULT_RESOURCE_PACKAGE + myResources.getString("CSS"));
        myXMLFilePath = myResources.getString("DefaultSimulationPath");
        initStep(myResources.getString("DefaultSimulation"), myXMLFilePath);
        SimControls controllers = new SimControls(this, myTimeline, myResources);
        controllers.addControls(myRoot);
        Scene simulation = new Scene(myRoot, Integer.parseInt(myResources.getString("WindowWidth")),
                Integer.parseInt(myResources.getString("WindowHeight")));
        return simulation;
    }
}