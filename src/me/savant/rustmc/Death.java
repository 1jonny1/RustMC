package me.savant.rustmc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Death implements Listener
{
	RustMC plugin;
	public Death(RustMC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Block block = e.getEntity().getLocation().getBlock();
		block.setType(Material.CHEST);
		
		List<ItemStack> sorted = new ArrayList<ItemStack>();
		for(ItemStack drop : e.getDrops())
		{
			if(drop != null && drop.getType() != Material.WORKBENCH)
			{
				sorted.add(drop);
			}
		}
		block.setMetadata("body", new FixedMetadataValue(plugin, sorted.toArray(new ItemStack[sorted.size()])));
		e.getDrops().clear();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Block clicked = e.getClickedBlock();
		if(clicked != null && clicked.getType() == Material.CHEST)
		{
			Player p = e.getPlayer();
			if(clicked.hasMetadata("body"))
			{
				e.setCancelled(true);
				if(clicked.getMetadata("body").get(0).value() instanceof ItemStack[])
				{
					ItemStack[] list = (ItemStack[]) clicked.getMetadata("body").get(0).value();
					
					Inventory inventory = Bukkit.createInventory(e.getPlayer(), 45, ChatColor.BOLD + "Body Loot");
					inventory.setContents(list);
					p.openInventory(inventory);
					p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 15);
				}
			}
			else
			{
				p.sendMessage("False!");
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		if(ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Body Loot"))
		{
			Player p = (Player) e.getPlayer();
			Block block = p.getTargetBlock((Set<Material>) null, 10);
			boolean doContinue = false;
			for(ItemStack item : e.getInventory().getContents())
			{
				if(item != null && item.getAmount() != 0)
				{
					doContinue = true;
				}
			}
			if(!doContinue)
			{
				block.setType(Material.AIR);
				block.getLocation().add(0, -1, 0).getBlock().setType(Material.GRASS);
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 15);
				System.out.println("[RustMC] Body has no content, removing...");
			}
			else
			{
				p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 15);
				System.out.println("[RustMC] Saved Body contents to block!");
				block.setMetadata("body", new FixedMetadataValue(plugin, e.getInventory().getContents()));
			}
		}
	}
	
	
}
