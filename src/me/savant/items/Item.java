package me.savant.items;

import java.util.ArrayList;
import java.util.List;

import me.savant.blueprint.Blueprint;
import me.savant.crafting.Resource;
import me.savant.crafting.ResourceCost;
import me.savant.crafting.ResourceEntry;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Item
{
	PAPER,
	BUILDING_PLAN,
	WOOD_SWORD,
	WOOD_PICK,
	STONE_AXE,
	STONE_PICK,
	BANDAGE,
	BOW,
	ARROW,
	SHOTGUN,
	PUMP_SHOTGUN,
	SHOTGUN_SHELLS,
	MAKESHIFT_PISTOL,
	PISTOL,
	SUBMACHINE_GUN,
	PISTOL_AMMO,
	RIFLE,
	SNIPER_RIFLE,
	AK47,
	ASSAULT_AMMO,
	CLOTH_BOOTS,
	CLOTH_PANTS,
	CLOTH_SHIRT,
	CLOTH_HAT,
	FURNACE,
	METAL_PICK,
	METAL_AXE,
	GUN_POWDER,
	EXPLOSIVE,
	C4,
	CHARCOAL;
	
	public boolean isDefaultItem()
	{
		switch(this)
		{
			case PAPER:
			case WOOD_PICK:
			case STONE_AXE:
			case STONE_PICK:
			case BANDAGE:
			case BOW:
			case ARROW:
			case WOOD_SWORD:
			case CLOTH_BOOTS:
			case CLOTH_PANTS:
			case CLOTH_SHIRT:
			case CLOTH_HAT:
			case FURNACE:
			case CHARCOAL:
			case BUILDING_PLAN:
				return true;
		}
		return false;
	}
	
	public static Item getValue(String name)
	{
		name = name.split(" -")[0];
		for(Item i : values())
		{
			if(i.toString().replace("_", " ").equalsIgnoreCase(ChatColor.stripColor(name)))
				return i;
		}
		System.out.println("[RustMC] Failed to parse item " + name + ChatColor.RESET + " to an enum.");
		return null;
	}
	
	public static Item[] formatValues(Player p)
	{
		List<Item> unlocked = new ArrayList<Item>();
		List<Item> locked = new ArrayList<Item>();
		for(Item item : values())
		{
			if(item.isUnlocked(p))
			{
				unlocked.add(item);
			}
			else
			{
				locked.add(item);
			}
		}
		unlocked.addAll(locked);
		return unlocked.toArray(new Item[unlocked.size()]);
	}
	
	public static Item[] getLockedItems()
	{
		List<Item> ls = new ArrayList<Item>();
		for(Item item : values())
		{
			if(!item.isDefaultItem())
			{
				ls.add(item);
			}
		}
		return ls.toArray(new Item[ls.size()]);
	}
	
	public static Item[] getDefaultItems()
	{
		List<Item> ls = new ArrayList<Item>();
		for(Item item : values())
		{
			if(item.isDefaultItem())
			{
				ls.add(item);
			}
		}
		return ls.toArray(new Item[ls.size()]);
	}
	
	public void removeItem(Player p, int amount)
	{
		int removed = 0;
		for(ItemStack item : p.getInventory())
		{
			if(item != null && item.getType() != Material.AIR && item.getType() == getItem().getType())
			{
				if(removed < amount)
				{
					if(item.getAmount() + removed <= amount)
					{
						removed = removed + item.getAmount();
						p.getInventory().removeItem(item);
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
	
	public ItemStack getItem()
	{
		String[] none = new String[] {};
		switch(this)
		{
			case PAPER:
				return createItem(new ItemStack(Material.PAPER, 1), "Paper", none);
			case BUILDING_PLAN:
				return createItem(new ItemStack(Material.ANVIL, 1), ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "Building Plan" + ChatColor.RESET + "" + ChatColor.GRAY + " - core", new String[] {ChatColor.GRAY + "Shift + Right Click to Select", ChatColor.GRAY + "Right Click to Place", ChatColor.GRAY + "Overrides default Hotkeys"});
			case STONE_AXE:
				return createItem(new ItemStack(Material.STONE_AXE, 1), "Stone Axe", none);
			case STONE_PICK:
				return createItem(new ItemStack(Material.STONE_PICKAXE, 1), "Stone Pick", none);
			case WOOD_PICK:
				return createItem(new ItemStack(Material.WOOD_PICKAXE, 1), "Wood Pick", none);
			case BANDAGE:
				return createItem(new ItemStack(Material.WOOL, 1), "Bandage", new String[] {ChatColor.GRAY + "Heals 2♥" });
			case BOW:
				return createItem(new ItemStack(Material.BOW, 1), "Bow", none);
			case ARROW:
				return createItem(new ItemStack(Material.ARROW, 1), "Arrow", none);
			case WOOD_SWORD:
				return createItem(new ItemStack(Material.WOOD_SWORD, 1), "Wood Sword", none);
			case SHOTGUN:
				return createItem(new ItemStack(Material.getMaterial(418), 1), "Shotgun", none);
			case PUMP_SHOTGUN:
				return createItem(new ItemStack(Material.getMaterial(419), 1), "Pump Shotgun", none);
			case SHOTGUN_SHELLS:
				return createItem(new ItemStack(Material.getMaterial(351), 1, (short) 3), "Shotgun Shells", new String[] { ChatColor.GRAY + "Used With:", ChatColor.GRAY + "  + Shotgun", ChatColor.GRAY + "  + Pump Shotgun" });
			case MAKESHIFT_PISTOL:
				return createItem(new ItemStack(Material.IRON_HOE, 1), "Makeshift Pistol", none);
			case PISTOL:
				return createItem(new ItemStack(Material.GOLD_HOE, 1), "Pistol", none);
			case SUBMACHINE_GUN:
				return createItem(new ItemStack(Material.DIAMOND_HOE, 1), "Submachine Gun", none);
			case PISTOL_AMMO:
				return createItem(new ItemStack(Material.SEEDS, 1), "Pistol Ammo", new String[] { ChatColor.GRAY + "Used With:", ChatColor.GRAY + "  + Makeshift Pistol", ChatColor.GRAY + "  + Pistol", ChatColor.GRAY + "  + Submachine Gun"});
			case RIFLE:
				return createItem(new ItemStack(Material.IRON_SPADE, 1), "Rifle", none);
			case SNIPER_RIFLE:
				return createItem(new ItemStack(Material.GOLD_SPADE, 1), "Sniper Rifle", none);
			case AK47:
				return createItem(new ItemStack(Material.DIAMOND_SPADE, 1), "AK47", none);
			case ASSAULT_AMMO:
				return createItem(new ItemStack(Material.MELON_SEEDS, 1), "Assault Ammo", none);
			case CLOTH_BOOTS:
				return createItem(new ItemStack(Material.LEATHER_BOOTS, 1), "Cloth Boots", none);
			case CLOTH_PANTS:
				return createItem(new ItemStack(Material.LEATHER_LEGGINGS, 1), "Cloth Pants", none);
			case CLOTH_SHIRT:
				return createItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1), "Cloth Shirt", none);
			case CLOTH_HAT:
				return createItem(new ItemStack(Material.LEATHER_HELMET, 1), "Cloth Hat", none);
			case FURNACE:
				return createItem(new ItemStack(Material.FURNACE, 1), "Furnace", new String[] { ChatColor.GRAY + "3x3x3", ChatColor.GRAY + "Used to smelt ores" });
			case METAL_PICK:
				return createItem(new ItemStack(Material.IRON_PICKAXE, 1), "Metal Pick", none);
			case METAL_AXE:
				return createItem(new ItemStack(Material.IRON_AXE, 1), "Metal Axe", none);
			case GUN_POWDER:
				return createItem(new ItemStack(Material.SULPHUR, 1), "Gun Powder", none);
			case EXPLOSIVE:
				return createItem(new ItemStack(Material.REDSTONE_TORCH_ON, 1), "Explosive", none);
			case C4:
				return createItem(new ItemStack(Material.TNT, 1), "C4", new String[] { "Click to Launch!" });
			case CHARCOAL:
				return createItem(new ItemStack(Material.COAL, 1), "Charcoal");
		}
		return new ItemStack(Material.AIR, 1);
	}
	
	public ItemStack getBlueprintItem()
	{
		ItemStack item = getItem();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "(Blueprint)");
		lore.add(ChatColor.RED + "Click to learn!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public String getName()
	{
		return getItem().getItemMeta().getDisplayName();
	}
	
	
	public String getStrippedName()
	{
		return ChatColor.stripColor(getItem().getItemMeta().getDisplayName());
	}
	
	public ResourceCost getCost()
	{
		switch(this)
		{
			case PAPER:
				return new ResourceCost(new ResourceEntry("Wood", new Resource(ItemType.WOOD), 100));
			
			case BUILDING_PLAN:
				return new ResourceCost(new ResourceEntry("Paper", new Resource(Item.PAPER), 1));
			case WOOD_PICK:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 50)
				});
			case STONE_AXE:
				return new ResourceCost(new ResourceEntry[]
				{
						new ResourceEntry("Wood", new Resource(ItemType.WOOD), 125),
						new ResourceEntry("Stone", new Resource(ItemType.STONE), 200)
				});
			
			case STONE_PICK:
				return new ResourceCost(new ResourceEntry[]
				{
						new ResourceEntry("Wood", new Resource(ItemType.WOOD), 100),
						new ResourceEntry("Stone", new Resource(ItemType.STONE), 225)
				});
			case BANDAGE:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 2)
				});
			case BOW:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 250),
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 30)
				});
			case ARROW:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 25)
				});
			case WOOD_SWORD:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 450)
				});
			case SHOTGUN:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 100),
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 25)
				});
			case PUMP_SHOTGUN:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 250),
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 100)
				});
			case SHOTGUN_SHELLS:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Stone", new Resource(ItemType.STONE), 5),
					new ResourceEntry("Sulfur", new Resource(ItemType.SULFUR), 5)
				});
			case MAKESHIFT_PISTOL:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Stone", new Resource(ItemType.STONE), 225),
					new ResourceEntry("Wood", new Resource(ItemType.SULFUR), 100)
				});
			case PISTOL:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 125),
					new ResourceEntry("High Quality Metal", new Resource(ItemType.HIGH_QUALITY_METAL), 5)
				});
			case SUBMACHINE_GUN:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 50),
					new ResourceEntry("High Quality Metal", new Resource(ItemType.HIGH_QUALITY_METAL), 20),
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 100)
				});
			case PISTOL_AMMO:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 2),
					new ResourceEntry("Sulfur", new Resource(ItemType.SULFUR), 5)
				});
			case RIFLE:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 250),
					new ResourceEntry("High Quality Metal", new Resource(ItemType.HIGH_QUALITY_METAL), 100)
				});
			case SNIPER_RIFLE:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 350),
					new ResourceEntry("High Quality Metal", new Resource(ItemType.HIGH_QUALITY_METAL), 200)
				});
			case AK47:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 400),
					new ResourceEntry("High Quality Metal", new Resource(ItemType.HIGH_QUALITY_METAL), 550)
				});
			case ASSAULT_AMMO:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 5),
					new ResourceEntry("Sulfur", new Resource(ItemType.SULFUR), 10)
				});
			case CLOTH_BOOTS:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 30)	
				});
			case CLOTH_PANTS:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 50)	
				});
			case CLOTH_SHIRT:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 60)	
				});
			case CLOTH_HAT:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 20)	
				});
			case FURNACE:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 250),
					new ResourceEntry("Stone", new Resource(ItemType.STONE), 100)
				});
			case METAL_PICK:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 200),
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 100)
				});
			case METAL_AXE:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Wood", new Resource(ItemType.WOOD), 150),
					new ResourceEntry("Metal", new Resource(ItemType.METAL), 75)
				});
			case GUN_POWDER:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Sulfur", new Resource(ItemType.SULFUR), 5),
					new ResourceEntry("Charcoal", new Resource(Item.CHARCOAL), 20)
				});
			case EXPLOSIVE:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Gun Powder", new Resource(Item.GUN_POWDER), 10),
					new ResourceEntry("Sulfur", new Resource(ItemType.SULFUR), 10)
				});
			case C4:
				return new ResourceCost(new ResourceEntry[]
				{
					new ResourceEntry("Explosive", new Resource(Item.EXPLOSIVE), 5),
					new ResourceEntry("Cloth", new Resource(ItemType.CLOTH), 200)
				});
			case CHARCOAL:
				return new ResourceCost(new ResourceEntry[]
				{
						new ResourceEntry("Wood", new Resource(ItemType.WOOD), 5),
				});
		}
		return null;
	}
	
	public boolean isUnlocked(Player p)
	{
		if(isDefaultItem())
			return true;
		else if(Blueprint.hasBlueprint(p, getItem().getType()))
			return true;
		else
			return false;
	}
	
	public ItemStack getDisplayItem(Player p)
	{
		ItemStack item = getItem();
		
		List<String> lore = new ArrayList<String>();
		
		if(!isUnlocked(p))
		{
			lore.add(ChatColor.RED + "" + ChatColor.BOLD + "[LOCKED]");
		}
		else
		{
			lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "[UNLOCKED!]");
		}
		
		String costLine = ChatColor.GRAY + "";
		int x = 1;
		for(ResourceEntry entry : getCost().getEntrys())
		{
			if(getCost().getEntrys().length == 1)
				costLine = costLine + entry.getAmount() + " " + entry.getUnit();
			else if(getCost().getEntrys().length != x)
				costLine = costLine + entry.getAmount() + " " + entry.getUnit() + ", ";
			else
				costLine = costLine + entry.getAmount() + " " + entry.getUnit();
			x++;
		}
		lore.add(costLine);
		
		lore.add(ChatColor.BLUE + "" + ChatColor.ITALIC + "Click to Craft!");
		
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack createItem(ItemStack item, String name, String... lore)
	{
		ItemMeta meta = item.getItemMeta();
		if(name.equalsIgnoreCase(ChatColor.stripColor(name)))
			meta.setDisplayName(ChatColor.BLUE + name);
		else
			meta.setDisplayName(name);
		List<String> l = new ArrayList<String>();
		if(lore != null)
		{
			for(String s : lore)
			{
				l.add(s);
			}
		}
		meta.setLore(l);
		item.setItemMeta(meta);
		return item;
	}
}
