package me.savant.building;

import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosiveDamage implements Listener
{
	@EventHandler
	public void onTNT(EntityExplodeEvent e)
	{
		for(Block b : e.blockList())
		{
			Health.damage(b, Health.EXPLOSIVE_DAMAGE);
			b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
		}
		e.setCancelled(true);
	}
}
