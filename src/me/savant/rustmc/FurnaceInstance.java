package me.savant.rustmc;

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
	
	Block block;
	String uuid;
	Player p;
	int id;
	
	boolean status;
	
	public FurnaceInstance(Block block, String uuid, Player p)
	{
		this.block = block;
		this.uuid = uuid;
		this.p = p;
		this.status = false;
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
	
	public void activate()
	{
		if(work())
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
		if(work())
		{
			Bukkit.getScheduler().cancelTask(id);
			status = false;
			updateAction();
		}
	}
	
	private final int WOOD_AMOUNT = 2;
	
	private void schedule()
	{
		if(Bukkit.getScheduler().isCurrentlyRunning(id))
		{
			Bukkit.getScheduler().cancelTask(id);
			System.out.println("Task did not cancel, attempted to create another one.");
		}
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				p.sendMessage(ChatColor.GRAY + "...");
				if(p.getOpenInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTitle()).equalsIgnoreCase("Furnace"))
				{
					((Chest)block.getState()).getBlockInventory().setContents(p.getOpenInventory().getTopInventory().getContents());
				}
				if(containsWood(WOOD_AMOUNT))
				{
					removeWood(WOOD_AMOUNT);
				}
				else
				{
					deactivate();
				}
				processOre();
			}
			
		}, 0L, 20L);
	}
	
	void processOre()
	{
		
	}
	
	private void removeWood(int amount)
	{
		ItemIndex.removeItem(ItemIndex.getItem(ItemType.WOOD), WOOD_AMOUNT, getInventory());
	}
	
	private boolean containsWood(int amount)
	{
		int i = ItemIndex.getAmount(ItemIndex.getItem(ItemType.WOOD), getInventory());
		if(i >= amount)
		{
			return true;
		}
		return false;
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
	
	private Inventory getInventory()
	{
		if(p.getOpenInventory() != null && ChatColor.stripColor(p.getOpenInventory().getTitle()).equalsIgnoreCase("Furnace"))
			return p.getOpenInventory().getTopInventory();
		return ((Chest)block.getState()).getBlockInventory();
	}
	
	static boolean canWork = true;
	public static boolean work()
	{
		if(canWork)
		{
			canWork = false;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				@Override
				public void run() {
					canWork = true;
				}
				
			}, 5L);
			return true;
		}
		else
		{
			return false;
		}
	}
}
