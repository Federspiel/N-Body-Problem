package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		int gnumBodies = Integer.parseInt(args[0]);
		int numSteps = Integer.parseInt(args[1]);
		int far = Integer.parseInt(args[2]);
		int DT = 1;
		int loadedBodies = 0;
		ArrayList<Body> space = new ArrayList<Body>(); 
		File file = new File("C:\\Users\\josef\\Desktop\\nbody120.txt");   
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String st; 
		while ((st = br.readLine()) != null && gnumBodies >= loadedBodies){ 
		    double px = Double.parseDouble(st);
		  	double py = Double.parseDouble(br.readLine());
		  	double vx = Double.parseDouble(br.readLine());
		  	double vy = Double.parseDouble(br.readLine());
		  	double m = Double.parseDouble(br.readLine());
		  	Body body = new Body(px,py,vx,vy,0,0,m);
		  	space.add(body);
		  	loadedBodies++;
		  }
		  br.close();
		Bounds bounds = calculateBounds(space);

		double stime = System.nanoTime();  
		for(int i = 0; i< numSteps; i++){  
		QuadTree tree = new QuadTree(bounds,space);
		space = tree.getBodies();
		tree.calculateForce(space,far);
		moveBodies(space,DT);
		bounds = calculateBounds(space);

		}
		double etime = System.nanoTime();
		System.out.println("Input parameters  gnumBodies = " + gnumBodies + " numSteps = " + numSteps + " far = " + far);
		System.out.println((etime-stime)/1000000000 + "seconds");
	}
	
	public static Bounds calculateBounds(ArrayList<Body> bodies){
		double max = 0;
		double min = 0;
		for(Body body: bodies){
			if(body.getPositionVector().getxVal() < min){
				min = body.getPositionVector().getxVal();
			}
			if(body.getPositionVector().getyVal() < min){
				min = body.getPositionVector().getyVal();
			}
			if(body.getPositionVector().getxVal() > max){
				max = body.getPositionVector().getxVal();
			}
			if(body.getPositionVector().getyVal() > max){
				max = body.getPositionVector().getyVal();
			}
		}
		Bounds bounds = new Bounds(min,min,max,max);
		return bounds;
	}
	
	public static void moveBodies(ArrayList<Body> bodies, double timeSteps){
		Vector deltav = new Vector(0,0);
		Vector deltap = new Vector(0,0);
		int i = 0;
		for(i = 0; i < bodies.size();i++){
			deltav.setxVal((bodies.get(i).getForceVector().getxVal()/bodies.get(i).getMass())*timeSteps); 
			deltav.setyVal((bodies.get(i).getForceVector().getyVal()/bodies.get(i).getMass())*timeSteps); 
			deltap.setxVal((bodies.get(i).getVelocityVector().getxVal()+deltav.getxVal()/2) *timeSteps);
			deltap.setyVal((bodies.get(i).getVelocityVector().getyVal()+deltav.getyVal()/2) *timeSteps);
			bodies.get(i).getVelocityVector().setxVal(bodies.get(i).getVelocityVector().getxVal() + deltav.getxVal()); 
			bodies.get(i).getVelocityVector().setyVal(bodies.get(i).getVelocityVector().getyVal() + deltav.getxVal()); 
			bodies.get(i).getPositionVector().setxVal(bodies.get(i).getPositionVector().getxVal() + deltap.getxVal()); 
			bodies.get(i).getPositionVector().setyVal(bodies.get(i).getPositionVector().getyVal() + deltap.getyVal()); 
			bodies.get(i).getForceVector().setxVal(0);
			bodies.get(i).getForceVector().setyVal(0);
		}
	}
	
}
