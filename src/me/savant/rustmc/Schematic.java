package me.savant.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import me.savant.jnbt.ByteArrayTag;
import me.savant.jnbt.CompoundTag;
import me.savant.jnbt.NBTInputStream;
import me.savant.jnbt.ShortTag;
import me.savant.jnbt.Tag;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Schematic {
	private short[] blocks;
	private byte[] data;
	private short width;
	private short length;
	private short height;
	private String name;
	
	public Schematic(short[] blocks, byte[] data, short width, short length, short height, String name) {
		this.blocks = blocks;
		this.data = data;
		this.width = width;
		this.length = length;
		this.height = height;
		this.name = name;
	}

	/**
	 * @return the blocks
	 */
	public short[] getBlocks() {
		return blocks;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return the width
	 */
	public short getWidth() {
		return width;
	}

	/**
	 * @return the length
	 */
	public short getLength() {
		return length;
	}

	/**
	 * @return the height
	 */
	public short getHeight() {
		return height;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return if can place at @param origin blacklisting @param placed
	 */
	public boolean canPlace(Location origin, org.bukkit.Material placed)
	{
		origin = Center(origin, length, width);
		int i = 0;
		int count = 0;
		for(int x = (int)origin.getX(); x < (int) origin.getX() + length; x++)
		{
			for(int y = (int)origin.getY(); y < (int) origin.getY() + height; y++)
			{
				for(int z = (int)origin.getZ(); z < (int) origin.getZ() + width; z++)
				{
					Location place = new Location(origin.getWorld(), x, y, z);
					if(place.getBlock().getType() == org.bukkit.Material.AIR || place.getBlock().getType() == placed)
					{
						count++;
					}
					i++;
				}
			}
		}
		return i == count;
	}
	
	/**
	 * @return schematic parsed from @param string
	 * @throws IOException
	 */
	public static Schematic parseSchematic(String string) throws IOException
	{
		if(new File("plugins/WorldEdit/schematics", string + ".schematic").exists())
			return Schematic.loadSchematic(new File("plugins/WorldEdit/schematics", string + ".schematic"));
		return null;
	}

	/**
	 * 
	 * @param world
	 * @param loc
	 * @param schematic
	 * @param p
	 * @return Time Operation Took
	 */
	public static long pasteSchematic(final World world, final Location loc, Schematic schematic, final Player p) {
		loc.getBlock().setType(Material.AIR);
		final int i = 2;
		final short[] blocks = schematic.getBlocks();
		final byte[] blockData = schematic.getData();

		final short length = schematic.getLength();
		final short width = schematic.getWidth();
		short height = schematic.getHeight();
		
		for (int y = 0; y < height; y++)
		{
			final int fY = y;
			for (int x = 0; x < width; x++) 
			{
				final int fX = x;
				for (int z = 0; z < length; z++) 
				{
					final int fZ = z;
						Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("AdvancedBasev2"),new Runnable(){
							@SuppressWarnings("deprecation")
							public void run(){
								int index = fY * width * length + fZ * width + fX;
								Block block = new Location(world, fX + loc.getX(), fY + loc.getY(), fZ + loc.getZ()).getBlock();
								block.setTypeIdAndData(blocks[index], blockData[index], false);
								p.playEffect(block.getLocation(), Effect.ZOMBIE_CHEW_WOODEN_DOOR, 15);
							}
						},i * x + i * y + i * z);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("AdvancedBasev2"), new Runnable()
						{
							public void run() {
								p.playEffect(p.getLocation(), Effect.RECORD_PLAY, 15);
							}
						}, i * length + i * width + i * height);
					//int index = fY * width * length + fZ * width + fX;
					//Block block = new Location(world, fX + loc.getX(), fY + loc.getY(), fZ + loc.getZ()).getBlock();
					//block.setTypeIdAndData(blocks[index], blockData[index], false);

				}
			}
		}
		return i * width + i * height + i * length;
	}
	
	/**
	 * @return returns loaded Schematic from @param file
	 * @throws IOException
	 */
	public static Schematic loadSchematic(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		@SuppressWarnings("resource")
		NBTInputStream nbtStream = new NBTInputStream(stream);
		CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
		if (!schematicTag.getName().equals("Schematic")) {
			throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
		}
		
		Map<String, Tag> schematic = schematicTag.getValue();
		if (!schematic.containsKey("Blocks")) {
			throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
		}
		
		short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
		short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
		short height = getChildTag(schematic, "Height", ShortTag.class).getValue();


        // Get blocks
        byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length]; // Have to later combine IDs

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (schematic.containsKey("AddBlocks")) {
            addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
        }

        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < blockId.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (blockId[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                }
            }
        }
		return new Schematic(blocks, blockData, width, length, height, file.getName().toLowerCase().replace(".schematic", ""));
	}
	
	/**
	 * 
	 * @param origin
	 * @param placed
	 * @param width
	 * @param length
	 * @param height
	 * @return if can place schematic at location
	 */
	public static boolean canPlace(Location origin, org.bukkit.Material placed, int width, int length, int height)
	{
		origin = Center(origin, length, width);
		int i = 0;
		int count = 0;
		for(int x = (int)origin.getX(); x < (int) origin.getX() + length; x++)
		{
			for(int y = (int)origin.getY(); y < (int) origin.getY() + height; y++)
			{
				for(int z = (int)origin.getZ(); z < (int) origin.getZ() + width; z++)
				{
					Location place = new Location(origin.getWorld(), x, y, z);
					if(place.getBlock().getType() == org.bukkit.Material.AIR || place.getBlock().getType() == placed)
					{
						count++;
					}
					i++;
				}
			}
		}
		return i == count;
	}
	
	public static Location Center(Location pos, int length, int width)
	{
		Location loc = pos.clone();
		loc.setX((int)loc.getX() - (int)(length / 2));
		loc.setZ((int)loc.getZ() - (int)(width / 2));
		return loc;
	}

	/**
	 * Get child tag of a NBT structure.
	 * 
	 * @param items
	 *            The parent tag map
	 * @param key
	 *            The name of the tag to get
	 * @param expected
	 *            The expected type of the tag
	 * @return child tag casted to the expected type
	 * @throws DataException
	 *             if the tag does not exist or the tag is not of the expected
	 *             type
	 */
	private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException 
	{
		if (!items.containsKey(key)) 
		{
			throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
		}
		Tag tag = items.get(key);
		if (!expected.isInstance(tag)) 
		{
			throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
		}
		return expected.cast(tag);
	}
}
