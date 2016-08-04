package me.savant.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class HempListener implements Listener
{
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Block clicked = e.getClickedBlock();
		if(clicked != null && clicked.getTypeId() == 31 && clicked.getData() == (byte) 2)
		{
			clicked.setType(Material.AIR);
			Player p = e.getPlayer();
			ItemIndex.giveItem(ItemType.CLOTH, 5, p);
		}
	}
}
