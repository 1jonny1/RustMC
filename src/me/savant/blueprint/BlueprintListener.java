package me.savant.blueprint;

import me.savant.items.ItemIndex;
import me.savant.rustmc.RustMC;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class BlueprintListener implements Listener
{
	RustMC plugin;
	public BlueprintListener(RustMC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(PlayerInteractEvent e) 
	{
		if(e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.BOOKSHELF)
		{
			e.setCancelled(true);
			Player p = e.getPlayer();
			Loot loot = new Loot(e.getClickedBlock(), plugin);
			loot.randomizeLoot();
			loot.open(p);
			p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 15);
		}
		if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasLore())
		{
			ItemStack item = e.getItem();
			Player p = e.getPlayer();
			if(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).contains("Blueprint"))
			{
				e.setCancelled(true);
				if(Blueprint.hasBlueprint(p, item.getType()))
				{
					p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 15);
					ActionBarAPI.sendActionBar(p, ChatColor.RED + "" + ChatColor.ITALIC + "Blueprint already learned!", 40);
				}
				else
				{
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 15);
					ActionBarAPI.sendActionBar(p, ChatColor.GREEN + "Blueprint learned!", 40);
					Blueprint.addBlueprint(p, item.getType());
					ItemIndex.removeItem(item.getType(), 1, p.getInventory());
				}
			}
		}
	}
}
