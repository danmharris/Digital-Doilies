import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Contains the GUI components for the GalleryScrollPane, and methods to control the images
 * @author Dan
 *
 */
public class GalleryScrollPanel extends JScrollPane{
	private JPanel galleryPanel; // Holds all the gallery images
	private ArrayList<GalleryImage> images = new ArrayList<GalleryImage>(); // Images stored in gallery
	private JButton saveBtn;
	/**
	 * Constructs a new scroll panel and add a gallery panel to it
	 */
	public GalleryScrollPanel(){
		this.galleryPanel = new JPanel();
		this.galleryPanel.setLayout(new BoxLayout(galleryPanel, BoxLayout.PAGE_AXIS));
		this.setViewportView(this.galleryPanel);
		
		this.setPreferredSize(new Dimension(220, 700));
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.getVerticalScrollBar().setUnitIncrement(10);
	}
	
	public void setSaveBtn(JButton saveBtn){
		this.saveBtn = saveBtn;
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
				galleryPanel.remove(gi);
				this.revalidate();
				this.repaint();
			}
		}
		
		if (this.saveBtn!=null && !isFull()){
			this.saveBtn.setEnabled(true);
		}
	}
}
