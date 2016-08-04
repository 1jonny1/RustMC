package me.savant.furnace;

import java.util.ArrayList;
import java.util.UUID;

import me.savant.furnace.FurnaceMenu.OptionClickEvent;
import me.savant.rustmc.RustMC;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class Furnace implements Listener
{
	RustMC plugin;
	public Furnace(RustMC plugin)
	{
		this.plugin = plugin;
	}
	
	ArrayList<FurnaceInstance> instances = new ArrayList<FurnaceInstance>();
	
	@EventHandler
	public void click(PlayerInteractEvent e)
	{
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.FURNACE)
		{
			e.setCancelled(true);
			Player p = e.getPlayer();
			Block block = e.getClickedBlock();
			Block chestBlock = block.getRelative(getCardinalDirection(p));
			if(chestBlock.getType() == Material.CHEST)
			{
				String uuid = getUUID(chestBlock);
				FurnaceInstance furnaceInstance = getInstance(chestBlock, uuid, p);
				
				final Chest chest = (Chest)chestBlock.getState();
				FurnaceMenu menu = new FurnaceMenu(ChatColor.BOLD + "Furnace", 27, new FurnaceMenu.OptionClickEventHandler()
				{
					public void onOptionClick(OptionClickEvent event) 
					{
						event.setWillClose(false);
						event.setWillDestroy(false);
					}
				 }, plugin, chest, furnaceInstance)
				 .setContents(chest.getInventory().getContents());
				 menu.open(p);
				 
				 furnaceInstance.activate();
			}
			else
			{
				ActionBarAPI.sendActionBar(p, ChatColor.RED + "" + ChatColor.BOLD + "Open the Furnace Menu by Right Clicking Straight On", 120);
			}
		}
	}
	
	private FurnaceInstance getInstance(Block block, String uuid, Player p)
	{
		for(FurnaceInstance instance : instances)
		{
			if(instance.uuid.equalsIgnoreCase(uuid))
			{
				instance.setUpdater(p);
				return instance;
			}
		}
		FurnaceInstance instance = new FurnaceInstance(block, uuid, p);
		instances.add(instance);
		return instance;
	}
	
	private String getUUID(Block block)
	{
		if(block.getMetadata("furnace-uuid") == null || block.getMetadata("furnace-uuid").isEmpty())
		{
			block.setMetadata("furnace-uuid", new FixedMetadataValue(plugin, UUID.randomUUID().toString()));
		}
		return block.getMetadata("furnace-uuid").get(0).asString();
	}
	
	public static BlockFace getCardinalDirection(Player player)
	{
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
         if (0 <= rotation && rotation < 22.5)
         {
        	 //!
            return BlockFace.WEST;
        }
         else if (22.5 <= rotation && rotation < 67.5)
         {
        	 //!
            return BlockFace.NORTH_WEST;
        }
        else if (67.5 <= rotation && rotation < 112.5)
        {
        	//!
            return BlockFace.NORTH;
        } 
       else if (112.5 <= rotation && rotation < 157.5)
       {
    	   //!
            return BlockFace.NORTH_EAST;
        } 
       else if (157.5 <= rotation && rotation < 202.5)
       {
    	   //!
            return BlockFace.EAST;
        }
        else if (202.5 <= rotation && rotation < 247.5)
        {
        	//!
            return BlockFace.SOUTH_EAST;
        }
        else if (247.5 <= rotation && rotation < 292.5)
        {
        	//!
            return BlockFace.SOUTH;
        }
        else if (292.5 <= rotation && rotation < 337.5)
        {
        	//!
            return BlockFace.SOUTH_WEST;
        }
        else if (337.5 <= rotation && rotation < 360.0)
        {
        	//!
            return BlockFace.WEST;
        }
        else
        {
            return null;
        }
    }
}
