import java.awt.Color;
import java.awt.geom.Path2D;

public class DrawStroke extends Path2D.Double{
	private int diameter;
	private Color colour;
	private boolean reflect;
	
	public DrawStroke(int diameter, Color colour, boolean reflect){
		super();
		this.diameter = diameter;
		this.colour = colour;
		this.reflect = reflect;
	}
	
	public DrawStroke(double x, double y,int diameter, Color colour){
		super();
		this.diameter = diameter;
		this.colour = colour;
		this.addPoint(x, y);
	}
	
	public Color getColour(){
		return this.colour;
	}
	
	public int getDiameter(){
		return this.diameter;
	}	
	
	public boolean isReflected(){
		return this.reflect;
	}
		
	public void addPoint(double x, double y){
		if (getCurrentPoint() == null){
			this.moveTo(x, y);
		} else {
			this.lineTo(x, y);
		}
	}
	
}
