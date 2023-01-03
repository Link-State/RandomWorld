package main;

import java.io.File;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Plugin PLUGIN;
	public static HashMap<Player, Boolean> REGISTED_PLAYER = new HashMap<Player, Boolean>();
	SimpleConfig config;
	
	@Override
	public void onEnable() {
		getConfigFile();
		// 각 config에서 설정 가져옴
		// 하나라도 없으면 data폴더 삭제 후 다시 getConfigFile()
		
		// 특정 아이템만 적용, {whitelist : ""}일 경우 적용안함 (이벤트 등록안함)
		// 특정 아이템만 제외, {itemban : *}일 경우 전체제외 (이벤트 등록안함)
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld");
//		Bukkit.getPluginManager().registerEvents(new PickupItem(), this);
//		Bukkit.getPluginManager().registerEvents(new BrewPotion(), this);
//		Bukkit.getPluginManager().registerEvents(new EnchantItem(), this);
//		Bukkit.getPluginManager().registerEvents(new CreateItem(), this);
	}
	
	private void getConfigFile() {
		/* 
		 * 온 오프
		 * 유저 개별 적용 (명령어 포함)
		 * 특정 이벤트만을 켰다껐다 하고싶음
		 * 이벤트별로 적용/제외하고싶은 아이템 설정
		 * ㄴ 이벤트 목록 (줍기, / 양조기, / 인첸트테이블, / 작업대, 화로, 모루, 용광로, 숫돌, 석재절단기, 훈연기, 대장장이작업대, 베틀, 지도제작대, 상인, 플레이어작업대)
		 * 타이머
		 * */
		File file = new File(this.getDataFolder().getPath() + "\\config.yml");

		if (!file.exists()) {
			String[] headers = {"사용 방법 : ", "", "플러그인 활성화 여부"};
			SimpleConfigManager manager = new SimpleConfigManager(this);
			config = manager.getNewConfig("config.yml");
			
			config.set("Enable_Plugin", true, headers);
			config.set("Enable_UserNames", "*", "랜덤아이템을 적용 할 플레이어 목록");
			config.set("Enable_Events", "*", "랜덤아이템을 적용 할 이벤트");
			config.set("Pickup", "*", "아이템을 주웠을 때");
			config.set("Brewing_Stand", "*", "물약 제조가 완료됐을 때");
			config.set("Enchant_Table", "*", "마법부여가 완료됐을 때");
			config.set("Workbench", "*", "제작대를 사용했을 때");
			config.set("Furnace", "*", "화로를 사용했을 때");
			config.set("Anvil", "*", "모루를 사용했을 때");
			config.set("Blast_Furnace", "*", "용광로를 사용했을 때");
			config.set("Grind_Stone", "*", "숫돌을 사용했을 때");
			config.set("Stone_Cutter", "*", "석재 절단기를 사용했을 때");
			config.set("Smoker", "*", "훈연기를 사용했을 때");
			config.set("Smithing_Table", "*", "대장장이 작업대를 사용했을 때");
			config.set("Loom", "*", "베틀을 사용했을 때");
			config.set("Cartography", "*", "지도 제작대를 사용했을 때");
			config.set("Merchant", "*", "상인에게서 물건을 구입했을 때");
			config.set("Crafting", "*", "플레이어의 2x2 제작대를 사용했을 때");
			config.saveConfig();
		} else {
			// 각 메뉴 설정 변수에 할당
		}
	}
}
