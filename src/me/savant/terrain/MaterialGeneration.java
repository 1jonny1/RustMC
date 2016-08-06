package me.savant.terrain;

import java.util.Random;

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
	private long seed = 123456789L;
	private Random r;
	private long duration;
	private int loading;
	
	public MaterialGeneration()
	{
		world = Bukkit.getServer().getWorld("flat");
	}
	
	public void update()
	{
		final long startTime = System.currentTimeMillis();
		
		terrain = new Terrain();
		r = new Random();
		r.setSeed(terrain.getSeed());
		
		duration = 0L;
		
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("RustMC"), new Runnable()
		{
			@Override
			public void run()
			{
				oreGeneration();
				treeGeneration();
				barrelGeneration();
				hempGeneration();
				cleanupEntities();
				
				//Ended
				long endTime = System.currentTimeMillis();
				duration = endTime - startTime;
			}
		});
		loading = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("RustMC"), new Runnable()
		{
			@Override
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(p.isOp() || p.hasPermission("admin") || p.hasPermission("staff"))
					{
						if(duration != 0)
						{
							p.sendMessage(ChatColor.GRAY + "Ended... Took " + duration + "ms");
							Bukkit.getScheduler().cancelTask(loading);
						}
						else
						{
							p.sendMessage(ChatColor.GRAY + "Progressing...");
						}
					}
				}
			}
		}, 0L, 60L);
	}
	
	public void updateAll()
	{
		final long startTime = System.currentTimeMillis();
		
		terrain = new Terrain();
		r = new Random();
		r.setSeed(terrain.getSeed());
		
		duration = 0L;
		
		//New Threading
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("RustMC"), new Runnable()
		{
			@Override
			public void run()
			{
				terrainGeneration();
				naturalize();
				oreGeneration();
				treeGeneration();
				barrelGeneration();
				hempGeneration();
				grassalize();
				
				//Ended
				long endTime = System.currentTimeMillis();
				duration = endTime - startTime;
			}
		});
		loading = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("RustMC"), new Runnable()
		{
			@Override
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(p.isOp() || p.hasPermission("admin") || p.hasPermission("staff"))
					{
						if(duration != 0)
						{
							p.sendMessage(ChatColor.GRAY + "Ended... Took " + duration + "ms");
							Bukkit.getScheduler().cancelTask(loading);
						}
						else
						{
							p.sendMessage(ChatColor.GRAY + "Progressing...");
						}
					}
				}
			}
		}, 0L, 60L);
	}
	
	public long reset()
	{
		long startTime = System.currentTimeMillis();
		
		for(int x = 0; x < size; x++)
		{
			for(int z = 0; z < size; z++)
			{
				for(int y = level; y < 256; y++)
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
