package me.savant.crafting;

import java.util.Arrays;
import java.util.List;

import me.savant.rustmc.RustMC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CraftingMenu implements Listener
{
	private String name;
    private int size;
    private OptionClickEventHandler handler;
    private RustMC plugin;
    
    private String[] optionNames;
    private ItemStack[] optionIcons;
   
    public CraftingMenu(String name, int size, OptionClickEventHandler handler, RustMC plugin)
    {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.registerEvents(this);
    }
   
    public CraftingMenu setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, Arrays.asList(info));
        return this;
    }
    
    public CraftingMenu setOption(int position, ItemStack icon)
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
   
    public void destroy()
    {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getInventory().getTitle().equals(name))
        {
            event.setCancelled(true);
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
