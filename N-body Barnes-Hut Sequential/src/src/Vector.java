package src;

public class Vector {
	private double xVal;
	private double yVal;
	public Vector(double x , double y){
		this.xVal = x;
		this.yVal = y;
	}
	
	public boolean equalsVector(Vector vec){
		if(vec.xVal == this.xVal && vec.yVal == this.yVal){
			return true;
		}
		return false;
	}
	
	public double getxVal() {
		return xVal;
	}
	public void setxVal(double xVal) {
		this.xVal = xVal;
	}
	public double getyVal() {
		return yVal;
	}
	public void setyVal(double yVal) {
		this.yVal = yVal;
	}
	public String toString(){
		return "" + this.xVal + "," + this.yVal;
	}
}
