import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Contains the GUI components for the GalleryScrollPane, and methods to control the images
 * @author Dan
 *
 */
public class GalleryScrollPanel extends JScrollPane{
	private JPanel galleryPanel; // Holds all the gallery images
	private ButtonGroup imageGroup = new ButtonGroup(); // Button group for gallery image selection logic
	private ArrayList<GalleryImage> images = new ArrayList<GalleryImage>(); // Images stored in gallery
	
	/**
	 * Constructs a new scroll panel and add a gallery panel to it
	 */
	public GalleryScrollPanel(){
		this.galleryPanel = new JPanel();
		this.galleryPanel.setLayout(new BoxLayout(galleryPanel, BoxLayout.PAGE_AXIS));
		this.setViewportView(this.galleryPanel);
		
		this.setPreferredSize(new Dimension(220, 700));
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	/**
	 * Returns whether the gallery is currently full
	 * @return False if gallery size is greater than MAX_GALLERY
	 */
	public boolean isFull(){
		return this.images.size() >= DigitalDoily.MAX_GALLERY;
	}
	
	/**
	 * Saves a new image to the gallery by creating button and adding to group
	 * @param dp DoilyPanel to be saved
	 */
	public void saveToGallery(DoilyPanel dp){
		GalleryImage image = new GalleryImage(dp, this.getWidth()-20);
		galleryPanel.add(image);
		images.add(image);
		imageGroup.add(image);
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Removes any selected image from the gallery
	 */
	public void removeSelectedFromGallery(){
		Iterator<GalleryImage> it = images.iterator();
		while (it.hasNext()){
			GalleryImage gi = it.next();
			if (gi.isSelected()){
				it.remove();
				imageGroup.remove(gi);
				galleryPanel.remove(gi);
				this.revalidate();
				this.repaint();
				break;
			}
		}
	}
}
