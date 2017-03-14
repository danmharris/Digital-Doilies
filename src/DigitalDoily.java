import java.awt.Color;

import javax.swing.SwingUtilities;

/**
 * Main entry point for application, simply creates an initialises a new frame
 * @author dmh2g16
 *
 */
public class DigitalDoily {
	// Starting constants for initial drawing properties
	public static final int START_SECTOR_COUNT = 12;
	public static final int START_DIAMETER = 5;
	public static final boolean START_REFLECT_DRAWING = false;
	public static final boolean START_SHOW_SECTOR_LINES = true;
	public static final Color START_COLOUR = Color.WHITE;
	public static final int MIN_DIAMETER = 2;
	public static final int MAX_DIAMETER = 20;
	public static final int MIN_SECTORS = 2;
	public static final int MAX_SECTORS = 36;
	public static final int MAX_GALLERY = 12;
	public static final int MAX_STROKE_SIZE = 200;
	
	/**
	 * Main Entry Point. Initialises GUI
	 * @param args
	 */
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				DoilyFrame df = new DoilyFrame();
				df.init();
			}
			
		});
		
	}
}
