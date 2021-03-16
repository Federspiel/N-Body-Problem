package src;

import java.util.ArrayList;

public class CalculateForceTask implements Runnable{
	private ArrayList<Body> bodies = new ArrayList<Body>();
	private double far = 0;
	private QuadTree tree = null;
	private boolean terminate = false;
	private boolean finnished = false;
	private boolean calc = false;
	
	public CalculateForceTask(){
		
	}
	
	public synchronized void calculate(QuadTree tree, ArrayList<Body> bodies,int far) throws InterruptedException
	{
		this.tree = tree;
		this.bodies = bodies;
		this.far = far;
		calc = true;
		
		notifyAll();
	}
		
	public synchronized void terminate()
	{
		terminate = true;
		notifyAll();
	}
	public synchronized void isfinnished() throws InterruptedException
	{
		if (!finnished)
		{
			
			wait();
		}
		
		this.finnished = false;
	}
	
	public synchronized void setfinnished(){
		this.finnished = true;
	}
	public void run(){
			while (!terminate)
			{
				synchronized (this)
				{
					if (!calc)
					{				
						try {
							
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					calc = false;
				}

				if (!terminate)
				{
					
					tree.calculateForce(this.bodies, far);
				
					synchronized (this)
					{
						setfinnished();
						
						notifyAll();
					}
				}
			}
	}
}
