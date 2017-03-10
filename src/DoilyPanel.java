import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * JPanel that provides the main drawing area and an interface to change properties like pen size etc.
 * @author dmh2g16
 *
 */
public class DoilyPanel extends JPanel{
	private ArrayList<DrawPoint> points = new ArrayList<DrawPoint>(); // Stores all the points on the screen
	private Stack<ArrayList<DrawPoint>> history = new Stack<ArrayList<DrawPoint>>(); // Stack of previous drawings (for undo)
	private DrawPoint currentPoint = null; // Point where mouse cursor currently is (used for point preview) 

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
		this.saveState();
		this.points.clear();
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
	 * Saves the current state of the image by pushing all the points onto the stack
	 */
	public void saveState(){
		this.history.push(new ArrayList<DrawPoint>(points));
	}

	/**
	 * Undoes a previous action by popping off the stack and replacing the current points array with it.
	 * Disables undo button if now empty
	 */
	public void undo(){
		this.points = this.history.pop();;
		this.repaint();
		if (this.history.isEmpty()){
			this.undoBtn.setEnabled(false);
		}
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
		if (sectorLinesVisible){
			this.paintSectorLines(g2d);
		}

		if (this.currentPoint!=null){
			g2d.setColor(this.currentPoint.getColour());
			g2d.fillOval(this.currentPoint.getX(), this.currentPoint.getY(), this.currentPoint.getDiameter(), this.currentPoint.getDiameter());
		}

		if (points.size() > 0){
			// Iterates through each point currently saved
			for (DrawPoint p : points){
				g2d.setColor(p.getColour()); // Sets colour of pen to the colour of the pen
				
				// Repeats for each sector (for rotation)
				for (int i = 0; i < sectorCount; i++){
					// If there is no previous point attached to the point (either single click or start of drag), just draw oval
					if (p.getPrevious() == null){
					g2d.fillOval(this.centX+p.getX(),this.centY+p.getY(), p.getDiameter(), p.getDiameter());
					} else {
						// Otherwise draw a line between the previous point and this one
						DrawPoint prev = p.getPrevious();
						g2d.setStroke(new BasicStroke(p.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
						g2d.drawLine(this.centX+prev.getX(), this.centY+prev.getY(), this.centX+p.getX(), this.centY+p.getY());
					}
					// Rotate drawing context to allow points to be drawn in all sectors
					g2d.rotate(2*Math.PI/sectorCount, centX, centY);
				}
			}
		}
	}

	/**
	 * Paints the sector lines denoting the edge of sectors
	 * @param g2d Graphics object to do drawing
	 */
	public void paintSectorLines(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < sectorCount; i++){
			g2d.drawLine(centX, centY, centX, 0);
			g2d.rotate(2*Math.PI/sectorCount, centX, centY);
		}
	}

	/// PANEL LISTENER ///

	/**
	 * Listener that detects when mouse clicked or dragged to control points in drawing
	 * @author dan
	 */
	class DoilyMouseListener extends MouseAdapter{
		DrawPoint previousPoint; // Previous point added, allows connecting of points
		DrawPoint previousReflectPoint; // Previous reflected point added, allows connecting of points

		/**
		 * If the mouse is dragged, add a point to the array and connect it to the previous point
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			addPoint(e, true);
		}

		/**
		 * When the mouse is pressed down, remove the preview point from the screen, push the current drawing to the stack
		 * so can be undone and add a new point. However this new point is not connected to any previous one
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			saveState();
			currentPoint = null;
			addPoint(e,false);
		}

		/**
		 * When the mouse is moved inside the panel, move the currentPoint so the preview point follows the mouse
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			currentPoint = new DrawPoint(e.getX(),e.getY(),diameter,colour);
			repaint();
		}

		/**
		 * When the mouse leaves the panel remove the preview point
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			currentPoint = null;
			repaint();
		}

		/**
		 * Adds a new point to the array of points
		 * @param e MouseEvent so that co-ordinates of click/drag can be determined
		 * @param connect Whether this point should be connected to the previous one
		 */
		public void addPoint(MouseEvent e, boolean connect){
			// Creates a new DrawPoint. The co-ordinates saved are relative to the centre to allow for dynamic frame resizing
			DrawPoint dp = new DrawPoint(e.getX()-centX,e.getY()-centY,diameter,colour);
			points.add(dp);
			// If this point needs to be connected to the previous one, attach the previous one to it
			if (connect){
				dp.setPrevious(previousPoint);
			}
			previousPoint = dp; // Update itself as the new previous point
		
			// If the point needs to be reflected in each sector
			if (reflect){
				// Similar point to above, however the relative x to centre is inversed (reflected across centre Y axis_
				DrawPoint dpReflect = new DrawPoint(-(e.getX()-centX),e.getY()-centY,diameter,colour);
				points.add(dpReflect);
				// If this point needs to be connected to previous one attach it
				if (connect){
					dpReflect.setPrevious(previousReflectPoint);
				}
				previousReflectPoint = dpReflect; // Sets itself as new previous point
			}
			repaint();
			undoBtn.setEnabled(true); // Allow the image to now be undone
		}

	}
}
