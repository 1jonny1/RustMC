package me.savant.items;

import me.savant.rustmc.RustMC;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener
{
	
	RustMC plugin;
	public ItemListener(RustMC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(final PlayerInteractEvent e)
	{
		if(e.getItem() != null)
		{
			Player p = e.getPlayer();
			if(e.getItem().getType() == Material.WOOL)
			{
				if(p.getHealth() < 18)
				{
					p.setHealth(p.getHealth() + 2);
					p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 15);
					ItemIndex.removeItem(Material.WOOL, 1, p.getInventory());
				}
			}
			else if(e.getItem().getType() == Material.TNT)
			{
				e.setCancelled(true);
				if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasLore())
				{
					ItemStack item = e.getItem();
					if(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).contains("Blueprint"))
					{
						return;
					}
				}
				TNTPrimed tnt = (TNTPrimed) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.PRIMED_TNT);
				tnt.setVelocity(p.getLocation().getDirection().normalize().multiply(1));
				Item.C4.removeItem(p, 1);
			}
		}
	}
}
