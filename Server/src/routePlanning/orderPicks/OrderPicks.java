package routePlanning.orderPicks;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import Objects.WarehouseMap;
import Objects.Sendable.SingleTask;
import routePlanning.pathFinding.SimpleDistance;

/*
 * @author Maria
 * Orders a list of items in a job and returns vectors of integers with the moves required to take by the robot
 */


public class OrderPicks {

	
	
	public ArrayList<Objects.Sendable.SingleTask> items; //the list of items in the job
	public ArrayList<Objects.Sendable.SingleTask> orderedItems; //the ordered list of items
	public ArrayList<Point> dropOffs;
	public Point dropOff;
	private boolean running=true;
	private boolean canceled=false;
	private WarehouseMap map; 
	private SimpleDistance dist;
	
	public OrderPicks(List<Objects.Sendable.SingleTask> items,List<Point> dropOffs,WarehouseMap map)
	{
		this.map=map;
		this.dist=new SimpleDistance(map);
		this.dropOffs=(ArrayList<Point>) dropOffs;
		this.items=(ArrayList<SingleTask>) items;
		orderedItems=new ArrayList<Objects.Sendable.SingleTask>();
		planOrder();
	}
	
	public String toString()
	{
		String s="";
		for(int i=0;i<orderedItems.size();i++)
		{
			s=s+orderedItems.get(i).toString()+"  ";
			
		}
		
		return s;
	}
	
	/**
	 * cancels methods thop.orderedItems();at are trying to order the jobs
	 */
	public void cancel()
	{
		canceled=true;
	}
	
	public ArrayList<Objects.Sendable.SingleTask> getOrder()
	{
		if(running) return null;
		return orderedItems;
	}
	
	
	/**
	 * method that plans the order of the picks in the job using Nearest Insertion Heuristic
	 */	
	private void planOrder()
	{
		int miny=-1;
		Point dropOffForMiny=dropOff ;
		ArrayList<Objects.Sendable.SingleTask> orderedItemsForMiny=new ArrayList<Objects.Sendable.SingleTask>();
		int minTotalDistance=-1;
		for(int y=0;y<dropOffs.size();y++)
		{
			dropOff=(Point) dropOffs.get(y);
			//picking the first item (the one closest to the robot)
			int min=0,minI=0;
			for(int i=0;i<items.size();i++)
			{				
				Point loc=items.get(i).getLocation();
				int x=getDistance(dropOff,loc);
				if(min==0 || min>x)
				{
					if(canceled) return;
					min=x;
					minI=i;
				}
			}
			
			//System.out.println("minI == " + minI);
			//System.out.println("Size of items == " + items.size());
			orderedItems.add(items.get(minI));
			items.remove(minI);
			
			while(!items.isEmpty())
			{
				if(canceled) return;
				minI=findClosestPick();
				findInsertLocation(minI);
				items.remove(minI);
			}
			int x=getDistance();
			if(x<minTotalDistance || minTotalDistance==-1)
			{
				miny=y;
				minTotalDistance=x;
				orderedItemsForMiny.clear();
				dropOffForMiny=dropOff;
				orderedItemsForMiny.addAll(orderedItems);
				
				
			}
			
			// Changed to stop the NullPointer issuec
			items = (ArrayList<SingleTask>) orderedItems.clone();
			
			orderedItems.clear();
		}
		
		orderedItems.addAll(orderedItemsForMiny);
		dropOff=dropOffForMiny;
		
		/*
		//add the dropoff point as a singleTask object at the end of the job
		
		SingleTask drop=new SingleTask("dropOff", 0, dropOffForMiny);
		orderedItems.add(drop);
		//System.out.println(orderedItems);
		 
		 */
		running=false;
	}
	
	
	
	
	/**
	 * A method that inserts an items in the best possible location
	 * @param index the index of the item(from the unordered list) to be inserted
	 */	
	private void findInsertLocation(int index)
	{
		int minj=-1,minD=-1;
		for(int j=0;j<orderedItems.size();j++)
		{
			orderedItems.add(j, items.get(index));
			int dist=this.getDistance();
			if(minD==-1 || dist<minD)
			{
				if(canceled) return;
				minD=dist;
				minj=j;
			}
			orderedItems.remove(j);
		}
		orderedItems.add( items.get(index));
		int dist=this.getDistance();
		if(minD==-1 || dist<minD)
		{
			if(canceled) return;
			minD=dist;
			minj=orderedItems.size()-1;
		}
		orderedItems.remove(orderedItems.size()-1);	
		orderedItems.add(minj, items.get(index));		
	}
	
	
	
	/**
	 *  A method to find the best pick to choose next
	 * @return the index of the pick closest to any of the picks already selected
	 */	
	private int findClosestPick()
	{
		int min=-1,minI=-1;
		for(int i=0;i<items.size();i++)
		{
			for(int j=0;j<orderedItems.size();j++)
			{
				Point loci=items.get(i).getLocation();
				Point locj=orderedItems.get(j).getLocation();
				int x=getDistance(locj,loci);
				if(min==-1 || min>x)
				{
					if(canceled) return -1;
					min=x;
					minI=i;
				}
			}
		}
		return minI;
	}
	
	
	/**
	 * Method that calculates the total distance for the current route between all already selected picks
	 * @return the distance
	 */
	private int getDistance()
	{
		Point loc=orderedItems.get(0).getLocation();
		int sum=getDistance(dropOff,loc);;
		for(int i=1;i<orderedItems.size();i++)
		{
			if(canceled) return -1;
			loc=orderedItems.get(i-1).getLocation();
			Point loci=orderedItems.get(i).getLocation();
			sum+=getDistance(loc,loci);;
		}
		
		
		loc=orderedItems.get(orderedItems.size()-1).getLocation();
		sum+=getDistance(loc,dropOff);;
		return sum;
	}
	
	
	/**
	 * Method to be used by other classes that require the total distance for a job
	 * @return -1 if the order has not yet been completed so other classes can't get incorrect distance
	 * @return if the order has been computed, will return the total distance
	 */	
	public int getFinalDistance()
	{
		if(running)	return -1;
		else return getDistance();
	}
	
		
	public int getDistance(Point loc1, Point loc2)
	{
		Integer distance=dist.GetDistnace(loc1, loc2);
		if(distance==null) return 1000;
		return distance.intValue();
	}
	
	
	
}