package me.savant.fire;

import java.util.Random;

import me.savant.rustmc.RustMC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Fire
{
	public static RustMC plugin;
	
	Location loc;
	int id = 0;
	Random random;
	
	public Fire(Location loc)
	{
		this.loc = loc;
		random = new Random();
	}
	
	public void start()
	{
		if(id == 0)
			schedule();
	}
	
	public void stop()
	{
		Bukkit.getScheduler().cancelTask(id);
		id = 0;
	}
	
	void schedule()
	{
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if(random.nextFloat() < 0.8)
					playSound(Sound.BLOCK_FIRE_AMBIENT, 1, 6);
				if(random.nextFloat() < 0.8)
					playSound(Sound.BLOCK_FIRE_AMBIENT, 1, 4);
				if(random.nextFloat() < 0.3)
				{
					if(random.nextBoolean())
					{
						playSound(Sound.ENTITY_TNT_PRIMED, 0.3f, 15);
					}
					else
					{
						playSound(Sound.ENTITY_CREEPER_PRIMED, 0.1f, 45);
					}
				}
			}
		}, 10L, 10L);
	}
	
	void playSound(Sound sound, float pitch, float volume)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.playSound(loc, sound, pitch, volume);
		}
	}
	
	
}
