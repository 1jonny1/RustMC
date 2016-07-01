package me.savant.rustmc;

import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
	private String version = "0.1";
	private MaterialGeneration matGen;
	
	PluginManager pm;
	
	public void onEnable()
	{
		FurnaceInstance.plugin = this;
		
		pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new Break(), this);
		pm.registerEvents(new Furnace(this), this);
		
		matGen = new MaterialGeneration();
		
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
			
		}, 0L, 4800L);
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
			
			ItemStack item = new ItemStack(Material.PAPER, 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "Building Plan" + ChatColor.RESET + "" + ChatColor.GRAY + " - core");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Shift + Right Click to Select");
			lore.add(ChatColor.GRAY + "Right Click to Place");
			lore.add(ChatColor.GRAY + "Overrides default Hotkeys");
			meta.setLore(lore);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
			
			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 15);
			
			e.setCancelled(true);
		}
	}
}
