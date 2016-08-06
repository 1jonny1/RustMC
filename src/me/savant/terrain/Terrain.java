package me.savant.terrain;

import java.util.Random;

import me.savant.rustmc.Config;

import org.bukkit.configuration.file.FileConfiguration;

public class Terrain
{
	public final int seedLength = 99999;
	
	public float AMPLITUDE;
	public int OCTAVES;
	public float ROUGHNESS;
	
	private Config cfgTerrain;
	private FileConfiguration config;
	
	public void setRoughness(float roughness)
	{
		ROUGHNESS = roughness;
		config.set("roughness", Double.valueOf(ROUGHNESS));
		cfgTerrain.Save();
	}
	
	public float getRoughness()
	{
		if(!config.contains("roughness"))
		{
			config.set("roughness", Double.valueOf(0.1f));
			cfgTerrain.Save();
		}
		return (float) config.getDouble("roughness");
	}
	
	public void setOctaves(int octaves)
	{
		OCTAVES = octaves;
		config.set("octaves", Integer.valueOf(OCTAVES));
		cfgTerrain.Save();
	}
	
	public int getOctaves()
	{
		if(!config.contains("octaves"))
		{
			config.set("octaves", Integer.valueOf(4));
			cfgTerrain.Save();
		}
		return config.getInt("octaves");
	}
	
	public void setAmplitude(float amplitude)
	{
		AMPLITUDE = amplitude;
		config.set("amplitude", Double.valueOf(AMPLITUDE));
		cfgTerrain.Save();
	}
	
	public float getAmplitude()
	{
		if(!config.contains("amplitude"))
		{
			config.set("amplitude", Double.valueOf(10));
			cfgTerrain.Save();
		}
		return (float) config.getDouble("amplitude");
	}
	
	public int getSeed()
	{
		if(!config.contains("seed"))
		{
			config.set("seed", Integer.valueOf(random.nextInt(seedLength)));
			cfgTerrain.Save();
		}
		return config.getInt("seed");
	}
	
	private Random random;
	private int seed;
	
	public Terrain()
	{
		updateInputs();
		random = new Random();
		seed = getSeed();
	}
	
	public void updateInputs()
	{
		cfgTerrain = new Config("terrain");
		config = cfgTerrain.getConfig();
		AMPLITUDE = getAmplitude();
		OCTAVES = getOctaves();
		ROUGHNESS = getRoughness();
	}
	
	public float generateHeight(int x, int z)
	{
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES - 1);
		for(int i = 0; i < OCTAVES; i++)
		{
			float freq = (float) (Math.pow(2, i) / d);
			float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += getInterpolatedNoise(x * freq, z * freq) * amp;
		}
		return total;
	}
	
	private float interpolate(float a, float b, float blend)
	{
		double theta = blend * Math.PI;
		float f = (float) (1f - Math.cos(theta)) * 0.5f;
		return a * (1f - f) + b * f;
	}
	
	private float getInterpolatedNoise(float x, float z)
	{
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;
		
		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}
	
	private float getSmoothNoise(int x, int z)
	{
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}
	
	private float getNoise(int x, int z)
	{
		random.setSeed(x * 49632 + z * 325176 + seed);
		return random.nextFloat() * 2f - 1f;
	}
}
