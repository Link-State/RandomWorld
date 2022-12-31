package main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Plugin PLUGIN = null;
	FileConfiguration config = null;
	
	@Override
	public void onEnable() {
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld");
		getConfigFile();
		Bukkit.getPluginManager().registerEvents(new RandomItem(), this);
		getLogger().info("RandomWorld");
	}

	@Override
	public void onDisable() {
		getLogger().info("RandomWorld");
	}
	
	private void getConfigFile() {
		config = this.getConfig();
		/* 
		 * 온 오프
		 * 유저 개별 적용
		 * 아이템 밴
		 * 적용할 아이템
		 * 타이머
		 * 적용할 이벤트(줍기, / 양조기, / 인첸트테이블, / 작업대, 화로, 모루, 용광로, 숫돌, 석재절단기, 훈연기, 대장장이작업대, 베틀, 지도제작대, 상인, 플레이어작업대)
		 * ㄴ '/' 으로 나눈 것은 클래스(이벤트)로 구분해서 나누기
		 * */
		config.addDefault("", true);
		config.options().copyDefaults(true);
		saveConfig();
	}
}
