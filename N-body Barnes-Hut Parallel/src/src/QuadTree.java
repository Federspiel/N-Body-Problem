package src;

import java.util.ArrayList;

public class QuadTree {
	
	private class Node
	{
		Bounds bounds = null;
		Body body = null;
		
		double totMass = 0.0;
		Vector centerOfGravity = new Vector(0.0, 0.0);
		
		Node[] nodes =  null;
		
		public Node(Bounds bounds)
		{
			this.bounds = bounds;
		}
		public String toString(){
			String temp = "";
			temp = "Node: " + this.bounds.toString() + " TotMass = " + totMass + " Center of gravity = " + this.centerOfGravity.toString() +"\n";
			if(body != null){
				temp = temp + this.body.toString(); 
			}
			return temp;
		}
	}

	Node root = null;
	
	public QuadTree(Bounds bounds, ArrayList<Body> bodies)
	{
		root = new Node(bounds);
		for (Body body : bodies)
		{	
			insert(body);
		}
	}

	public ArrayList<Body> getBodies()
	{
		 ArrayList<Body> bodies = new ArrayList<Body>();
		 
		 getBodies(root, bodies);
		 
		 return bodies;
	}
	
	private void getBodies(Node node, ArrayList<Body> bodies)
	{
		if (node.body != null)
		{
			bodies.add(node.body);
			return;
		}
		if(node.nodes != null){
			for (Node n : node.nodes)
			{
				getBodies(n, bodies);	
			}
		}
		
	}
	 
	private void insert(Body body)
	{
		if (!insert(root, body)){
			System.out.println("Bug");
			System.out.println(body.toString());
		}
	}
	
	private boolean insert(Node node, Body body)
	{
		if (!node.bounds.withinBounds(body.getPositionVector()))
		{
			return false;
		}
		if ((node.nodes == null) && (node.body != null)){
			if(node.body.calculateDistance(body.getPositionVector()) < 1000){
				node.body.merge(body);
				return true;
			}
		}
		
		if ((node.nodes  == null) && (node.body == null))
		{
			node.totMass = body.mass;
			node.centerOfGravity.setxVal(body.getPositionVector().getxVal());
			node.centerOfGravity.setyVal(body.getPositionVector().getyVal());
			node.body = body;
			return true;
		}

		if (node.nodes != null)
		{
			// node.body is null
			
			for (Node n : node.nodes)
			{
				if (insert(n, body))
				{
					updateStat(node,body);
					return true;
				}
			}
			
			return false;
		}

		// node.body is not null
		
		node.nodes = new Node[4];
		Bounds NW = new Bounds(node.bounds.getxMin(),node.bounds.getCenter().getyVal(),node.bounds.getCenter().getxVal(),node.bounds.getyMax());
		Bounds SW = new Bounds(node.bounds.getxMin(),node.bounds.getyMin(),node.bounds.getCenter().getxVal(),node.bounds.getCenter().getyVal());
		Bounds SE = new Bounds(node.bounds.getCenter().getxVal(),node.bounds.getyMin(),node.bounds.getxMax(),node.bounds.getCenter().getyVal());
		Bounds NE = new Bounds(node.bounds.getCenter().getxVal(),node.bounds.getCenter().getyVal(),node.bounds.getxMax(),node.bounds.getyMax());
		node.nodes[0] = new Node(NW);
		node.nodes[1] = new Node(SW);
		node.nodes[2] = new Node(SE);
		node.nodes[3] = new Node(NE);

		for (Node n : node.nodes)
		{
			if (insert(n, node.body))
			{
				node.body = null;
				break;
			}
		}

		if (node.body != null)
		{
			return false;
		}
		
		for (Node n : node.nodes)
		{
			if (insert(n, body))
			{
				updateStat(node,body);
				return true;
			}
		}
		
		return false;
	}
	
	private void updateStat(Node n, Body body)
	{
		double totMass = n.totMass + body.mass;
		n.centerOfGravity.setxVal((n.centerOfGravity.getxVal() * n.totMass + body.getPositionVector().getxVal() * body.mass) / totMass);
		n.centerOfGravity.setyVal((n.centerOfGravity.getyVal() * n.totMass + body.getPositionVector().getyVal() * body.mass) / totMass);
		n.totMass = totMass;
	
	}
	public void calculateForce(ArrayList<Body> bodies,double far){
		for(Body body : bodies){
			calculateForce(body,far);
		}
	}
	
	private void calculateForce(Body body,double far){
		calculateForce(this.root,body,far);
	}
	
	private void calculateForce(Node node , Body body, double far){
		if(node.nodes == null){
			if(node.body == null){
				return;
			}
			if(node.body.equalsBody(body)){
				return;
			}
			body.updateForces(node.centerOfGravity,node.totMass);
			return;
		}
		if(body.calculateDistance(node.centerOfGravity) > far){
				body.updateForces(node.centerOfGravity,node.totMass);
				return;
		}
		for(Node n : node.nodes){
			calculateForce(n,body,far);
		}
		
	}
	public void run(){
		
	}
	
	public String toString(){
		String temp = "";
		temp = this.root.toString();
		if (root.nodes != null){
		for(Node n : this.root.nodes){
				temp = temp + toString(n);
		}
		}
		return temp;
	}
	public String toString(Node node){
		String temp = "";
		temp = node.toString();
		if(node.nodes != null){
			for(Node n : node.nodes){
				temp = temp + toString(n);
			}	
		}
		return temp;
	}
}
