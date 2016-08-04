package me.savant.rustmc;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
	public static RustMC plugin;
	
	private FileConfiguration fc;
	private String n;
	private File file;
	
	public FileConfiguration getConfig()
	{
		return fc;
	}
	
	public Config(String n)
	{
		this.n = n;
		this.file = new File(n + ".yml");
		this.fc = YamlConfiguration.loadConfiguration(file);
		Save();
	}
	
	public String getName()
	{
		return n;
	}
	
	public void Save()
	{
		try
		{
			fc.save(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
}
