package me.savant.items;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class ItemIndex
{
	public static ItemType getRefined(ItemType type)
	{
		if(type == ItemType.SULFUR_ORE)
			return ItemType.SULFUR;
		if(type == ItemType.METAL_ORE)
			return ItemType.METAL;
		if(type == ItemType.HIGH_QUALITY_METAL_ORE)
			return ItemType.HIGH_QUALITY_METAL;
		return null;
	}
	
	public static boolean isRefinable(ItemType type)
	{
		switch (type)
		{
			case SULFUR_ORE:
				return true;
			case METAL_ORE:
				return true;
			case HIGH_QUALITY_METAL_ORE:
				return true;
			default:
				return false;
		}
	}
	
	public static Material getType(ItemType type)
	{
		if(type == ItemType.SULFUR_ORE)
			return Material.GOLD_ORE;
		if(type == ItemType.SULFUR)
			return Material.GOLD_INGOT;
		if(type == ItemType.METAL_ORE)
			return Material.IRON_ORE;
		if(type == ItemType.METAL)
			return Material.IRON_INGOT;
		if(type == ItemType.HIGH_QUALITY_METAL_ORE)
			return Material.DIAMOND_ORE;
		if(type == ItemType.HIGH_QUALITY_METAL)
			return Material.DIAMOND;
		if(type == ItemType.WOOD)
			return Material.LOG;
		if(type == ItemType.STONE)
			return Material.COBBLESTONE;
		if(type == ItemType.CLOTH)
			return Material.STRING;
		if(type == ItemType.BEEF)
			return Material.COOKED_BEEF;
		if(type == ItemType.CHICKEN)
			return Material.COOKED_CHICKEN;
		if(type == ItemType.PORK)
			return Material.GRILLED_PORK;
		if(type == ItemType.CHARCOAL)
			return Material.COAL;
		return null;
	}
	
	public static ItemType getType(Material material)
	{
		switch(material)
		{
			case COBBLESTONE:
				return ItemType.STONE;
			case LOG:
				return ItemType.WOOD;
			case GOLD_ORE:
				return ItemType.SULFUR_ORE;
			case GOLD_INGOT:
				return ItemType.SULFUR;
			case IRON_ORE:
				return ItemType.METAL_ORE;
			case IRON_INGOT:
				return ItemType.METAL;
			case DIAMOND_ORE:
				return ItemType.HIGH_QUALITY_METAL_ORE;
			case DIAMOND:
				return ItemType.HIGH_QUALITY_METAL;
			case STRING:
				return ItemType.CLOTH;
			case COOKED_BEEF:
				return ItemType.BEEF;
			case COOKED_CHICKEN:
				return ItemType.CHICKEN;
			case GRILLED_PORK:
				return ItemType.PORK;
			case COAL:
				return ItemType.CHARCOAL;
			default:
				return null;
		}
	}
	
	public static void removeItem(Material type, int amount, Inventory inventory)
	{
		int removed = 0;
		for(ItemStack item : inventory.getContents())
		{
			if(item != null && item.getType() != Material.AIR && item.getType() == type)
			{
				if(removed < amount)
				{
					if(item.getAmount() + removed <= amount)
					{
						removed = removed + item.getAmount();
						inventory.removeItem(item);
					}
					else
					{
						int t = item.getAmount() - (amount - removed);
						removed = removed + (amount - removed);
						item.setAmount(t);
					}
				}
			}
		}
	}
	
	public static int getAmount(Material type, Inventory inventory)
	{
		int i = 0;
		for(ItemStack item : inventory.getContents())
		{
			if(item != null && item.getType() == type)
			{
				i += item.getAmount();
			}
		}
		return i;
	}
	
	public static ItemType parseItemType(String itemName)
	{
		itemName = ChatColor.stripColor(itemName);
		if(itemName.equalsIgnoreCase("Wood"))
			return ItemType.WOOD;
		if(itemName.equalsIgnoreCase("Stone"))
			return ItemType.STONE;
		if(itemName.equalsIgnoreCase("Sulfur Ore"))
			return ItemType.SULFUR_ORE;
		if(itemName.equalsIgnoreCase("Metal Ore"))
			return ItemType.METAL_ORE;
		if(itemName.equalsIgnoreCase("High Quality Metal Ore"))
			return ItemType.HIGH_QUALITY_METAL_ORE;
		if(itemName.equalsIgnoreCase("Sulfur"))
			return ItemType.SULFUR;
		if(itemName.equalsIgnoreCase("Metal"))
			return ItemType.METAL;
		if(itemName.equalsIgnoreCase("High Quality Metal"))
			return ItemType.HIGH_QUALITY_METAL;
		if(itemName.equalsIgnoreCase("Cloth"))
			return ItemType.CLOTH;
		if(itemName.equalsIgnoreCase("Beef"))
			return ItemType.BEEF;
		if(itemName.equalsIgnoreCase("Chicken"))
			return ItemType.CHICKEN;
		if(itemName.equalsIgnoreCase("Pork"))
			return ItemType.PORK;
		if(itemName.equalsIgnoreCase("Charcoal"))
			return ItemType.CHARCOAL;
		return null;
	}
	
	public static void addItem(ItemType type, int amount, Inventory inventory)
	{
		inventory.addItem(getItem(type, amount));
	}
	
	public static ItemStack getItem(ItemType type, int amount)
	{
		if(type == ItemType.WOOD)
		{
			ItemStack log = new ItemStack(Material.LOG, amount);
			ItemMeta meta = log.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Wood");
			log.setItemMeta(meta);
			return log;
		}
		if(type == ItemType.STONE)
		{
			ItemStack stone = new ItemStack(Material.COBBLESTONE, amount);
			ItemMeta meta = stone.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Stone");
			stone.setItemMeta(meta);
			return stone;
		}
		if(type == ItemType.SULFUR_ORE)
		{
			ItemStack sulfur_ore = new ItemStack(Material.GOLD_ORE, amount);
			ItemMeta meta = sulfur_ore.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Sulfur Ore");
			sulfur_ore.setItemMeta(meta);
			return sulfur_ore;
		}
		if(type == ItemType.METAL_ORE)
		{
			ItemStack metal_ore = new ItemStack(Material.IRON_ORE, amount);
			ItemMeta meta = metal_ore.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Metal Ore");
			metal_ore.setItemMeta(meta);
			return metal_ore;
		}
		if(type == ItemType.HIGH_QUALITY_METAL_ORE)
		{
			ItemStack high_quality_metal_ore = new ItemStack(Material.DIAMOND_ORE, amount);
			ItemMeta meta = high_quality_metal_ore.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "High Quality Metal Ore");
			high_quality_metal_ore.setItemMeta(meta);
			return high_quality_metal_ore;
		}
		if(type == ItemType.SULFUR)
		{
			ItemStack sulfur = new ItemStack(Material.GOLD_INGOT, amount);
			ItemMeta meta = sulfur.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Sulfur");
			sulfur.setItemMeta(meta);
			return sulfur;
		}
		if(type == ItemType.METAL)
		{
			ItemStack metal = new ItemStack(Material.IRON_INGOT, amount);
			ItemMeta meta = metal.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Metal");
			metal.setItemMeta(meta);
			return metal;
		}
		if(type == ItemType.HIGH_QUALITY_METAL)
		{
			ItemStack high_quality_metal = new ItemStack(Material.DIAMOND, amount);
			ItemMeta meta = high_quality_metal.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "High Quality Metal");
			high_quality_metal.setItemMeta(meta);
			return high_quality_metal;
		}
		if(type == ItemType.CLOTH)
		{
			ItemStack cloth = new ItemStack(Material.STRING, amount);
			ItemMeta meta = cloth.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Cloth");
			cloth.setItemMeta(meta);
			return cloth;
		}
		if(type == ItemType.BEEF)
		{
			ItemStack beef = new ItemStack(Material.COOKED_BEEF, amount);
			ItemMeta meta = beef.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Beef");
			beef.setItemMeta(meta);
			return beef;
		}
		if(type == ItemType.CHICKEN)
		{
			ItemStack chicken = new ItemStack(Material.COOKED_CHICKEN, amount);
			ItemMeta meta = chicken.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Chicken");
			chicken.setItemMeta(meta);
			return chicken;
		}
		if(type == ItemType.PORK)
		{
			ItemStack pork = new ItemStack(Material.GRILLED_PORK, amount);
			ItemMeta meta = pork.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Pork");
			pork.setItemMeta(meta);
			return pork;
		}
		if(type == ItemType.CHARCOAL)
		{
			ItemStack charcoal = new ItemStack(Material.COAL, amount);
			ItemMeta meta = charcoal.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + "Charcoal");
			charcoal.setItemMeta(meta);
			return charcoal;
		}
		return null;
	}
	
	public static String getName(ItemType type)
	{
		return ChatColor.stripColor(getItem(type, 1).getItemMeta().getDisplayName());
	}
	
	public static void giveItem(ItemType type, int amount, Player p)
	{
		p.getInventory().addItem(getItem(type, amount));
		p.updateInventory();
		p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 15);
		ActionBarAPI.sendActionBar(p, ChatColor.GOLD + "+" + amount + " " + getName(type), 20);
	}
}
