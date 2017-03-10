import java.awt.Color;

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
	
	/**
	 * Main Entry Point. Initialises GUI
	 * @param args
	 */
	public static void main(String[] args){
		DoilyFrame df = new DoilyFrame();
		df.init();
	}
}
