package main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static HashMap<Player, Boolean> REGISTED_PLAYER = new HashMap<Player, Boolean>();
	public static Plugin PLUGIN = null;
	FileConfiguration config = null;
	
	@Override
	public void onEnable() {
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld");
		getConfigFile();
		// 온 오프
		// 적용 할 이벤트
		// 특정 아이템만 적용, {whitelist : ""}일 경우 적용안함 (이벤트 등록안함)
		// 특정 아이템만 제외, {itemban : *}일 경우 전체제외 (이벤트 등록안함)
		Bukkit.getPluginManager().registerEvents(new PickupItem(), this);
		Bukkit.getPluginManager().registerEvents(new CreateItem(), this);
		Bukkit.getPluginManager().registerEvents(new BrewPotion(), this);
		Bukkit.getPluginManager().registerEvents(new EnchantItem(), this);
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
		 * 유저 개별 적용 (명령어 포함)
		 * - 아이템 밴
		 * - 적용할 아이템
		 * 이벤트별로 적용하고싶은 아이템 설정?
		 * 타이머
		 * 적용할 이벤트(줍기, / 양조기, / 인첸트테이블, / 작업대, 화로, 모루, 용광로, 숫돌, 석재절단기, 훈연기, 대장장이작업대, 베틀, 지도제작대, 상인, 플레이어작업대)
		 * ㄴ '/' 으로 나눈 것은 클래스(이벤트)로 구분해서 나누기
		 * */
		config.addDefault("dmdk", true);
		config.options().copyDefaults(true);
		saveConfig();
	}
}
