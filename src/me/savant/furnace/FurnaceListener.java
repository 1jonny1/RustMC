package me.savant.furnace;

import java.util.HashSet;

import me.savant.items.ItemIndex;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceListener implements Listener
{
	final Short COOK_TIME = (short) 190;
	
	@EventHandler
	public void furnaceBurn(FurnaceBurnEvent e)
	{
		Furnace furnace = (Furnace)e.getBlock().getState();
		furnace.setCookTime(COOK_TIME);
	}
	
	@EventHandler
	public void furnaceSmeltEvent(FurnaceSmeltEvent e)
	{
		if(ItemIndex.getType(e.getResult().getType()) != null)
		{
			e.setResult(ItemIndex.getItem(ItemIndex.getType(e.getResult().getType()), 1));
		}
		else
		{
			e.setResult(new ItemStack(Material.AIR, 0));
		}
		Furnace furnace = (Furnace)e.getBlock().getState();
		furnace.setCookTime(COOK_TIME);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
        Block blocktype = e.getWhoClicked().getTargetBlock((HashSet<Byte>)null, 10);   
        
        if (blocktype.getType() == Material.FURNACE || blocktype.getType() == Material.BURNING_FURNACE) {
            if ((e.getSlot() == 0 || e.getSlot() == 1) && e.getCursor().getType() != Material.AIR) {
                Furnace furnace = (Furnace) blocktype.getState();
                furnace.setCookTime(COOK_TIME);
            }
        }
	}
}
