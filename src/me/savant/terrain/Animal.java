package me.savant.terrain;

import me.savant.items.ItemIndex;
import me.savant.items.ItemType;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Animal implements Listener
{
	@EventHandler
	public void onDeath(EntityDeathEvent e)
	{
		if(e.getEntityType() == EntityType.CHICKEN)
		{
			e.getDrops().clear();
			e.getDrops().add(ItemIndex.getItem(ItemType.CHICKEN, 4));
			e.setDroppedExp(0);
		}
		if(e.getEntityType() == EntityType.HORSE)
		{
			e.getDrops().clear();
			e.getDrops().add(ItemIndex.getItem(ItemType.CHICKEN, 8));
			e.setDroppedExp(0);
		}
		if(e.getEntityType() == EntityType.COW)
		{
			e.getDrops().clear();
			e.getDrops().add(ItemIndex.getItem(ItemType.BEEF, 10));
			e.setDroppedExp(0);
		}
		if(e.getEntityType() == EntityType.PIG)
		{
			e.getDrops().clear();
			e.getDrops().add(ItemIndex.getItem(ItemType.PORK, 6));
			e.setDroppedExp(0);
		}
	}
}
