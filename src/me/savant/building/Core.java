package me.savant.building;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Core
{
	public static DataManager dm;
	
	public static final int DISTANCE = 50;
	
	public static Block getCore(Player p)
	{
		if(dm == null)
			System.out.println("DM NULL");
		else if(dm.getData() == null)
			System.out.println("DATA NULL");
		for(Block b : dm.getData())
		{
			if(Tag.is(b, "core"))
			{
				if(Tag.is(b, "owner") && Tag.getValue(b, "owner").equalsIgnoreCase(p.getName()))
				{
					return b;
				}
			}
		}
		return null;
	}
	
	public static boolean hasPlacedCore(Player p)
	{
		return getCore(p) != null;
	}
	
	public static boolean isInRange(Player p, Location loc)
	{
		Block b = getCore(p);
		if(b == null)
			return true;
		double dist = Math.abs(b.getLocation().distance(loc));
		return dist < DISTANCE;
	}
	
	public static boolean isInAnyRange(Player p, Location loc)
	{
		for(Block b : dm.getData())
		{
			if(Tag.is(b, "core"))
			{
				if(Tag.is(b, "owner") && !Tag.getValue(b, "owner").equalsIgnoreCase(p.getName()))
				{
					double dist = Math.abs(b.getLocation().distance(loc));
					if(dist < DISTANCE)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
