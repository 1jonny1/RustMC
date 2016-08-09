package me.savant.rustmc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.savant.blueprint.BlueprintListener;
import me.savant.crafting.CraftingListener;
import me.savant.furnace.Fire;
import me.savant.furnace.Furnace;
import me.savant.furnace.FurnaceInstance;
import me.savant.items.HempListener;
import me.savant.items.Item;
import me.savant.items.ItemListener;
import me.savant.rustmc.ItemMenu.OptionClickEvent;
import me.savant.terrain.Animal;
import me.savant.terrain.MaterialGeneration;

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
		Util.plugin = this;
		
		pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new Break(), this);
		pm.registerEvents(new Furnace(this), this);
		pm.registerEvents(new CraftingListener(this), this);
		pm.registerEvents(new BlueprintListener(this), this);
		pm.registerEvents(new Death(this), this);
		pm.registerEvents(new HempListener(), this);
		pm.registerEvents(new ItemListener(), this);
		pm.registerEvents(new Animal(), this);
		
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
				matGen.update();
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
		if(message.equalsIgnoreCase("building"))
		{
			p.sendMessage(ChatColor.GRAY + "Giving Building Plan");
			p.getInventory().addItem(Item.BUILDING_PLAN.getItem());
			
			p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 15);
			
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
			
			p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 15);
			
			e.setCancelled(true);
		}
		if(message.equalsIgnoreCase("items") || message.equalsIgnoreCase("item"))
		{
			ItemMenu itemMenu = new ItemMenu(ChatColor.RED + "Item Index", 54, new ItemMenu.OptionClickEventHandler()
			{
				@Override
				public void onOptionClick(OptionClickEvent e)
				{
					if(Util.work())
					{
						e.setWillClose(false);
						e.setWillDestroy(false);
						Player p = e.getPlayer();
						p.getInventory().addItem(Item.getValue(e.getName()).getItem());
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 15);
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
		if(cmd.getName().equalsIgnoreCase("set"))
		{
			Player p = (Player) sender;
			if(args.length == 2)
			{
				String type = args[0];
				if(type.equalsIgnoreCase("octave"))
				{
					try
					{
						int octave = Integer.parseInt(args[1]);
						matGen.terrain.setOctaves(octave);
						p.sendMessage(ChatColor.GRAY + "Octaves set to " + matGen.terrain.getOctaves());
					}
					catch (NumberFormatException e)
					{
						p.sendMessage(ChatColor.RED + args[1] + " is not a Integer");
					}
					return true;
				}
				else if(type.equalsIgnoreCase("amplitude"))
				{
					try
					{
						float amplitude = Float.parseFloat(args[1]);
						matGen.terrain.setAmplitude(amplitude);
						p.sendMessage(ChatColor.GRAY + "Amplitude set to " + matGen.terrain.getAmplitude());
					}
					catch (NumberFormatException e)
					{
						p.sendMessage(ChatColor.RED + args[1] + " is not a Floating point Value");
					}
					return true;
				}
				else if(type.equalsIgnoreCase("roughness"))
				{
					try
					{
						float roughness = Float.parseFloat(args[1]);
						matGen.terrain.setRoughness(roughness);
						p.sendMessage(ChatColor.GRAY + "Roughness set to " + matGen.terrain.getRoughness());
					}
					catch (NumberFormatException e)
					{
						p.sendMessage(ChatColor.RED + args[1] + " is not a Floating point Value");
					}
					return true;
				}
			}
			p.sendMessage("/set <octave | amplitude | roughness> <value>");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("values"))
		{
			Player p = (Player) sender;
			p.sendMessage(ChatColor.GRAY + "OCTAVES: " + matGen.terrain.getOctaves());
			p.sendMessage(ChatColor.GRAY + "AMPLITUDE: " + matGen.terrain.getAmplitude());
			p.sendMessage(ChatColor.GRAY + "ROUGHNESS: " + matGen.terrain.getRoughness());
		}
		if(cmd.getName().equalsIgnoreCase("regen"))
		{
			Player p = (Player) sender;
			if(p.isOp())
			{
				if(args.length == 0)
				{
					matGen.update();
					return true;
				}
				else if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("full"))
					{
						matGen.updateAll();
						return true;
					}
				}
				p.sendMessage("/regen [full]");
				return true;
			}
			else
			{
				p.sendMessage(ChatColor.RED + "Reported to Staff.");
			}
		}
		if(cmd.getName().equalsIgnoreCase("reset"))
		{
			Player p = (Player) sender;
			if(p.isOp())
			{
				matGen.reset();
			}
			else
			{
				p.sendMessage(ChatColor.RED + "Reported to Staff.");
			}
		}
		return false;
	}
}
