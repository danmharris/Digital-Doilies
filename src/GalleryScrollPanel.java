import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GalleryScrollPanel extends JScrollPane{
	private JPanel galleryPanel; // Holds all the gallery images
	private ButtonGroup imageGroup = new ButtonGroup(); // Button group for gallery image selection logic
	private ArrayList<GalleryImage> images = new ArrayList<GalleryImage>(); // Images stored in gallery
	
	public GalleryScrollPanel(){
		this.galleryPanel = new JPanel();
		this.galleryPanel.setLayout(new BoxLayout(galleryPanel, BoxLayout.PAGE_AXIS));
		this.setViewportView(this.galleryPanel);
		
		this.setPreferredSize(new Dimension(220, 700));
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	public boolean isFull(){
		return this.images.size() >= DigitalDoily.MAX_GALLERY;
	}
	
	public void saveToGallery(DoilyPanel dp){
		GalleryImage image = new GalleryImage(dp, this.getWidth()-20);
		galleryPanel.add(image);
		images.add(image);
		imageGroup.add(image);
		this.revalidate();
		this.repaint();
	}
	
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
