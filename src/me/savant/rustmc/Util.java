package me.savant.rustmc;

import org.bukkit.Material;

public class Util
{
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
}
