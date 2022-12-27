package main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static Plugin PLUGIN = null;
	
	@Override
	public void onEnable() {
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld");
		getServer().getPluginManager().registerEvents(new RandomItem(), this);
	}

	@Override
	public void onDisable() {
	}
}
