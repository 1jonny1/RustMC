package me.savant.items;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
		if(clicked != null && clicked.getTypeId() == 175)
		{
			if(clicked.getRelative(BlockFace.DOWN).getType() != Material.GRASS)
				clicked.getRelative(BlockFace.DOWN).setType(Material.AIR);
			else
				clicked.setType(Material.AIR);
			Player p = e.getPlayer();
			ItemIndex.giveItem(ItemType.CLOTH, 5, p);
		}
	}
}
