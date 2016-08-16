package me.savant.building;

import me.savant.rustmc.Util;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BuildingInteraction implements Listener
{
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if(Util.work())
		{
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && Tag.is(e.getClickedBlock(), "health"))
			{
				ItemStack item = e.getPlayer().getItemInHand();
				if(item == null ||
				   (item.getType() != Material.WOOD &&
				   item.getType() != Material.COBBLESTONE &&
				   item.getType() != Material.BRICK &&
				   item.getType() != Material.IRON_BLOCK &&
				   item.getType() != Material.BANNER))
				{
					e.getPlayer().sendMessage(Util.red + "(" + Tag.getValue(e.getClickedBlock(), "health") + " / " + Health.getMaxHealth(e.getClickedBlock().getType()) + ")");
				}
			}
		}
	}
}
