import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * JPanel that provides the main drawing area and an interface to change properties like pen size etc.
 * @author dmh2g16
 *
 */
public class DoilyPanel extends JPanel{
	private Stack<DrawStroke> strokes = new Stack<DrawStroke>();
	private DrawStroke mouseStroke =null;

	private JButton undoBtn; // Undo button as stack needs to control whether this is enabled

	private int sectorCount; // Current number of sectors to rotate through
	private int centX, centY; // Centre position of the panel
	private boolean reflect; // Whether to reflect the points in each sector
	private boolean sectorLinesVisible; // Whether to draw the sector lines
	private int diameter; // Diameter of the draw point
	private Color colour; // Colour of the draw point

	/**
	 * Sets up the panel for drawing with default parameters set in main class
	 * @param undoBtn
	 */
	public DoilyPanel(JButton undoBtn){
		this.sectorCount = DigitalDoily.START_SECTOR_COUNT;
		this.reflect = DigitalDoily.START_REFLECT_DRAWING;
		this.sectorLinesVisible = DigitalDoily.START_SHOW_SECTOR_LINES;
		this.diameter = DigitalDoily.START_DIAMETER;
		this.colour = DigitalDoily.START_COLOUR;

		this.undoBtn = undoBtn;
		
		this.setBackground(Color.BLACK);

		// Attaches listeners that control drawing
		DoilyMouseListener listener = new DoilyMouseListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
	}

	/// Below methods control the drawing parameters. Called by the GUI buttons ///

	/**
	 * Erases the current drawing (but not history). The old drawing is added to the history stack
	 */
	public void clear(){
		this.strokes.clear();
		this.undoBtn.setEnabled(false);
		this.repaint();
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
		this.repaint();
	}

	/**
	 * Sets the number of sectors in the image
	 * @param sectorCount Number of sectors in image
	 */
	public void setSectorCount(int sectorCount){
		this.sectorCount = sectorCount;
		this.repaint();
	}

	/**
	 * Undoes a previous action by popping off the stack and replacing the current points array with it.
	 * Disables undo button if now empty
	 */
	public void undo(){
		this.strokes.pop();
		this.repaint();
		if (this.strokes.isEmpty()){
			this.undoBtn.setEnabled(false);
		}
	}
	
	public Stack<DrawStroke> getStrokes(){
		return this.strokes;
	}
	
	public void setStrokes(Stack<DrawStroke> strokes){
		this.strokes = strokes;
		repaint();
	}

	/// GRAPHICS FUNCTIONS ///

	/**
	 * Main drawing function. Draws sector lines if option enabled and a point under the mouse cursor (for preview).
	 * Then draws all the points in the array, connecting lines if necessary to produce smooth lines
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);

		this.centX = (this.getWidth()/2);
		this.centY = (this.getHeight()/2);

		
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(centX, centY);
		if (sectorLinesVisible){
			this.paintSectorLines(g2d);
		}
		
		Iterator<DrawStroke> it = this.strokes.iterator();
		while (it.hasNext()){			
			DrawStroke curr = it.next();
			g2d.setColor(curr.getColour());
			g2d.setStroke(new BasicStroke(curr.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			for (int i = 0; i < sectorCount; i++){
				g2d.draw(curr);
				if (curr.isReflected()){
					g2d.scale(-1, 1);
					g2d.draw(curr);
					g2d.scale(-1, 1);
				}
				g2d.rotate(2*Math.PI/sectorCount);
			}
		}
		
		if (this.mouseStroke!=null){
			g2d.setColor(this.mouseStroke.getColour());
			g2d.setStroke(new BasicStroke(this.mouseStroke.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g2d.draw(this.mouseStroke);
		}
	}

	/**
	 * Paints the sector lines denoting the edge of sectors
	 * @param g2d Graphics object to do drawing
	 */
	public void paintSectorLines(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < sectorCount; i++){
			if (getHeight() > getWidth()){
				g2d.drawLine(0, 0, 0, -(getHeight()/2));
			} else {
				g2d.drawLine(0, 0, -(getWidth()/2), 0);
			}
			g2d.rotate(2*Math.PI/sectorCount);
		}
	}

	/// PANEL LISTENER ///

	/**
	 * Listener that detects when mouse clicked or dragged to control points in drawing
	 * @author dan
	 */
	class DoilyMouseListener extends MouseAdapter{
		DrawStroke currentStroke = null;

		/**
		 * If the mouse is dragged, add a point to the array and connect it to the previous point
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			addPoint(e);
		}

		/**
		 * When the mouse is pressed down, remove the preview point from the screen, push the current drawing to the stack
		 * so can be undone and add a new point. However this new point is not connected to any previous one
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			mouseStroke = null;
			this.currentStroke = new DrawStroke(diameter,colour,reflect);
			strokes.push(this.currentStroke);
			addPoint(e);
		}

		/**
		 * When the mouse is moved inside the panel, move the currentPoint so the preview point follows the mouse
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			mouseStroke = new DrawStroke(e.getX()-centX,e.getY()-centY, diameter, colour);
			repaint();
		}

		/**
		 * When the mouse leaves the panel remove the preview point
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			mouseStroke = null;
			repaint();
		}

		/**
		 * Adds a new point to the array of points
		 * @param e MouseEvent so that co-ordinates of click/drag can be determined
		 * @param connect Whether this point should be connected to the previous one
		 */
		public void addPoint(MouseEvent e){
			double relX = e.getX()-centX;
			double relY = e.getY()-centY;
			
			this.currentStroke.addPoint(relX, relY);

			repaint();
			undoBtn.setEnabled(true); // Allow the image to now be undone
		}

	}
}
