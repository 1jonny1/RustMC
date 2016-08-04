package me.savant.blueprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.savant.rustmc.Config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Blueprint
{
	static Map<Player, Config> cache = new HashMap<Player, Config>();
	
	public static boolean hasBlueprint(Player p, Material material)
	{
		FileConfiguration fc = getConfig(p).getConfig();
		if(fc.contains("locked"))
		{
			List<String> ls = (List<String>) fc.getList("locked");
			if(ls.contains(material.toString()))
				return true;
		}
		return false;
	}
	
	public static void addBlueprint(Player p, Material material)
	{
		Config config = getConfig(p);
		FileConfiguration fc = config.getConfig();
		
		List<String> ls = new ArrayList<String>();
		if(fc.contains("locked"))
			ls = (List<String>) fc.getList("locked");
		ls.add(material.toString());
		fc.set("locked", ls);
		config.Save();
	}
	
	public static void clearBlueprints(Player p)
	{
		Config config = getConfig(p);
		FileConfiguration fc = config.getConfig();
		for(String key : fc.getKeys(false))
			fc.set(key, null);
		config.Save();
	}
	
	private static Config getConfig(Player p)
	{
		if(!cache.containsKey(p))
			cache.put(p, new Config("Blueprints/" + p.getName().toLowerCase()));
		return cache.get(p);
	}
}
