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
 * JPanel that provides the main drawing area and an interface to change properties like pen size etc.
 * @author dmh2g16
 *
 */
public class DoilyPanel extends JPanel{
	private DoilyDrawing dd;
	private int centX, centY; // Centre position of the panel
	private boolean doCompleteRedraw = false;
	private BufferedImage currentDrawing;
	/**
	 * Sets up the panel for drawing with default parameters set in main class
	 * @param undoBtn
	 */
	public DoilyPanel(){		
		this.setBackground(Color.BLACK);
		this.dd = new DoilyDrawing(this);

		// Attaches listeners that control drawing
		DoilyMouseListener listener = new DoilyMouseListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				doCompleteRedraw = true;
			}
		});
	}
	
	public DoilyDrawing getDoilyDrawing(){
		return this.dd;
	}

	public void doCompleteRedraw(){
		this.doCompleteRedraw = true;
		updateImage();
		repaint();
	}
	
	/// GRAPHICS FUNCTIONS ///

	/**
	 * Main drawing function. Draws sector lines if option enabled and a point under the mouse cursor (for preview).
	 * Then draws all the points in the array, connecting lines if necessary to produce smooth lines
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		this.centX = (this.getWidth()/2);
		this.centY = (this.getHeight()/2);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(centX, centY); // Translates so 0,0 is in centre (allows relative drawing)
		if (this.dd.drawSectorLines()){
			this.paintSectorLines(g2d);
		}
				
		if(!this.doCompleteRedraw){
			if (this.currentDrawing != null){
				int height = this.currentDrawing.getHeight();
				int width = this.currentDrawing.getWidth();
				g2d.drawImage(this.currentDrawing, -(width/2), -(height/2), width, height, null);
			}

			if (!this.dd.getStrokes().isEmpty()){
				this.paintStroke(g2d, this.dd.getStrokes().peek());
			}
			
		} else {
			// Iterates through the stack to draw all the strokes
			Iterator<DrawStroke> it = this.dd.getStrokes().iterator();
			while (it.hasNext()){			
				this.paintStroke(g2d, it.next());
			}
			this.doCompleteRedraw = false;
		}
			
		// If the mouse is in the panel then paint it
		if (this.dd.getMouseStroke()!=null){
			DrawStroke mouseStroke = this.dd.getMouseStroke();
			g2d.setColor(mouseStroke.getColour());
			g2d.setStroke(new BasicStroke(mouseStroke.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g2d.draw(mouseStroke);
		}
	}
	
	private void paintStroke(Graphics2D g2d, DrawStroke ds){
		g2d.setColor(ds.getColour());
		g2d.setStroke(new BasicStroke(ds.getDiameter(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		for (int i = 0; i < this.dd.getSectorCount(); i++){
			g2d.draw(ds); // Draws the Path2D in every sector
			if (ds.isReflected()){ // Reflects the drawing context if point needs to be reflected
				g2d.scale(-1, 1);
				g2d.draw(ds);
				g2d.scale(-1, 1); // Reflects back for next stroke to be drawn correctly
			}
			g2d.rotate(2*Math.PI/this.dd.getSectorCount());
		}
	}

	/**
	 * Paints the sector lines denoting the edge of sectors
	 * @param g2d Graphics object to do drawing
	 */
	private void paintSectorLines(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < this.dd.getSectorCount(); i++){
			if (getHeight() > getWidth()){
				g2d.drawLine(0, 0, 0, -(getHeight()/2));
			} else {
				g2d.drawLine(0, 0, 0, -(getWidth()/2));
			}
			g2d.rotate(2*Math.PI/this.dd.getSectorCount());
		}
	}
	
	private void updateImage(){
		BufferedImage newDrawing = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = newDrawing.createGraphics();
		paintComponent(g2d);
		currentDrawing = newDrawing;
	}

	/// PANEL LISTENER ///

	/**
	 * Listener that detects when mouse clicked or dragged to control points in drawing
	 * @author dan
	 */
	class DoilyMouseListener extends MouseAdapter{
		DrawStroke currentStroke = null;
		int pointCount;
	
		/**
		 * If the mouse is dragged, add a point to the stroke
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			dd.newPoint(e.getX()-centX, e.getY()-centY, false);
			if (pointCount++ >=200){
				updateImage();
				dd.newPoint(e.getX()-centX, e.getY()-centY, true);
				pointCount=1;
			}
			
			repaint();
		}

		/**
		 * When the mouse is pressed down, remove the preview point from the screen, add a new entry to the drawing stack
		 * and add the first point to the stroke. Re-enables the undo button if previously disabled
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			dd.clearMouseStroke();
			updateImage();
			dd.newPoint(e.getX()-centX,e.getY()-centY, true);
			pointCount=1;
			repaint();
		}

		/**
		 * When the mouse is moved inside the panel, move the mouseStroke so the preview point follows the mouse
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			dd.setMouseStroke(e.getX()-centX, e.getY()-centY);
			repaint();
		}

		/**
		 * When the mouse leaves the panel remove the preview point
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			dd.clearMouseStroke();
			repaint();
		}

	}
}
