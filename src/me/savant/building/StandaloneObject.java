package me.savant.building;

import java.io.IOException;
import java.util.HashSet;

import me.savant.items.Item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class StandaloneObject implements Listener
{
	Item[] items = new Item[] { Item.FURNACE };
	
	@EventHandler
	public void onPlace(PlayerInteractEvent e)
	{
		if(e.getItem() != null)
		{
			for(Item item : items)
			{
				if(e.getItem().getType() == item.getItem().getType())
				{
					e.setCancelled(true);
					String name = item.getStrippedName().toLowerCase() + "_standalone";
					Player p = e.getPlayer();
					Block b = p.getTargetBlock((HashSet<Byte>) null, 5);
					if(b.getType().getId() != 31)
						b = b.getRelative(BlockFace.UP);
					try
					{
						Schematic schematic = Schematic.parseSchematic(name);
						if(schematic.canPlace(b.getLocation(), Material.getMaterial(31)))
						{
							Schematic.pasteSchematic(p.getWorld(), Schematic.Center(b.getLocation(), schematic.getLength(), schematic.getWidth()), schematic, p);
							item.removeItem(p, 1);
						}
						else
						{
							ActionBarAPI.sendActionBar(p, ChatColor.RED + "Cannot place this here", 100);
						}
					}
					catch (IOException e1)
					{
						System.out.println("[RustMC] Cannot find schematic of standalone object: " + name);
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
