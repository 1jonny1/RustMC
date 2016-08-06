package me.savant.rustmc;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
	
	public static void sendServerMessage(String string)
	{
		Bukkit.broadcastMessage(ChatColor.GOLD + "[Server] " + string);
	}
	
	public static void sendStaffMessage(String string)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.isOp() || p.hasPermission("admin") || p.hasPermission("staff"))
			{
				p.sendMessage(ChatColor.GRAY + "[StaffMessage] " + string);
			}
		}
	}
	
	public static int range(int min, int max)
	{
		Random random = new Random();
		return random.nextInt((max - min) + 1) + min;
	}
	
	public static int range(int min, int max, long seed)
	{
		Random random = new Random();
		random.setSeed(seed);
		return random.nextInt((max - min) + 1) + min;
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
			}, 2L);
			return true;
		}
		else
		{
			return false;
		}
	}
}
