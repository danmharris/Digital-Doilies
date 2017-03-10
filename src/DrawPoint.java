import java.awt.Color;

/**
 * @deprecated
 * NOTE: THIS CLASS IS NO LONGER BEING USED - USE DRAWSTROKE INSTEAD
 * 
 * Represents an individual point which is displayed on the screen. Stores properties such as its relative position,
 * colour, diameter and the previous point (if in line)
 * @author dan
 *
 */
public class DrawPoint {
	private int x; // X co-ordinate relative to centre
	private int y; // Y co-ordinate relative to centre
	private Color colour; // Colour of the point
	private int diameter; // Diameter of the point
	private DrawPoint previous; // Reference to previous point, if they need to be connected (drawing line/curve)
	
	/**
	 * Initialises point with parameters set to 0 and colour set to black 
	 */
	public DrawPoint(){
		this.x = 0;
		this.y = 0;
		this.colour = Color.BLACK;
		this.diameter = 0;
	}
	
	/**
	 * Constructs a new draw point with specified parameters
	 * @param x Relative x co-ordinate to add point (centre of point)
	 * @param y Relative y co-ordinate to add point (centre of point)
	 * @param diameter Diameter the point should be drawn at
	 * @param colour Colour the point should be drawn in
	 */
	public DrawPoint(int x, int y, int diameter, Color colour){
		this.diameter = diameter;
		this.colour = colour;
		this.x = x-(diameter/2); // Translates the stored point as Graphics draws from top left not centre
		this.y = y-(diameter/2);
		this.previous = null; // Initial previous point is nothing
	}
	
	/**
	 * Returns the top left x co-ordinate of the point relative to centre
	 * @return X co-ordinate relative to centre
	 */
	public int getX(){
		return this.x;
	}
	
	/**
	 * Returns the top left y co-ordinate of the point relative to centre
	 * @return Y co-ordinate relative to centre
	 */
	public int getY(){
		return this.y;
	}
	
	/**
	 * Returns diameter of point
	 * @return Diameter of point
	 */
	public int getDiameter(){
		return this.diameter;
	}
	
	/**
	 * Returns colour of the point
	 * @return Colour of point
	 */
	public Color getColour(){
		return this.colour;
	}
	
	/**
	 * Sets the previous point if they are to be joint in a line/curve (allows for smooth drawing)
	 * @param previous Reference to previous DrawPoint object
	 */
	public void setPrevious(DrawPoint previous){
		this.previous = previous;
	}
	
	/**
	 * Returns the previous point attached to this point
	 * @return Previous point object reference
	 */
	public DrawPoint getPrevious(){
		return this.previous;
	}
}
