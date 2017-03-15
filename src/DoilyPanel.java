import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 * JPanel that provides the main drawing area. Draws information that is stored in a DoilyDrawing Object
 * @author dmh2g16
 *
 */
public class DoilyPanel extends JPanel{
	private DoilyDrawing drawing; // DoilyDrawing which this panel is drawing
	private int centX, centY; // Centre position of the panel
	private BufferedImage drawingImg; // Stores a cache of the current image to improve performance
	
	/**
	 * Sets up the panel for drawing by attaching it to a DoilyDrawing and setting up mouse listeners
	 */
	public DoilyPanel(){		
		this.setBackground(Color.BLACK);
		this.drawing = new DoilyDrawing(this);
		
		// Attaches listeners that control drawing
		DoilyMouseListener listener = new DoilyMouseListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		
		this.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				redrawImage();
			}
		});
	}

	public void init(){
		this.drawingImg = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Returns the doily drawing used by this panel. Provided so listeners can control this
	 * @return
	 */
	public DoilyDrawing getDoilyDrawing(){
		return this.drawing;
	}

	/**
	 * Forces a complete redraw of the image to the BufferedImage cache
	 */
	public void redrawImage(){
		init();
		Iterator<DrawStroke> it = this.drawing.getStrokes().iterator();
		while (it.hasNext()){			
			this.paintStroke(it.next());
		}
		repaint();
	}
	
	/// GRAPHICS FUNCTIONS ///

	/**
	 * Main drawing function. Draws sector lines if option enabled and a point under the mouse cursor (for preview).
	 * Then either draws all of the strokes in the stack, or the BufferedImage cache and the most recent stroke (depending on whether repaint is full)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		this.centX = (this.getWidth()/2);
		this.centY = (this.getHeight()/2);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(centX, centY); // Translates so 0,0 is in centre (allows relative drawing)
		if (this.drawing.drawSectorLines()){
			this.paintSectorLines(g2d);
		}
		
		// Only draw the image if it is initialised
		if (this.drawingImg != null){
			int height = this.drawingImg.getHeight();
			int width = this.drawingImg.getWidth();
			g2d.drawImage(this.drawingImg, -(width/2), -(height/2), null);
		}			
			
		// If the mouse is in the panel then paint it
		if (this.drawing.getMouseStroke()!=null){
			DrawStroke mouseStroke = this.drawing.getMouseStroke();
			g2d.setColor(mouseStroke.getColour());
			g2d.setStroke(new BasicStroke(mouseStroke.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g2d.draw(mouseStroke);
		}
	}
	
	/**
	 * Paints a stroke object onto the graphics context
	 * @param g2d Graphics context to draw this to
	 * @param ds DrawStroke object to draw
	 */
	private void paintStroke(DrawStroke ds){
		Graphics2D g2d = this.drawingImg.createGraphics();
		g2d.translate(getWidth()/2, getHeight()/2);
		g2d.setColor(ds.getColour());
		g2d.setStroke(new BasicStroke(ds.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		for (int i = 0; i < this.drawing.getSectorCount(); i++){
			g2d.draw(ds); // Draws the Path2D in every sector
			if (ds.isReflected()){ // Reflects the drawing context if point needs to be reflected
				g2d.scale(-1, 1);
				g2d.draw(ds);
				g2d.scale(-1, 1); // Reflects back for next stroke to be drawn correctly
			}
			g2d.rotate(2*Math.PI/this.drawing.getSectorCount());
		}
	}

	/**
	 * Paints the sector lines denoting the edge of sectors
	 * @param g2d Graphics object to do drawing
	 */
	private void paintSectorLines(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < this.drawing.getSectorCount(); i++){
			// Draws to the edge of whichever size component is larger
			if (getHeight() > getWidth()){
				g2d.drawLine(0, 0, 0, -(getHeight()/2));
			} else {
				g2d.drawLine(0, 0, 0, -(getWidth()/2));
			}
			g2d.rotate(2*Math.PI/this.drawing.getSectorCount());
		}
	}
	
	/// PANEL LISTENER ///

	/**
	 * Listener that detects when mouse clicked or dragged to control points in drawing
	 * @author dan
	 */
	class DoilyMouseListener extends MouseAdapter{
		int pointCount;
	
		/**
		 * If the mouse is dragged, add a point to the stroke. If the stroke is long enough, break it
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			drawing.newPoint(e.getX()-centX, e.getY()-centY, false);
			paintStroke(drawing.getStrokes().peek());
			if (pointCount++ >=DigitalDoily.MAX_STROKE_SIZE){
				drawing.newPoint(e.getX()-centX, e.getY()-centY, true);
				pointCount=1;
			}
			
			repaint();
		}

		/**
		 * When the mouse is pressed down, remove the cursor point from the screen, add a new entry to the drawing stack
		 * and add the first point to the stroke. Re-enables the undo button if previously disabled
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			drawing.clearMouseStroke();
			drawing.newPoint(e.getX()-centX,e.getY()-centY, true);
			paintStroke(drawing.getStrokes().peek());
			pointCount=1;
			repaint();
		}

		/**
		 * When the mouse is moved inside the panel, move the mouseStroke so the preview point follows the mouse
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			drawing.setMouseStroke(e.getX()-centX, e.getY()-centY);
			repaint();
		}

		/**
		 * When the mouse leaves the panel remove the preview point
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			drawing.clearMouseStroke();
			repaint();
		}

	}
}
