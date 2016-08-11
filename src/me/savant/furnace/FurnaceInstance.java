package me.savant.furnace;

import me.savant.items.ItemIndex;
import me.savant.items.ItemType;
import me.savant.rustmc.RustMC;
import me.savant.rustmc.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FurnaceInstance
{
	public static RustMC plugin;
	
	private final int WOOD_AMOUNT = 2;
	private final int REFINE_AMOUNT = 2;
	private final long REFINE_TIME = 40L;
	
	Block block;
	String uuid;
	Player p;
	int id;
	boolean status;
	Fire fire;
	
	public FurnaceInstance(Block block, String uuid, Player p)
	{
		this.block = block;
		this.uuid = uuid;
		this.p = p;
		this.status = false;
		fire = new Fire(block.getLocation());
	}
	
	private Inventory getInventory()
	{
		if(p.getOpenInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTitle()).equalsIgnoreCase("Furnace"))
			return p.getOpenInventory().getTopInventory();
		if(!(block.getState() instanceof Chest))
			deactivate();
		return ((Chest)block.getState()).getBlockInventory();
	}
	
	
	public void activate()
	{
		if(Util.work())
		{
			if(!status)
			{
				if(containsWood(WOOD_AMOUNT))
				{
					if(!Bukkit.getScheduler().isCurrentlyRunning(id))
						schedule();
					status = true;
					updateAction();
				}
			}
		}
	}
	
	public void deactivate()
	{
		if(Util.work())
		{
			Bukkit.getScheduler().cancelTask(id);
			status = false;
			updateAction();
		}
	}
	
	
	public void setUpdater(Player p)
	{
		this.p = p;
	}
	
	public Block getBlock()
	{
		return block;
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	
	private void schedule()
	{
		if(Bukkit.getScheduler().isCurrentlyRunning(id) || status)
		{
			deactivate();
			System.out.println("Task did not cancel, attempted to create another one.");
		}
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if(p.getOpenInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTitle()).equalsIgnoreCase("Furnace"))
				{
					((Chest)block.getState()).getBlockInventory().setContents(p.getOpenInventory().getTopInventory().getContents());
				}
				
				if(containsWood(WOOD_AMOUNT))
				{
					fire.start();
					removeWood(WOOD_AMOUNT);
					if(Util.chance(50f))
					{
						ItemIndex.addItem(ItemType.CHARCOAL, 1, getInventory());
					}
				}
				else
				{
					fire.stop();
					deactivate();
				}
				processOre();
			}
			
		}, 0L, REFINE_TIME);
	}
	
	private boolean containsWood(int amount)
	{
		int i = ItemIndex.getAmount(ItemIndex.getType(ItemType.WOOD), getInventory());
		if(i >= amount)
		{
			return true;
		}
		return false;
	}
	
	private void removeWood(int amount)
	{
		ItemIndex.removeItem(ItemIndex.getType(ItemType.WOOD), WOOD_AMOUNT, getInventory());
	}
	
	void processOre()
	{
		int refined = 0;
		for(ItemStack item : getInventory().getContents())
		{
			if(item != null && item.getType() != Material.AIR)
			{
				ItemType type;
				if(item.hasItemMeta() && item.getItemMeta().hasDisplayName())
					type = ItemIndex.parseItemType(item.getItemMeta().getDisplayName());
				else
					type = ItemIndex.getType(item.getType());
				if(ItemIndex.isRefinable(type))
				{
					for(int x = 0; x < item.getAmount() && refined < REFINE_AMOUNT; x++, refined++)
					{
						ItemIndex.removeItem(ItemIndex.getType(type), 1, getInventory());
						ItemIndex.addItem(ItemIndex.getRefined(type), 1, getInventory());
					}
					refined = 0;
				}
			}
		}
	}
	
	private void updateAction()
	{
		Block b1 = block.getLocation().clone().add(1, 0, 0).getBlock();
		org.bukkit.block.Furnace f1 = (org.bukkit.block.Furnace)b1.getState();
		
		Block b2 = block.getLocation().clone().add(-1, 0, 0).getBlock();
		org.bukkit.block.Furnace f2 = (org.bukkit.block.Furnace)b2.getState();
		
		Block b3 = block.getLocation().clone().add(0, 0, 1).getBlock();
		org.bukkit.block.Furnace f3 = (org.bukkit.block.Furnace)b3.getState();
		
		Block b4 = block.getLocation().clone().add(0, 0, -1).getBlock();
		org.bukkit.block.Furnace f4 = (org.bukkit.block.Furnace)b4.getState();
		
		if(status)
		{
			f1.setBurnTime((short) 999999999);
			f2.setBurnTime((short) 999999999);
			f3.setBurnTime((short) 999999999);
			f4.setBurnTime((short) 999999999);
		}
		else
		{
			f1.setBurnTime((short) 0);
			f2.setBurnTime((short) 0);
			f3.setBurnTime((short) 0);
			f4.setBurnTime((short) 0);
		}
	}
}
