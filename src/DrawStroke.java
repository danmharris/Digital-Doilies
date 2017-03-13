import java.awt.Color;
import java.awt.geom.Path2D;

/**
 * Represents an individual stroke on the panel i.e. where the mouse is moved whilst pressed.
 * Extends Path2D to allow easy drawing into the image
 * @author Dan
 *
 */
public class DrawStroke extends Path2D.Double{
	private int diameter; // Diameter of the stroke
	private Color colour; // Colour of the stroke
	private boolean reflect; // Whether the stroke needs to be reflected in each sector
	
	/**
	 * Creates a new instance with the properties for that particular stroke. Initial point not required
	 * @param diameter Diameter of the stroke
	 * @param colour Colour of the stroke
	 * @param reflect Whether the stroke needs to be reflected in each sector
	 */
	public DrawStroke(int diameter, Color colour, boolean reflect){
		super();
		this.diameter = diameter;
		this.colour = colour;
		this.reflect = reflect;
	}
	
	/**
	 * Creates a new instance with the properties for that particular stroke. Initial point is required
	 * @param x Initial x co-ordinate
	 * @param y Initial y co-ordinate
	 * @param diameter Diameter of stroke
	 * @param colour Colour of stroke
	 */
	public DrawStroke(double x, double y,int diameter, Color colour){
		super();
		this.diameter = diameter;
		this.colour = colour;
		this.moveTo(x, y);
	}
	
	/**
	 * Retrieves the colour for the stroke
	 * @return Colour of stroke
	 */
	public Color getColour(){
		return this.colour;
	}
	
	/**
	 * Retrieves the diameter of the stroke
	 * @return Diameter of stroke
	 */
	public int getDiameter(){
		return this.diameter;
	}	
	
	/**
	 * Returns whether the points need reflecting
	 * @return Boolean value where true means they need reflecting
	 */
	public boolean isReflected(){
		return this.reflect;
	}
}
