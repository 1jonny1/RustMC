package me.savant.crafting;

import me.savant.items.Item;
import me.savant.items.ItemType;

public class Resource
{
	Item item;
	ItemType type;
	
	public Resource(Item item)
	{
		this.item = item;
	}
	
	public Resource(ItemType type)
	{
		this.type = type;
	}
	
	public Object getResource()
	{
		if(item != null)
			return item;
		return type;
	}
	
	public boolean isItem()
	{
		return type == null;
	}
}
