package me.savant.rustmc;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class Util
{
	public static RustMC plugin;
	
	public static boolean isTool(Material m)
	{
		return
			m.equals(Material.WOOD_AXE) ||
			m.equals(Material.STONE_AXE) ||
			m.equals(Material.IRON_AXE) ||
			m.equals(Material.GOLD_AXE) ||
			m.equals(Material.DIAMOND_AXE) ||
			m.equals(Material.WOOD_PICKAXE) ||
			m.equals(Material.STONE_PICKAXE) ||
			m.equals(Material.IRON_PICKAXE) ||
			m.equals(Material.DIAMOND_PICKAXE);
	}
	
	static boolean canWork = true;
	public static boolean work()
	{
		if(canWork)
		{
			canWork = false;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					canWork = true;
				}
			}, 5L);
			return true;
		}
		else
		{
			return false;
		}
	}
}
