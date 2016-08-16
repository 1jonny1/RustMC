package me.savant.building;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Tag
{
	public static DataManager dm;
	
	public static boolean is(Block b, String key)
	{
		if(b.getMetadata(key) != null)
		{
			if(!b.getMetadata(key).isEmpty())
			{
				return true;
			}
		}
		return false;
	}
	
	public static void delete(Block b)
	{
		if(b.getType() != Material.AIR)
			b.setType(Material.AIR);
		
		b.getWorld().playEffect(b.getLocation(), Effect.CLOUD, 20);

		for(String key : dm.saveValues)
		{
			if(b.getMetadata(key) != null && !b.getMetadata(key).isEmpty())
			{
				b.removeMetadata(key, dm.plugin);
			}
		}
		dm.modified.clear();
		dm.queue.add(b);
		dm.save();
		dm.download();
	}
	
	public static String getValue(Block b, String key)
	{
		return b.getMetadata(key).get(0).asString();
	}
	
	public static void Tag(Block b, String key, String value)
	{
		dm.tag(b, key, value);
	}
}
