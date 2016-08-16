package me.savant.rustmc;

import java.util.Random;

import me.savant.building.Core;
import me.savant.building.Health;
import me.savant.building.Tag;
import me.savant.items.ItemIndex;
import me.savant.items.ItemType;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.connorlinfoot.actionbarapi.ActionBarAPI;


public class Break implements Listener
{
	private Random r;
	
	public Break()
	{
		r = new Random();
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		Material type = e.getBlock().getType();
		for(Material mat : building_blocks)
		{
			if(e.getBlock().getType() == mat && e.getPlayer().getGameMode() != GameMode.CREATIVE)
			{
				Tag.delete(e.getBlock());
				e.setCancelled(false);
				return;
			}
		}
		
		
		if(type != Material.IRON_ORE && type != Material.GOLD_ORE && type != Material.DIAMOND_ORE && type != Material.LOG && type != Material.LEAVES && !e.getPlayer().isOp())
		{
			e.setCancelled(true);
			return;
		}
		
		if(type == Material.IRON_ORE || type == Material.GOLD_ORE || type == Material.DIAMOND_ORE)
		{
			Player p = e.getPlayer();
			if(p.getItemInHand().getDurability() > p.getItemInHand().getType().getMaxDurability())
			{
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 5, 15);
				e.setCancelled(true);
				p.updateInventory();
				return;
			}
			if(chance(12.5f))
			{
				e.getBlock().setType(Material.AIR);
				e.getBlock().getLocation().subtract(0, 1, 0).getBlock().setType(Material.GRASS);
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 15);
			}
			if(type == Material.IRON_ORE)
			{
				if(chance(40))
				{
					int amount = 15;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.STONE, amount, p);
				}
				else if(chance(50))
				{
					int amount = 5;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.METAL_ORE, amount, p);
				}
				else
				{
					int amount = 1;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.HIGH_QUALITY_METAL_ORE, amount, p);
				}
			}
			else if(type == Material.GOLD_ORE)
			{
				if(chance(40))
				{
					int amount = 15;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.STONE, amount, p);
				}
				else if(chance(50))
				{
					int amount = 5;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.SULFUR_ORE, amount, p);
				}
				else
				{
					int amount = 1;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.HIGH_QUALITY_METAL_ORE, amount, p);
				}
			}
			else if(type == Material.DIAMOND_ORE)
			{
				if(chance(40))
				{
					int amount = 15;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.STONE, amount, p);
				}
				else if(chance(50))
				{
					int amount = 1;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.HIGH_QUALITY_METAL_ORE, amount, p);
				}
				else
				{
					int amount = 5;
					amount *= getPickBonus(p.getItemInHand());
					ItemIndex.giveItem(ItemType.METAL_ORE, amount, p);
				}
			}
			if(p.getItemInHand() != null && Util.isTool(p.getItemInHand().getType()))
				p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 2));
			e.setCancelled(true);
		}
		
		if(type == Material.LOG)
		{
			Player p = e.getPlayer();
			if(chance(12.5f))
			{
				Location loc = e.getBlock().getLocation();
				int y = 0;
				for(int y1 = loc.getBlockY(); new Location(loc.getWorld(), loc.getX(), y1, loc.getZ()).getBlock().getType() == Material.LOG; y1--)
				{
					y = y1;
				}
				breakTree(e.getBlock());
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 15);
			}
			int amount = 15;
			amount *= getAxeBonus(p.getItemInHand());
			ItemIndex.giveItem(ItemType.WOOD, amount, p);
			if(p.getItemInHand() != null && Util.isTool(p.getItemInHand().getType()))
				p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 2));
			e.setCancelled(true);
		}
	}
	
	public static Material[] building_blocks = new Material[]
	{
		Material.BEACON,
		Material.WOOD,
		Material.COBBLESTONE,
		Material.BRICK,
		Material.IRON_BLOCK
	};
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e)
	{
		for(Material mat : building_blocks)
		{
			if(e.getBlock().getType() == mat)
			{
				Location loc = e.getBlock().getLocation();
				Player p = e.getPlayer();
				if(Core.hasPlacedCore(p)|| e.getBlock().getType() == Material.BEACON)
				{
					if(Core.hasPlacedCore(p) && e.getBlock().getType() == Material.BEACON)
					{
						error(p, "You've already claimed the max amount of land (50)");
					}
					else
					{
						if(Core.isInRange(p, loc))
						{
							if(!Core.isInAnyRange(p, loc))
							{
								p.playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 8, 15);
								
								Tag.Tag(e.getBlock(), "owner", p.getName());
								Tag.Tag(e.getBlock(), "health", Health.getMaxHealth(e.getBlock().getType()) + "");
								
								if(e.getBlock().getType() == Material.BEACON)
									Tag.Tag(e.getBlock(), "core", "true");
								
								return;
							}
							else
							{
								error(p, "You are building blocked by another player here!");
							}
						}
						else
						{
							error(p, "You are not in range of your base!");
						}
					}
				}
				else
				{
					error(p, "You have not claimed any land yet!");
				}
			}
		}
		e.setCancelled(true);
		
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE && e.isCancelled())
			e.setCancelled(false);
	}
	
	private void error(Player p, String message)
	{
		ActionBarAPI.sendActionBar(p, ChatColor.DARK_RED + message, 100);
		p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 15);
	}
	
	public void breakTree(Block tree)
	{
		if(tree.getType() != Material.LOG && tree.getType() != Material.LEAVES)
			return;
		tree.setType(Material.AIR);
		for(BlockFace face: BlockFace.values())
			breakTree(tree.getRelative(face));
	}
	
	private int getPickBonus(ItemStack item)
	{
		if(item.getType() == Material.STONE_PICKAXE)
		{
			return 2;
		}
		else if(item.getType() == Material.IRON_PICKAXE)
		{
			return 3;
		}
		else if(item.getType() == Material.DIAMOND_PICKAXE)
		{
			return 4;
		}
		return 1;
	}
	
	private int getAxeBonus(ItemStack item)
	{
		if(item.getType() == Material.STONE_AXE)
		{
			return 2;
		}
		else if(item.getType() == Material.IRON_AXE)
		{
			return 3;
		}
		else if(item.getType() == Material.DIAMOND_AXE)
		{
			return 4;
		}
		return 1;
	}
	
	private boolean chance(float chance)
	{
		float f = r.nextFloat();
		return f <= (chance / 100);
	}
}
