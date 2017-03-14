import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JButton;

/**
 * Represents a drawing context. The DoilyPanel draws based on the information in this class.
 * Contains all of the strokes and current properties from the control panel
 * @author Dan
 *
 */
public class DoilyDrawing {
	private DoilyPanel dp; // DoilyPanel which this is attached to
	private Stack<DrawStroke> strokes = new Stack<DrawStroke>(); // Stack of all the strokes in the current drawing
	private Stack<Stack<DrawStroke>> clearedDrawings = new Stack<Stack<DrawStroke>>(); // Stores all the strokes of images which have been cleared from the screen
	private DrawStroke mouseStroke = null; // Stroke where the mouse cursor currently is 
	private DrawStroke currentStroke = null; // Current stroke being drawn
	private JButton undoBtn; // Undo Button referenced so that can be enabled/disabled depending on size of stack

	private int sectorCount; // Current number of sectors to rotate through
	private boolean reflect; // Whether to reflect the points in each sector
	private boolean sectorLinesVisible; // Whether to draw the sector lines
	private int diameter; // Diameter of the draw point
	private Color colour; // Colour of the draw point

	/**
	 * Initialises drawing context with default parameters and attaches to DoilyPanel
	 * @param dp DoilyPanel which will be drawing this
	 */
	public DoilyDrawing(DoilyPanel dp){
		this.dp =dp;
		this.sectorCount = DigitalDoily.START_SECTOR_COUNT;
		this.reflect = DigitalDoily.START_REFLECT_DRAWING;
		this.sectorLinesVisible = DigitalDoily.START_SHOW_SECTOR_LINES;
		this.diameter = DigitalDoily.START_DIAMETER;
		this.colour = DigitalDoily.START_COLOUR;
	}

	/**
	 * Binds the undo button (is optional, but adds feature to disable button)
	 * @param undoBtn Undo Button used to control drawing
	 */
	public void setUndoBtn(JButton undoBtn){
		this.undoBtn = undoBtn;
	}

	/// Below methods control the drawing parameters. Called by the GUI buttons ///

	/**
	 * Erases the current drawing (but not history). The old drawing is added to the clearedDrawings stack
	 */
	public void clear(){
		if (!this.strokes.isEmpty()){
			//Stack<DrawStroke> drawing = new Stack<DrawStroke>();
			//drawing.addAll(this.strokes); // Duplicates 
			this.clearedDrawings.push(this.strokes);
			this.strokes = new Stack<DrawStroke>();
			this.strokes.clear();
			dp.doCompleteRedraw();
		}
	}

	/**
	 * Update the colour of the pen
	 * @param colour New colour for pen
	 */
	public void setColour(Color colour){
		this.colour = colour;
	}

	/**
	 * Returns the current colour of the pen
	 * @return Current colour of pen
	 */
	public Color getColour(){
		return this.colour;
	}

	/**
	 * Sets diameter for pen
	 * @param diameter New Diameter
	 */
	public void setDiameter(int diameter){
		this.diameter = diameter;
	}

	/**
	 * Set whether points should be reflected in each sector
	 * @param reflect Boolean where true equals reflect points
	 */
	public void setReflect(boolean reflect){
		this.reflect = reflect;
	}

	/**
	 * Sets whether the sector lines should be visible in the drawing
	 * @param visible Boolean stating whether sector lines should be visible
	 */
	public void setSectorLineVisible(boolean visible){
		this.sectorLinesVisible = visible;
		dp.doCompleteRedraw();
	}

	/**
	 * Sets the number of sectors in the image
	 * @param sectorCount Number of sectors in image
	 */
	public void setSectorCount(int sectorCount){
		this.sectorCount = sectorCount;
		dp.doCompleteRedraw();
	}

	/**
	 * Undoes previous action taken (or part of action) by popping off of the stroke stack. If the stack is empty it pops the previous drawing off and draws this
	 */
	public void undo(){
		if (this.strokes.isEmpty()){
			this.strokes = this.clearedDrawings.pop();
		} else if (!this.strokes.isEmpty()){
			this.strokes.pop();
		}
		dp.doCompleteRedraw(); // Forces a full redraw of the image (not buffered)

		// Disables the Undo Button if stack now empty (if present)
		if (this.strokes.isEmpty() && this.clearedDrawings.isEmpty() && this.undoBtn != null){
			this.undoBtn.setEnabled(false);
		}
	}

	/**
	 * Allows the stack of draw strokes to be accessed (contains all information to redraw image) 
	 * @return The stack of draw strokes
	 */
	public Stack<DrawStroke> getStrokes(){
		return this.strokes;
	}

	/**
	 * Creates a new point on the drawing. Can be specified as the start of a new stroke or part of a previous one
	 * @param x Polar X co-ordinate 
	 * @param y Polar Y co-ordinate
	 * @param start Whether this is the start of a new stroke
	 */
	public void newPoint(double x, double y, boolean start){
		if (start){
			this.currentStroke = new DrawStroke(this.diameter,this.colour,this.reflect);
			strokes.push(this.currentStroke); // Pushes the new stroke to the stack so can be drawn
			this.currentStroke.moveTo(x, y); // Adds a new point without drawing a new line
		} else {
			this.currentStroke.lineTo(x, y); // Adds a new point, connecting it to the previous one by a line
		}
		
		// Re-enables undo button if necessary
		if (this.undoBtn != null){
			undoBtn.setEnabled(true);
		}
	}

	/**
	 * Sets the co-ordinates of where the mouse currently is, provides the cursor circle on the panel
	 * @param x Polar X co-ordinate
	 * @param y Polar Y co-ordinate
	 */
	public void setMouseStroke(double x, double y){
		this.mouseStroke = new DrawStroke(x,y,this.diameter,this.colour);
	}	

	/**
	 * Clears the mouse stroke (for if cursor is no longer in panel)
	 */
	public void clearMouseStroke(){
		this.mouseStroke = null;
	}

	/**
	 * Returns the Mouse Stroke so can be drawn
	 * @return Mouse Stroke
	 */
	public DrawStroke getMouseStroke(){
		return this.mouseStroke;
	}

	/** Returns whether the sector lines should be visible
	 * @return True if should be visible
	 */
	public boolean drawSectorLines(){
		return this.sectorLinesVisible;
	}

	/**
	 * Gets the current number of sectors for the image to be drawn around
	 * @return Number of sectors
	 */
	public int getSectorCount(){
		return this.sectorCount;
	}

}
