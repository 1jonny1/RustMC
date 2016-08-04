package me.savant.crafting;

import me.savant.items.Item;
import me.savant.items.ItemIndex;
import me.savant.items.ItemType;

import org.bukkit.entity.Player;

public class ResourceCost
{
	ResourceEntry[] resource;
	
	public ResourceCost(ResourceEntry... resources)
	{
		this.resource = resources;
	}
	
	public ResourceEntry[] getEntrys()
	{
		return resource;
	}
	
	public void takeResources(Player p)
	{
		for(ResourceEntry entry : resource)
		{
			Resource resource = entry.getResource();
			if(resource.isItem())
			{
				Item item = (Item) resource.getResource();
				item.removeItem(p, entry.getAmount());
			}
			else
			{
				ItemType item = (ItemType) resource.getResource();
				ItemIndex.removeItem(ItemIndex.getType(item), entry.getAmount(), p.getInventory());
			}
		}
	}
	
	public boolean hasResources(Player p)
	{
		for(ResourceEntry entry : resource)
		{
			Resource resource = entry.getResource();
			if(resource.isItem())
			{
				Item item = (Item) resource.getResource();
				int amount = ItemIndex.getAmount(item.getItem().getType(), p.getInventory());
				if(amount < entry.getAmount())
				{
					return false;
				}
			}
			else
			{
				ItemType type = (ItemType) resource.getResource();
				int amount = ItemIndex.getAmount(ItemIndex.getType(type), p.getInventory());
				if(amount < entry.getAmount())
				{
					return false;
				}
			}
		}
		return true;
	}
}
