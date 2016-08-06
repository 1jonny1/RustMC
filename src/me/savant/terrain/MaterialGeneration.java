package me.savant.terrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.savant.rustmc.Schematic;
import me.savant.rustmc.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MaterialGeneration
{
	public int size = 500;
	public int level = 32;
	public Terrain terrain;
	
	private World world;
	private Random r;
	
	public MaterialGeneration()
	{
		world = Bukkit.getServer().getWorld("flat");
	}
	
	public void update()
	{
		Util.sendServerMessage("Starting automatic terrain refill! (You may experience lag)");
		
		final long startTime = System.currentTimeMillis();
		
		terrain = new Terrain();
		r = new Random();
		r.setSeed(terrain.getSeed());
		
		oreGeneration();
		treeGeneration();
		barrelGeneration();
		hempGeneration();
		cleanupEntities();		
		
		long duration = System.currentTimeMillis() - startTime;
		Util.sendStaffMessage("Automatic Terrain Refill finished... Took " + duration + "ms");
	}
	
	public void updateAll()
	{
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("RustMC"), new Runnable()
		{
			@Override
			public void run()
			{
				Util.sendServerMessage("Full Terrain Reset in progress. You " + ChatColor.BOLD + "WILL" + ChatColor.RESET + "" + ChatColor.GOLD + " experience major lag.");
				
				final long startTime = System.currentTimeMillis();
				
				terrain = new Terrain();
				r = new Random();
				r.setSeed(terrain.getSeed());
				
				Util.sendServerMessage("0%");
				reset();
				Util.sendServerMessage("10%");
				terrainGeneration();
				Util.sendServerMessage("20%");
				naturalize();
				Util.sendServerMessage("30%");
				oreGeneration();
				Util.sendServerMessage("40%");
				treeGeneration();
				Util.sendServerMessage("50%");
				barrelGeneration();
				Util.sendServerMessage("60%");
				hempGeneration();
				Util.sendServerMessage("70%");
				buildingGeneration();
				Util.sendServerMessage("80%");
				grassalize();
				Util.sendServerMessage("90%");
				cleanupEntities();
				Util.sendServerMessage("100%");
				
				long duration = System.currentTimeMillis() - startTime;
				Util.sendStaffMessage("Terrain Generation finished.. Took " + duration + "ms");
			}
		});
	}
	
	public long reset()
	{
		long startTime = System.currentTimeMillis();
		
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				for(int y = 60; y > 1; y--)
				{
					new Location(world, x, y, z).getBlock().setType(Material.AIR);
				}
			}
		}
		
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				new Location(world, x, level - 1, z).getBlock().setType(Material.GRASS);
			}
		}
		cleanupEntities();
		
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}
	
	private void cleanupEntities()
	{
		for(Entity entity : world.getEntities())
		{
			entity.remove();
		}
	}
	
	private void buildingGeneration()
	{
		int buildingAmount = 2;
		List<Location> previousLocations = new ArrayList<Location>();
		for(int i = 1; i <= buildingAmount; i++)
		{
			try
			{
				Schematic bld = Schematic.parseSchematic("building_" + i);
				
				Location loc = randomLocation();
				int iteration = 0;
				while(!canPlace(bld, loc, previousLocations))
				{
					loc = randomLocation();
					System.out.println(loc.toVector().toString());
					iteration++;
					if(iteration > 30)
					{
						System.out.println("[RustMC] Cannot find suitable location for building_" + i);
						return;
					}
				}
				bld.pasteInstant(world, loc);
				previousLocations.add(loc);
			}
			catch (IOException e)
			{
				System.out.println("[RustMC] Cannot find building_" + i);
				e.printStackTrace();
			}
		}
	}
	
	private boolean canPlace(Schematic schematic, Location loc, List<Location> previousLocations)
	{
		int distanceBetweenBuildings = size / 3;
		if(schematic.canPlace(loc))
		{
			for(Location loc2 : previousLocations)
			{
				if(Math.abs(loc.distance(loc2)) < distanceBetweenBuildings)
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
		
	private Location randomLocation()
	{
		int x = Util.range(1, 499);
		int z = Util.range(1, 499);
		int y = world.getHighestBlockYAt(x, z);
		return new Location(world, x, y, z);
	}
	
	private void terrainGeneration()
	{
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				int height = level + (int) terrain.generateHeight(x, z);
				for(int y = level; y < height ; y++)
				{
					new Location(world, x, y, z).getBlock().setType(Material.DIRT);
				}
				for(int y = height; y < world.getMaxHeight(); y++)
				{
					new Location(world, x, y, z).getBlock().setType(Material.AIR);
				}
			}
		}
	}
	
	private void naturalize()
	{
		int maxStoneDepth = 5;
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				int height = world.getHighestBlockYAt(x, z);
				for(int y = height; y > 1; y--)
				{
					if(height - y == 0)
					{
						new Location(world, x, height, z).getBlock().setType(Material.GRASS);
					}
					else if(height - y < 5)
					{
						new Location(world, x, y, z).getBlock().setType(Material.DIRT);
					}
					else if(height - y < maxStoneDepth)
					{
						new Location(world, x, y, z).getBlock().setType(Material.STONE);
					}
					else
					{
						continue;
					}
				}
			}
		}
	}
	
	private void grassalize()
	{
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				if(chance(35))
				{
					Block block = new Location(world, x, world.getHighestBlockYAt(x, z), z).getBlock();
					if(block.getType() == Material.AIR && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS)
					{
						block.setTypeIdAndData(31, (byte) 1, true);	
					}
				}
			}
		}
	}
	
	private void oreGeneration()
	{
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				if(chance(0.085f))
				{
					Material mat = randomOre();
					Block block = new Location(world, x, world.getHighestBlockYAt(x, z), z).getBlock();
					if(block.getType() == Material.AIR && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS)
					{
						block.setType(mat);
					}
				}
			}
		}
	}
	
	private void treeGeneration()
	{
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				if(chance(0.175f))
				{
					Location loc = new Location(world, x, world.getHighestBlockYAt(x, z), z);
					if(loc.getBlock().getType() == Material.AIR)
					{
						world.generateTree(loc, TreeType.TREE);
					}
				}
			}
		}
	}
	
	private void barrelGeneration()
	{
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				if(chance(0.015f))
				{
					Block block = new Location(world, x, world.getHighestBlockYAt(x, z), z).getBlock();
					if(block.getType() == Material.AIR && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS)
					{
						block.setType(Material.BOOKSHELF);
					}
				}
			}
		}
	}
	
	private void hempGeneration()
	{
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				if(chance(0.06f))
				{
					Block block = new Location(world, x, world.getHighestBlockYAt(x, z), z).getBlock();
					if(block.getType() == Material.AIR && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS)
					{
						block.setTypeIdAndData(175, (byte) 5, true);
						block.getRelative(BlockFace.UP).setTypeIdAndData(175, (byte) 8, true);
					}
				}
			}
		}
	}
	
	private Material randomOre()
	{
		if(chance(50))
		{
			return Material.IRON_ORE;
		}
		else if(chance(70))
		{
			return Material.GOLD_ORE;
		}
		else
		{
			return Material.DIAMOND_ORE;
		}
	}
	
	private boolean chance(float chance)
	{
		float f = r.nextFloat();
		return f <= (chance / 100);
	}
}
