package me.savant.crafting;

public class ResourceEntry
{
	String unit;
	Resource resource;
	int amount;
	
	public ResourceEntry(String unit, Resource resource, int amount)
	{
		this.unit = unit;
		this.resource = resource;
		this.amount = amount;
	}

	public String getUnit()
	{
		return unit;
	}

	public Resource getResource()
	{
		return resource;
	}

	public int getAmount()
	{
		return amount;
	}
}
