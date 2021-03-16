package src;

public class Bounds {
	private double xMin;	
	private double xMax;
	private double yMin;
	private double yMax;
	
	public Bounds(double xMin, double yMin, double xMax, double yMax) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	public boolean withinBounds(int x , int y) {
		return (x >= this.getxMin() && x <= this.getxMax()
				&& y >= this.getyMin() && y <= this.getyMax());
	}
	
	public boolean withinBounds(Vector position) {
		return (position.getxVal() >= this.getxMin() && position.getxVal() <= this.getxMax()
				&& position.getyVal() >= this.getyMin() && position.getyVal() <= this.getyMax());
	}
	
	public Vector getCenter(){
		return new Vector(((this.xMax + this.xMin)/2),((this.yMax + this.yMin)/2));
	}
	public double getxMin() {
		return xMin;
	}

	public double getyMin() {
		return yMin;
	}

	public double getxMax() {
		return xMax;
	}

	public double getyMax() {
		return yMax;
	}
	public String toString(){
		return "Quadrant: " + this.xMin + ", " + this.yMin + ", " + this.xMax + ", " + this.yMax;
	}
}
