package me.savant.crafting;

import java.util.ArrayList;
import java.util.List;

import me.savant.crafting.CraftingMenu.OptionClickEvent;
import me.savant.crafting.CraftingMenu.OptionClickEventHandler;
import me.savant.items.Item;
import me.savant.items.ItemIndex;
import me.savant.items.ItemType;
import me.savant.rustmc.RustMC;
import me.savant.rustmc.Util;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class CraftingListener implements Listener
{
	RustMC plugin;
	public CraftingListener(RustMC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	void onInventoryClick(InventoryClickEvent e)
	{
		if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.WORKBENCH)
		{
			e.setCancelled(true);
			openCrafting((Player)e.getWhoClicked());
		}	
		if(e.getCursor() != null && e.getCursor().getType() == Material.WORKBENCH)
		{
			e.setCancelled(true);
			openCrafting((Player)e.getWhoClicked());
		}
	}
	
	@EventHandler
	void onDrop(PlayerDropItemEvent e)
	{
		if(e.getItemDrop() != null && e.getItemDrop().getItemStack().getType() == Material.WORKBENCH)
		{
			e.setCancelled(true);
			openCrafting(e.getPlayer());
		}
	}
	
	@EventHandler
	void onPlace(BlockPlaceEvent e)
	{
		if(e.getBlock().getType() == Material.WORKBENCH)
		{
			e.setCancelled(true);
			openCrafting(e.getPlayer());
		}
	}
	
	@EventHandler
	void onClick(PlayerInteractEvent e)
	{
		if(e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() == Material.WORKBENCH)
		{
			e.setCancelled(true);
			openCrafting(e.getPlayer());
		}
	}
	
	@EventHandler
	void onJoin(PlayerJoinEvent e)
	{
		ItemStack item = new ItemStack(Material.WORKBENCH, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Crafting");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + "Click to open the Crafting Menu");
		meta.setLore(lore);
		item.setItemMeta(meta);
		e.getPlayer().getInventory().setItem(8, item);
	}
	
	private void openCrafting(Player p)
	{
		CraftingMenu menu = new CraftingMenu(ChatColor.GRAY + "" + ChatColor.BOLD + "Crafting", 54, new OptionClickEventHandler()
		{
			@Override
			public void onOptionClick(OptionClickEvent e)
			{
				if(Util.work())
				{
					if(e.getClicked() != null && e.getClicked().getType() != Material.AIR && e.getClicked().getType() != Material.GRASS)
					{
						e.setWillClose(false);
						e.setWillDestroy(false);
						Player p = e.getPlayer();
						List<String> lore = e.getClicked().getItemMeta().getLore();
						if(ChatColor.stripColor(lore.get(0)).equalsIgnoreCase("[LOCKED]"))
						{
							p.playSound(p.getLocation(), Sound.CLICK, 1, 15);
							return;
						}
						Item item = Item.getValue(e.getName());
						if(item.getCost().hasResources(p))
						{
							item.getCost().takeResources(p);
							p.getInventory().addItem(item.getItem());
							p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 15);
							ActionBarAPI.sendActionBar(p, ChatColor.GREEN + "Crafted " + item.getStrippedName(), 40);
						}
						else
						{
							String text = "";
							int x = 1;
							for(ResourceEntry entry : item.getCost().getEntrys())
							{
								if(entry.getResource().isItem())
								{
									Item costItem = (Item) entry.getResource().getResource();
									int amount = ItemIndex.getAmount(costItem.getItem().getType(), p.getInventory());
									if(item.getCost().getEntrys().length == 1)
										text = text + amount + " " + costItem.getStrippedName() + ", Needed " + entry.getAmount() + " " + entry.getUnit();
									else if(item.getCost().getEntrys().length != x)
										text = text + amount + " " + costItem.getStrippedName() + ", Needed " + entry.getAmount() + " " + entry.getUnit() + " | ";
									else
										text = text + amount + " " + costItem.getStrippedName() + ", Needed " + entry.getAmount() + " " + entry.getUnit();
								}
								else
								{
									ItemType costItem = (ItemType) entry.getResource().getResource();
									int amount = ItemIndex.getAmount(ItemIndex.getType(costItem), p.getInventory());
									if(item.getCost().getEntrys().length == 1)
										text = text + amount + " " + ChatColor.stripColor(ItemIndex.getItem(costItem, 1).getItemMeta().getDisplayName()) + ", Needed " + entry.getAmount() + " " + entry.getUnit();
									else if(item.getCost().getEntrys().length != x)
										text = text + amount + " " + ChatColor.stripColor(ItemIndex.getItem(costItem, 1).getItemMeta().getDisplayName()) + ", Needed " + entry.getAmount() + " " + entry.getUnit() + " | ";
									else
										text = text + amount + " " + ChatColor.stripColor(ItemIndex.getItem(costItem, 1).getItemMeta().getDisplayName()) + ", Needed " + entry.getAmount() + " " + entry.getUnit();
								}
								x++;
							}
							text = ChatColor.RED + "" + ChatColor.ITALIC + "" + text;
							ActionBarAPI.sendActionBar(p, text, 480);
							p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 15);
							return;
						}
					}
				}
			}
			
		}, plugin)
		.setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Need Help?", new String[] { ChatColor.BLUE + "All craftable items are in this window.", ChatColor.BLUE + "Click on a item to start crafting it.", ChatColor.BLUE + "Most items require materials to craft,", ChatColor.BLUE + "while others are locked by default until", ChatColor.BLUE + "you find and unlock the Blueprint for it!"});
		
		Item[] items = Item.formatValues(p);
		for(int x = 1, i = 0; i < items.length; x++, i++)
		{
			Item item = items[i];
			menu.setOption(x, item.getDisplayItem(p));
		}
		menu.open(p);
	}
}
