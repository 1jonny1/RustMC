package me.savant.building;

import java.util.ArrayList;
import java.util.List;

import me.savant.rustmc.Config;
import me.savant.rustmc.RustMC;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

public class DataManager
{
	List<Block> queue = new ArrayList<Block>();
	List<Block> modified = new ArrayList<Block>();
	RustMC plugin;
	Config save = new Config("block-data");
	World world;
	
	String[] saveValues = new String[]
	{
		"owner",
		"health",
		"core",
		"health"
	};
	
	public DataManager(World world, RustMC plugin)
	{
		this.world = world;
		this.plugin = plugin;
		new Config(world.getName() + "-" + "block-data");
	}
	
	public void tag(Block b, String key, String value)
	{
		b.setMetadata(key, new FixedMetadataValue(plugin, value));
		queue(b);
	}
	
	public List<Block> getData()
	{
		return modified;
	}
	
	public void queue(Block b)
	{
		modified.add(b);
		queue.add(b);
	}
	
	public void download()
	{
		System.out.println("=====================================");
		System.out.println("[RustMC] Downloading world data...");
		System.out.println("=====================================");
		for(String path : save.getConfig().getKeys(false))
		{
			for(String key : save.getConfig().getConfigurationSection(path).getKeys(false))
			{
				String value = save.getConfig().getString(path + "." + key);
				System.out.println(path + " . " + key + " : " + value);
				Block b = toBlock(path);
				b.setMetadata(key, new FixedMetadataValue(plugin, value));
				modified.add(b);
			}
		}
		System.out.println("=====================================");
		System.out.println("[RustMC] Finished!");
		System.out.println("=====================================");
		
	}
	
	public void save()
	{
		System.out.println("=====================================");
		System.out.println("[RustMC] Saving block metadata to server...");
		System.out.println("=====================================");
		for(Block b : queue)
		{
			System.out.println("...");
			String path = toString(b);
			for(String value : saveValues)
			{
				if(b.getMetadata(value) != null && !b.getMetadata(value).isEmpty())
				{
					save.getConfig().set(path + "." + value, String.valueOf(b.getMetadata(value).get(0).asString()));
				}
				else if(save.getConfig().getString(path + "." + value) != null || save.getConfig().getString(path + "." + value) != "")
				{
					save.getConfig().set(path, null);
				}
			}
		}
		save.Save();
		queue.clear();
		System.out.println("=====================================");
		System.out.println("[RustMC] Finished!");
		System.out.println("=====================================");
	}
	
	private Block toBlock(String s)
	{
		String[] params = s.split("=");
		int x = Integer.parseInt(params[0]);
		int y = Integer.parseInt(params[1]);
		int z = Integer.parseInt(params[2]);
		return new Location(world, x, y, z).getBlock();
	}
	
	private String toString(Block b)
	{
		Location l = b.getLocation();
		return format(l.getBlockX()) + "=" + format(l.getBlockY()) + "=" + format(l.getBlockZ());
	}
	
	private String format(int i)
	{
		String s = i + "";
		if(s.contains("."))
			s = s.split(".")[0];
		return s;
	}
	
	
}
