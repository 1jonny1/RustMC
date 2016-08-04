package me.savant.items;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener
{
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if(e.getItem() != null)
		{
			Player p = e.getPlayer();
			if(e.getItem().getType() == Material.WOOL)
			{
				if(p.getHealth() < 18)
				{
					p.setHealth(p.getHealth() + 2);
					p.playSound(p.getLocation(), Sound.DRINK, 1, 15);
					ItemIndex.removeItem(Material.WOOL, 1, p.getInventory());
				}
			}
		}
	}
}
