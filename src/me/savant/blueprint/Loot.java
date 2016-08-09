package me.savant.blueprint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.savant.furnace.FurnaceMenu;
import me.savant.items.Item;
import me.savant.items.ItemIndex;
import me.savant.items.ItemType;
import me.savant.rustmc.RustMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class Loot implements Listener
{
	private RustMC plugin;
	
	private String name;
    private int size;
    private OptionClickEventHandler handler;
    
    private String[] optionNames;
    private ItemStack[] optionIcons;
        
    private Block block;
    
    private boolean willCancel;
    private boolean doDestroy;
    
    private Random random;
    
    public Loot(Block block, RustMC plugin)
    {
    	this.name = ChatColor.BOLD + "" + "Loot";
    	this.size = 9;
    	this.block = block;
    	this.doDestroy = true;
    	this.plugin = plugin;
    	
    	this.random = new Random();
    	
    	plugin.registerEvents(this);
    	
    	//TODO: Add Blueprint Database
    	this.handler = new OptionClickEventHandler()
    	{
			@Override
			public void onOptionClick(OptionClickEvent e)
			{
				if(ChatColor.stripColor(e.getName()).contains("Blueprint"))
				{
					setCancelled(true);
				}
			}
    	};
    	
    	optionIcons = new ItemStack[size];
    	optionNames = new String[size];
    }
    
    //Will the block get destoryed on close?
    public void setWillDestroy(boolean value)
    {
    	this.doDestroy = value;
    }
    
    public boolean willDestroy()
    {
    	return this.doDestroy;
    }
    
    //TODO: Resets items
    public void setSize(int size)
    {
    	this.size = size;
    	optionIcons = new ItemStack[size];
    	optionNames = new String[size];
    }
    
    public void setContents(ItemStack[] contents)
    {
    	optionIcons = new ItemStack[size];
    	optionNames = new String[size];
    	int i = 0;
    	for(ItemStack item : contents)
    	{
    		if(item == null)
    			item = new ItemStack(Material.AIR, 1);
    		setOption(i, item);
    		i++;
    	}
    }
    
    public void randomizeLoot()
    {
    	Random rand = new Random();
    	
    	int amountOfItems = 3;
    	
    	int minAmount = 5;
    	int maxAmount = 15;
    	
    	List<ItemType> items = new ArrayList<ItemType>();
    	items.add(ItemType.SULFUR);
    	items.add(ItemType.STONE);
    	items.add(ItemType.METAL_ORE);
    	
    	for(int x = 0; x < amountOfItems; x++)
    	{
        	int amount = rand.nextInt((maxAmount - minAmount) + 1) + minAmount;
        	ItemStack item = ItemIndex.getItem(items.get(rand.nextInt(items.size())), amount);
        	
        	String[] info = new String[]{};
        	if(item.getItemMeta().getLore() != null)
        		info = item.getItemMeta().getLore().toArray(new String[item.getItemMeta().getLore().size()]);
        	
        	setOption(x, item, item.getItemMeta().getDisplayName(), info);
    	}
    	if(chance(35f))
    	{
    		Item[] locked = Item.getLockedItems();
    		Item item = locked[random.nextInt(locked.length)];
    		ItemStack is = item.getBlueprintItem();
    		
    		setOption(
    				amountOfItems,
    				is,
    				is.getItemMeta().getDisplayName(),
    				is.getItemMeta().getLore().toArray(new String[is.getItemMeta().getLore().size()])
    		);
    	}
    }
    
	private boolean chance(float chance)
	{
		float f = random.nextFloat();
		return f <= (chance / 100);
	}
    
    public Loot(String name, int size, OptionClickEventHandler handler, RustMC plugin)
    {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.willCancel = false;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.registerEvents(this);
    }
   
    public Loot setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, Arrays.asList(info));
        return this;
    }
    
    public Loot setOption(int position, ItemStack icon)
    {
        optionNames[position] = icon.getItemMeta().getDisplayName();
        optionIcons[position] = setItemNameAndLore(icon, icon.getItemMeta().getDisplayName(), icon.getItemMeta().getLore());
        return this;
    }
   
    public void open(Player player)
    {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++)
        {
            if (optionIcons[i] != null)
            {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }
    
    public boolean willCancel()
    {
    	return willCancel;
    }
    
    public void setCancelled(boolean value)
    {
    	this.willCancel = value;
    }
    
    public void destroy()
    {
		block.setType(Material.AIR);
		block.getLocation().add(0, -1, 0).getBlock().setType(Material.GRASS);
        HandlerList.unregisterAll(this);
        handler = null;
        optionNames = null;
        optionIcons = null;
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getInventory().getTitle().equals(name))
        {
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null)
            {
                ItemStack item = event.getCurrentItem();
                if(item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null)
                	return;
                String name = item.getItemMeta().getDisplayName();
                OptionClickEvent e = new OptionClickEvent((Player)event.getWhoClicked(), slot, name, item);
                handler.onOptionClick(e);
                event.setCancelled(willCancel());
            }
        }
    }
    
    @EventHandler
    void onInventoryClose(InventoryCloseEvent e)
    {
    	if(e.getInventory().getTitle().equals(name))
    	{
    		if(willDestroy())
    		{
    			destroy();
    		}
    		else
    		{
    			boolean doContinue = false;
    			for(ItemStack item : optionIcons)
    			{
    				if(item != null && item.getAmount() != 0)
    				{
    					doContinue = true;
    				}
    			}
    			if(!doContinue)
    			{
    				destroy();
    			}
    		}
    	}
    }
   
    public interface OptionClickEventHandler
    {
        public void onOptionClick(OptionClickEvent event);      
    }
   
    public class OptionClickEvent
    {
        private Player player;
        private int position;
        private String name;
        private ItemStack item;
        
        public OptionClickEvent(Player player, int position, String name, ItemStack item)
        {
            this.player = player;
            this.position = position;
            this.name = name;
            this.item = item;
        }
        
        public ItemStack getClicked()
        {
        	return item;
        }
       
        public Player getPlayer()
        {
            return player;
        }
       
        public int getPosition()
        {
            return position;
        }
       
        public String getName()
        {
            return name;
        }
    }
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, List<String> lore)
    {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }
}
