package me.savant.rustmc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.savant.blueprint.BlueprintListener;
import me.savant.crafting.CraftingListener;
import me.savant.fire.Fire;
import me.savant.furnace.Furnace;
import me.savant.furnace.FurnaceInstance;
import me.savant.items.HempListener;
import me.savant.items.Item;
import me.savant.items.ItemListener;
import me.savant.rustmc.ItemMenu.OptionClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RustMC extends JavaPlugin implements Listener
{	
	private String version = "1.6";
	private MaterialGeneration matGen;
	
	private final int HOURS_FOR_RESET = 1;
	
	PluginManager pm;
	
	public void onEnable()
	{
		FurnaceInstance.plugin = this;
		Fire.plugin = this;
		
		pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new Break(), this);
		pm.registerEvents(new Furnace(this), this);
		pm.registerEvents(new CraftingListener(this), this);
		pm.registerEvents(new BlueprintListener(this), this);
		pm.registerEvents(new Death(this), this);
		pm.registerEvents(new HempListener(), this);
		pm.registerEvents(new ItemListener(), this);
		
		matGen = new MaterialGeneration();
		
		long hour = 1200L * 60;
		long time = hour * HOURS_FOR_RESET;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			//20L = 1 Second
			//60 Seconds = 20 * 60 = 1,200L
			//4 Minutes = 1,200L * 4 = 4,800L
			
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(p.isOp() || p.hasPermission("admin") || p.hasPermission("staff"))
					{
						p.sendMessage(ChatColor.GRAY + "Starting Ore Regeneration");
					}
				}
				
				long duration = matGen.update();
				
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(p.isOp() || p.hasPermission("admin") || p.hasPermission("staff"))
					{
						p.sendMessage(ChatColor.GRAY + "Ended... Took " + duration + "ms");
					}
				}
			}
			
		}, 0L, time);
	}
	
	public void registerEvents(Listener listener)
	{
		pm.registerEvents(listener, this);
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e)
	{
		String message = e.getMessage().split("/")[1];
		Player p = e.getPlayer();
		if(message.equalsIgnoreCase("about") || message.equalsIgnoreCase("version"))
		{
			p.sendMessage(ChatColor.GREEN + "RustMC v" + version + " by _Savant");
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("plugin") || message.equalsIgnoreCase("plugins") || message.equalsIgnoreCase("pl") || message.equalsIgnoreCase("help") || message.equalsIgnoreCase("?"))
		{
			p.sendMessage(ChatColor.DARK_RED + "RustMC");
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("reset"))
		{
			p.sendMessage(ChatColor.GRAY + "Starting Full Reset");
			
			long duration = matGen.reset();
			
			p.sendMessage(ChatColor.GRAY + "Ended.. Took " + duration + "ms");
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("regen") || message.equalsIgnoreCase("regenerate"))
		{
			p.sendMessage(ChatColor.GRAY + "Starting Ore Regeneration");

			long duration = matGen.update();
			
			p.sendMessage(ChatColor.GRAY + "Ended... Took " + duration + "ms");
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("building"))
		{
			p.sendMessage(ChatColor.GRAY + "Giving Building Plan");
			p.getInventory().addItem(Item.BUILDING_PLAN.getItem());
			
			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 15);
			
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("crafting"))
		{
			p.sendMessage(ChatColor.GRAY + "Giving Crafting Item");
			ItemStack item = new ItemStack(Material.WORKBENCH, 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Crafting");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.BLUE + "Click to open the Crafting Menu");
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			e.getPlayer().getInventory().setItem(8, item);
			
			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 15);
			
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("items") || message.equalsIgnoreCase("item"))
		{
			ItemMenu itemMenu = new ItemMenu(ChatColor.RED + "Item Index", 54, new ItemMenu.OptionClickEventHandler()
			{
				@Override
				public void onOptionClick(OptionClickEvent e)
				{
					if(work())
					{
						e.setWillClose(false);
						e.setWillDestroy(false);
						Player p = e.getPlayer();
						p.getInventory().addItem(Item.getValue(e.getName()).getItem());
						p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 15);
						p.updateInventory();
					}
				}
			}, this)
			.setOption(0, new ItemStack(Material.GRASS), ChatColor.GREEN + "?", new String[] { "Click on an item", "to spawn it in!" });
			int x = 1;
			for(Item item : Item.values())
			{
				String[] lore;
				if(item.getItem().getItemMeta().hasLore())
					lore = item.getItem().getItemMeta().getLore().toArray(new String[item.getItem().getItemMeta().getLore().size()]);
				else
					lore = new String[] {};
				itemMenu.setOption(x, item.getItem(), item.getName(), lore);
				x++;
			}
			itemMenu.open(p);
			e.setCancelled(true);
		}
	}
	
	boolean canWork = true;
	public boolean work()
	{
		if(canWork)
		{
			canWork = false;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
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
	
	public boolean onCommand (CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("rspawn"))
		{
			Player p = null;
			String prefix = "";
			if(sender instanceof Player)
				p = (Player) sender;
			else if(args.length == 1)
			{
				prefix = "[SUDO] ";
				p = Bukkit.getPlayer(args[0]);
			}
			else
			{
				System.out.println("[RustMC] /rspawn proper usage: /rspawn {player}");
				return false;
			}
			Random random = new Random();
			int x = random.nextInt(matGen.size);
			int z = random.nextInt(matGen.size);
			int y = p.getWorld().getHighestBlockYAt(x, z);
			Location teleportLocation = new Location(p.getWorld(), x, y, z);
			p.sendMessage(ChatColor.GRAY + prefix + "Spawned at X: " + x + " Z: " + z);
			p.teleport(teleportLocation);
			return true;
		}
		return false;
	}
}
