package me.savant.building;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Health
{
	public static int getMaxHealth(Material mat)
	{
		if(mat == Material.WOOD)
			return 500;
		if(mat == Material.COBBLESTONE)
			return 700;
		if(mat == Material.BRICK)
			return 1000;
		if(mat == Material.IRON_BLOCK)
			return 1500;
		return 10;
	}
	
	public static final int EXPLOSIVE_DAMAGE = 300;
	
	public static void damage(Block b, int amount)
	{
		if(Tag.is(b, "health"))
		{
			int health = Integer.parseInt(Tag.getValue(b, "health"));
			health -= amount;
			if(health < 0)
				Tag.delete(b);
			else
				Tag.Tag(b, "health", health + "");
		}
	}
}
