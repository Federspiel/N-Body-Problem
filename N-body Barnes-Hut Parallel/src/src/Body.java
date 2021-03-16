package src;

public class Body {
	private Vector positionVector;
	private Vector velocityVector;
	private Vector forceVector;
	double G = 1;
	double mass;
	public Body(double px, double py,double vx ,double vy, double fx, double fy, double mass ){
		this.positionVector = new Vector(px,py);
		this.velocityVector = new Vector(vx,vy);
		this.forceVector = new Vector(fx,fy);
		this.mass = mass;
	}
	
	
	public void merge(Body body){
		double totMass = this.mass + body.mass;
		this.velocityVector.setxVal((this.getVelocityVector().getxVal() * this.mass + body.getVelocityVector().getxVal() * body.mass) / totMass);
		this.velocityVector.setyVal((this.getVelocityVector().getxVal() * this.mass + body.getVelocityVector().getxVal() * body.mass) / totMass);
		this.mass = totMass;
	}
	
	public boolean equalsBody(Body body){
		if(this.positionVector.equalsVector(body.positionVector)&& this.velocityVector.equalsVector(body.velocityVector)&& this.mass == body.mass){
			return true;
		}
		return false;
	}
	public double calculateDistance(Vector vec){
		return Math.sqrt(Math.pow((this.positionVector.getxVal() - vec.getxVal()),2) + Math.pow((this.positionVector.getyVal() - vec.getyVal()),2));
	}

	
	public void updateForces(Vector vec, double mass){
		double distance = 0;
		double magnitude = 0;
		Vector direction = new Vector(0,0);
		distance = calculateDistance(vec);
		magnitude = (G*this.mass*mass)/Math.pow(distance,2);
		direction.setxVal(this.positionVector.getxVal()- vec.getxVal());
		direction.setyVal(this.positionVector.getyVal()- vec.getyVal());
		this.forceVector.setxVal((this.forceVector.getxVal()+magnitude*direction.getxVal()/distance));
		this.forceVector.setyVal((this.forceVector.getyVal()+magnitude*direction.getyVal()/distance));
		
	}
	
	public void moveBody(int DT){
		Vector deltav = new Vector(0,0);
		Vector deltap = new Vector(0,0);
		deltav.setxVal((this.forceVector.getxVal()/this.mass)*DT);
		deltav.setyVal((this.forceVector.getyVal()/this.mass)*DT);
		deltap.setxVal((this.velocityVector.getxVal() + deltav.getxVal()/2)*DT);
		deltap.setyVal((this.velocityVector.getyVal() + deltav.getyVal()/2)*DT);
		this.velocityVector.setxVal(this.velocityVector.getxVal() + deltav.getxVal()); 
		this.velocityVector.setyVal(this.velocityVector.getyVal() + deltav.getyVal());
		this.positionVector.setxVal(this.positionVector.getxVal() + deltap.getxVal()); 
		this.positionVector.setyVal(this.positionVector.getyVal() + deltap.getyVal());
		this.forceVector.setxVal(0);
		this.forceVector.setyVal(0);
	}
	
	public Vector getPositionVector() {
		return positionVector;
	}
	public void setPositionVector(Vector positionVector) {
		this.positionVector = positionVector;
	}
	public Vector getVelocityVector() {
		return velocityVector;
	}
	public void setVelocityVector(Vector velocityVector) {
		this.velocityVector = velocityVector;
	}
	public Vector getForceVector() {
		return forceVector;
	}
	public void setForceVector(Vector forceVector) {
		this.forceVector = forceVector;
	}
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	public String forceToString(){
		return "Body: force = " + this.forceVector.toString();
	}
	public String toString(){
		return "Body: Position = " + this.positionVector.toString() + " has mass = "+ this.mass +"\n";
	}
}

