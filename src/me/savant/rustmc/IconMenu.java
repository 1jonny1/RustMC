package me.savant.rustmc;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.plugin.Plugin;

public class IconMenu implements Listener
{
	private String name;
    private int size;
    private OptionClickEventHandler handler;
    private RustMC plugin;
    
    private String[] optionNames;
    private ItemStack[] optionIcons;
   
    private Chest saveInventory;
    private FurnaceInstance furnaceInstance;
    
    public IconMenu(String name, int size, OptionClickEventHandler handler, RustMC plugin, Chest saveInventory, FurnaceInstance furnaceInstance)
    {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        this.saveInventory = saveInventory;
        this.furnaceInstance = furnaceInstance;
        plugin.registerEvents(this);
    }
    
    public IconMenu setContents(ItemStack[] contents)
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
    	return this;
    }
   
    public IconMenu setOption(int position, ItemStack icon, String name, String... info)
    {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, Arrays.asList(info));
        return this;
    }
    
    public IconMenu setOption(int position, ItemStack icon)
    {
    	if(icon.getType() != Material.AIR)
    	{
    		optionNames[position] = icon.getItemMeta().getDisplayName();
    		optionIcons[position] = setItemNameAndLore(icon, icon.getItemMeta().getDisplayName(), icon.getItemMeta().getLore());
    	}
    	else
    	{
    		optionIcons[position] = icon;
    	}
        return this;
    }
   
    public void open(Player player)
    {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++)
        {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }
   
    public void destroy()
    {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }
   
    @EventHandler
    void onInventoryClose(InventoryCloseEvent e)
    {
    	if(e.getInventory().getTitle().equals(name))
    	{
    		saveInventory.getInventory().setContents(e.getInventory().getContents());
    		saveInventory.getBlockInventory().setContents(e.getInventory().getContents());
    		((Player)e.getPlayer()).playSound(((Player)e.getPlayer()).getLocation(), Sound.LEVEL_UP, 1, 15);
    		furnaceInstance.activate();
    	}
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getInventory().getTitle().equals(name))
        {
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null)
            {
                Plugin plugin = this.plugin;
                ItemStack item = event.getCurrentItem();
                String name = item.getItemMeta().getDisplayName();
                OptionClickEvent e = new OptionClickEvent((Player)event.getWhoClicked(), slot, name, item);
                handler.onOptionClick(e);
                if (e.willClose())
                {
                    final Player p = (Player)event.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                    {
                        public void run()
                        {
                            p.closeInventory();
                        }
                    }, 1);
                }
                if (e.willDestroy())
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
        private boolean close;
        private boolean destroy;
        private ItemStack item;
        
        public OptionClickEvent(Player player, int position, String name, ItemStack item)
        {
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = false;
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
       
        public boolean willClose()
        {
            return close;
        }
       
        public boolean willDestroy()
        {
            return destroy;
        }
       
        public void setWillClose(boolean close)
        {
            this.close = close;
        }
       
        public void setWillDestroy(boolean destroy)
        {
            this.destroy = destroy;
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