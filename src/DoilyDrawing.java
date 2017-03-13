import java.awt.Color;
import java.util.Stack;

import javax.swing.JButton;

public class DoilyDrawing {
	private DoilyPanel dp;
	private Stack<DrawStroke> strokes = new Stack<DrawStroke>();
	private Stack<Stack<DrawStroke>> clearedDrawings = new Stack<Stack<DrawStroke>>();
	private DrawStroke mouseStroke =null;
	private DrawStroke currentStroke = null;
	private JButton undoBtn;

	private int sectorCount; // Current number of sectors to rotate through
	private boolean reflect; // Whether to reflect the points in each sector
	private boolean sectorLinesVisible; // Whether to draw the sector lines
	private int diameter; // Diameter of the draw point
	private Color colour; // Colour of the draw point

	public DoilyDrawing(DoilyPanel dp){
		this.dp =dp;
		this.sectorCount = DigitalDoily.START_SECTOR_COUNT;
		this.reflect = DigitalDoily.START_REFLECT_DRAWING;
		this.sectorLinesVisible = DigitalDoily.START_SHOW_SECTOR_LINES;
		this.diameter = DigitalDoily.START_DIAMETER;
		this.colour = DigitalDoily.START_COLOUR;
	}

	public void setUndoBtn(JButton undoBtn){
		this.undoBtn = undoBtn;
	}

	/// Below methods control the drawing parameters. Called by the GUI buttons ///

	/**
	 * Erases the current drawing (but not history). The old drawing is added to the history stack
	 */
	public void clear(){
		if (!this.strokes.isEmpty()){
			Stack<DrawStroke> drawing = new Stack<DrawStroke>();
			drawing.addAll(this.strokes);
			this.clearedDrawings.push(drawing);
			this.strokes.clear();
			dp.repaint();
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
		dp.repaint();
	}

	/**
	 * Sets the number of sectors in the image
	 * @param sectorCount Number of sectors in image
	 */
	public void setSectorCount(int sectorCount){
		this.sectorCount = sectorCount;
		dp.repaint();
	}

	/**
	 * Undoes a previous action by popping off the stack and replacing the current points array with it.
	 * Disables undo button if now empty
	 */
	public void undo(){
		if (this.strokes.isEmpty()){
			this.strokes = this.clearedDrawings.pop();
		} else if (!this.strokes.isEmpty()){
			this.strokes.pop();
		}
		dp.repaint();
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

	public void newPoint(double x, double y, boolean start){
		if (start){
			this.currentStroke = new DrawStroke(this.diameter,this.colour,this.reflect);
			strokes.push(this.currentStroke);
			this.currentStroke.moveTo(x, y);
		} else {
			this.currentStroke.lineTo(x, y);
		}
		undoBtn.setEnabled(true);
	}

	public void setMouseStroke(double x, double y){
		this.mouseStroke = new DrawStroke(x,y,this.diameter,this.colour);
	}	

	public void clearMouseStroke(){
		this.mouseStroke = null;
	}

	public DrawStroke getMouseStroke(){
		return this.mouseStroke;
	}

	public boolean drawSectorLines(){
		return this.sectorLinesVisible;
	}

	public int getSectorCount(){
		return this.sectorCount;
	}

}
