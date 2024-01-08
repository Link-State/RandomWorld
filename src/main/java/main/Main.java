package main;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Plugin PLUGIN; // 해당 플러그인
	public static HashMap<String, Listener> EVENTS; // 사용할 이벤트 해시맵
	public static HashMap<UUID, RandomEvent> REGISTED_PLAYER; // 랜덤 아이템을 적용할 플레이어 해시맵
	public static SimpleConfigManager MANAGER;
	public static SimpleConfig CONFIG;
	
	// 플러그인 활성화 시,
	@Override
	public void onEnable() {
		boolean isPluginOn = loadConfigFile(); // config파일을 생성 및 불러오고 성공 시 true 반환
		
		
		// 과정
		// 1. 플러그인 켜짐
		// 2. config.yml파일과 userdata폴더 체크 후 없으면 생성, 있으면 userdata의 유저들 static해시맵 변수로 불러오기 후 기본값은 null (키는 uuid, 값은 null)
		// 3. 새로운 유저 입장 시, userdata폴더에 데이터 있는지 체크 후 없으면 새로 생성, 기본 값 저장 후 static변수에 등록, 있으면 불러와서 static변수에 등록
		// 4. 유저 퇴장 시, 해시맵에서 값을 null로 변경
		
		
		// userdata의 정보를 기반으로 RandomEvent객체 생성
		// config파일에는 플러그인 활성화 여부
		// userdata에는 적용할 이벤트, 이벤트 별 바뀌게하지 않을 아이템, 이벤트 별 바꾸지 않을 아이템
		
		// config파일, userdata폴더 체크
		
		if (!isPluginOn)
			return;
		
		// config 파일 불러오기 성공 시
		PLUGIN = Bukkit.getPluginManager().getPlugin("RandomWorld"); // 플러그인 객체
		EVENTS = new HashMap<String, Listener>();
		EVENTS.put("pickup", new PickupItem());
		EVENTS.put("brew", new BrewPotion());
		EVENTS.put("enchant", new EnchantItem());
		EVENTS.put("inventoryclick", new CreateItem());
		REGISTED_PLAYER = new HashMap<UUID, RandomEvent>();
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();

		// 현재 접속 중인 플레이어들
		for (Player p : players) {
			RandomEvent re = new RandomEvent(p);
			REGISTED_PLAYER.put(p.getUniqueId(), re);
		}

		// 1. 이벤트를 아무도 안쓰는 경우 언레지스트기능 구현
		Bukkit.getPluginManager().registerEvents(EVENTS.get("pickup"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("brew"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("enchant"), this);
		Bukkit.getPluginManager().registerEvents(EVENTS.get("inventoryclick"), this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		 먼저 plugin.yml 작성
//		/randomworld enable
//		/randomworld disable
//		/randomworld <이벤트 리스트> - userdata 있는 유저는 제외
//		/randomworld <아이템 리스트> - userdata 있는 유저는 제외
//		/randomworld <이벤트> <아이템 리스트> - userdata 있는 유저는 제외
//		/randomworld <플레이어 이름> <이벤트 리스트> - userdata 생성
//		/randomworld <플레이어 이름> <이벤트> <아이템 리스트> - userdata 생성
//		/randomworld reset <플레이어 이름> - userdata 삭제
//		/randomworld reset * - userdata 모두 삭제
		return super.onCommand(sender, command, label, args);
	}
	
	// 플러그인 온 오프
	private void switchPlugin(boolean b) {
		return;
	}
	
	// 유저 필터링 적용
	private void applyUser(String list) {
		return;
	}
	
	// 이벤트 필터링 적용
	private void applyEvents(String list) {
		// 해당 리스트가 비었으면 전체 등록
		if (list.isEmpty()) {
			
		}
		// 그렇지않다면 일부 등록
		else {
			String[] events = CONFIG.getString("Enable_Events").replaceAll(" ", "").replaceAll(",+", ",").toUpperCase().split(",");
			for (String e : events) {
				if (
						e.equals("WORKBENCH") ||
						e.equals("CRAFTING") ||
						e.equals("FURNACE") ||
						e.equals("BLAST_FURNACE") ||
						e.equals("SMOKER") ||
						e.equals("STONECUTTER") ||
						e.equals("SMITHING") ||
						e.equals("CARTOGRAPHY") ||
						e.equals("LOOM") ||
						e.equals("ANVIL") ||
						e.equals("GRINDSTONE") ||
						e.equals("MERCHANT")
					) {
					// <hashMap>
					// EVENTS.get("InvClick").
					// 	Crafting : item1, item2, ...
					// 	Furnace : item1, item2, ...
					// ...
					// 인벤토리 클릭 이벤트
				} else if (e.equalsIgnoreCase("PICKUP")) {
					// 줍기 이벤트
				} else if (e.equalsIgnoreCase("BREWING")) {
					// 양조 이벤트
				} else if (e.equalsIgnoreCase("ENCHANTING")) {
					// 인첸트 이벤트
				} else {
					
				}
			}
		}
		return;
	}
	
	// 각 이벤트의 아이템 필터링 적용
	private void applyItemBan(String event, String key) {
		return;
	}
	
	// config파일 불러오기
	private boolean loadConfigFile() {
		MANAGER = new SimpleConfigManager(this);
		CONFIG = MANAGER.getNewConfig("config.yml"); // config 파일
		File userdata = new File(this.getDataFolder() + File.separator + "userdata"); // 유저 파일
		
		// 유저파일이 없는 경우 생성
		if (!userdata.exists()) {
			try {
				userdata.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
				userdata = null;
			}
		}

		// config파일 작성
		if (CONFIG.contains("Plugin Enable")) {
			if (!CONFIG.getBoolean("Plugin Enable")) {
				return false;
			}
		}
		
		CONFIG.set("Plugin Enable", true, "플러그인 활성화 여부");
		CONFIG.saveConfig();
		
		// -------------------------------------------------------------
		
		// 각 세팅
		String[] settingName = {
				"Enable_Plugin",
				"Enable_UserNames",
				"Enable_Events",
				"COMMON_EXCEPT",
				"PICKUP_EXCEPT",
				"WORKBENCH_EXCEPT",
				"CRAFTING_EXCEPT",
				"FURNACE_EXCEPT",
				"BLAST_FURNACE_EXCEPT",
				"SMOKER_EXCEPT",
				"BREWING_EXCEPT",
				"STONECUTTER_EXCEPT",
				"SMITHING_EXCEPT",
				"CARTOGRAPHY_EXCEPT",
				"LOOM_EXCEPT",
				"ANVIL_EXCEPT",
				"ENCHANTING_EXCEPT",
				"GRINDSTONE_EXCEPT",
				"MERCHANT_EXCEPT",
				"COMMON_BAN",
				"PICKUP_BAN",
				"WORKBENCH_BAN",
				"CRAFTING_BAN",
				"FURNACE_BAN",
				"BLAST_FURNACE_BAN",
				"SMOKER_BAN",
				"BREWING_BAN",
				"STONECUTTER_BAN",
				"SMITHING_BAN",
				"CARTOGRAPHY_BAN",
				"LOOM_BAN",
				"ANVIL_BAN",
				"ENCHANTING_BAN",
				"GRINDSTONE_BAN",
				"MERCHANT_BAN"
		};
		
		// 각 세팅 설명
		String[][] settingDescript = {
				{"사용 방법 - ${블로그 링크}", "", "플러그인 활성화 여부"},
				{"랜덤아이템을 적용 할 플레이어 목록"},
				{"랜덤아이템을 적용 할 이벤트"},
				{"공통 - 얘로는 바뀌지 않음"},
				{"아이템을 주웠을 때"},
				{"제작대를 사용했을 때"},
				{"플레이어의 2x2 제작대를 사용했을 때"},
				{"화로를 사용했을 때"},
				{"용광로를 사용했을 때"},
				{"훈연기를 사용했을 때"},
				{"물약 제조가 완료됐을 때"},
				{"석재 절단기를 사용했을 때"},
				{"대장장이 작업대를 사용했을 때"},
				{"지도 제작대를 사용했을 때"},
				{"베틀을 사용했을 때"},
				{"모루를 사용했을 때"},
				{"마법부여가 완료됐을 때"},
				{"숫돌을 사용했을 때"},
				{"상인에게서 물건을 구입했을 때"},
				{"공통 - 얘는 안바뀜"},
				{"아이템을 주웠을 때"},
				{"제작대를 사용했을 때"},
				{"플레이어의 2x2 제작대를 사용했을 때"},
				{"화로를 사용했을 때"},
				{"용광로를 사용했을 때"},
				{"훈연기를 사용했을 때"},
				{"물약 제조가 완료됐을 때"},
				{"석재 절단기를 사용했을 때"},
				{"대장장이 작업대를 사용했을 때"},
				{"지도 제작대를 사용했을 때"},
				{"베틀을 사용했을 때"},
				{"모루를 사용했을 때"},
				{"마법부여가 완료됐을 때"},
				{"숫돌을 사용했을 때"},
				{"상인에게서 물건을 구입했을 때"}
		};
		
//		String[][] settingDescript = {
//				{"Manual - ${blog link}", "", "plugin activate"},
//				{"Player List"},
//				{"Event List"},
//				{"when pickup any item"},
//				{"when using workbench"},
//				{"when using crafting table"},
//				{"when using funace"},
//				{"when using blast furnace"},
//				{"when using smoker"},
//				{"when the potion is complete"},
//				{"when using stone cutter"},
//				{"when using smithing table"},
//				{"when using cartography"},
//				{"when using loom"},
//				{"when using anvil"},
//				{"when the item is enchanted"},
//				{"when using grind stone"},
//				{"when buying items from merchant"}
//		};
		
		
		File[] userdatas = userdata.listFiles();
		// 
		
		// 세팅 리스트와 세팅설명 리스트가 같아야 config파일 생성
		if (settingName.length == settingDescript.length) {
			int updateCount = 0; // config 업데이트 횟수
			
			for (int idx = 0; idx < settingName.length; idx++) {
				// config파일에 해당 세팅이 없으면 새로 추가
				if (!CONFIG.contains(settingName[idx])) {
					CONFIG.set(
						settingName[idx],
						settingName[idx].equals("Enable_Plugin") ? true : "",
						settingDescript[idx]
					);
					updateCount++;
				}
			}
			
			// 업데이트가 있었으면 config 저장
			if (updateCount > 0) {
				CONFIG.saveConfig();
				System.out.println("updated " + updateCount + " config setting");
			}
		} else {
			System.out.println("source code err!");
			return false;
		}
		
		return CONFIG.getBoolean("Enable_Plugin");
	}
}
