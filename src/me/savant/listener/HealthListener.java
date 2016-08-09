package me.savant.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class HealthListener
{
	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent e)
	{
		if(e.getRegainReason() == RegainReason.SATIATED || e.getRegainReason() == RegainReason.REGEN)
		{
			e.setCancelled(true);
		}
	}
}
