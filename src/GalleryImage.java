import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JToggleButton;

/**
 * Represents a single image that is used to save a state of the drawing into the gallery.
 * Is an extension of a JToggleButton as this allows ButtonGroups to be used to ensure only one image is selected at any one time
 * @author dmh2g16
 *
 */
public class GalleryImage extends JToggleButton{
	private BufferedImage img; // Buffered image containing actual drawing
	private boolean selected = false; // Whether the image is currently selected
	
	/**
	 * Constructs a new GalleryImage by drawing the current drawing on the panel to the specified width (to scale)
	 * @param panel Panel that contains the drawing to be saved
	 * @param width The width of the image
	 */
	public GalleryImage(DoilyPanel panel, int width){		
		// Creates new image that is direct copy of what is on the panel
		BufferedImage im = new BufferedImage(panel.getWidth(),panel.getHeight(),BufferedImage.TYPE_INT_ARGB);
		panel.paint(im.getGraphics());
		
		// Calculates aspect ratio and sets height to conform to this ratio
		double widthRatio = ((double)width/panel.getWidth());
		int height = (int) (widthRatio*panel.getHeight());
		
		// Set the size of itself to the new dimensions
		Dimension size = new Dimension(width,height);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		
		// Creates a new buffered image of new size and draws scaled image to it
		this.img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		this.img.createGraphics().drawImage(im.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		
		// Anonymous class controlling logic when image is selected (using button group)
		this.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				// Updates variable depending on whether image is now selected
				if (e.getStateChange() == ItemEvent.DESELECTED){
					selected = false;
				} else {
					selected = true;
				}
			}
			
		});
	}
	
	/**
	 * Controls drawing of the image onto the component
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// If it is selected create a blue border around the image (and clip the drawing of the image to inside this)
		if (selected == true){
			g.setColor(Color.RED);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setClip(5,5,this.getWidth()-10,this.getHeight()-10);
		}
		g.drawImage(img, 0, 0,null); // Paints the BufferedImage onto the component
	}

}
